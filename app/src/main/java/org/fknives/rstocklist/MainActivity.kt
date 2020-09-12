package org.fknives.rstocklist

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import org.fknives.rstocklist.appsync.SyncService
import org.fknives.rstocklist.databinding.ActivityMainBinding
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fileManager = FileManager(this)

        binding.startServiceCta.setOnClickListener {
            if (SyncService.canStart()) {
                startService(NotificationService.getStartIntent(this))
            } else {
                startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                )
            }
        }

        binding.shareFileCta.setOnClickListener {
            val file = fileManager.lastFile() ?: return@setOnClickListener
            val uri = FileProvider.getUriForFile(
                this,
                "org.fknives.rstocklist.fileprovider",
                file
            )
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/*"
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(sharingIntent, resources.getText(R.string.send_to)))
        }

        val adapter = TickerAdapter()
        binding.recycler.adapter = adapter

        lifecycleScope.launchWhenCreated {
            fileManager.tickersWithLastLoadedAtFlow.collect {
                binding.lastUpdatedAt.isVisible = it != null
                binding.shareFileCta.isVisible = it != null
                it?.first?.let(SimpleDateFormat("YYYY-MM-dd hh:mm")::format)
                    ?.let { date -> getString(R.string.file_last_updated_at, date) }
                    ?.let(binding.lastUpdatedAt::setText)
                adapter.submitList(it?.second.orEmpty())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(NotificationService.getStartIntent(this))
        SyncService.stop()
    }
}