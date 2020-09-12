package org.fknives.rstocklist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import org.fknives.rstocklist.appsync.SyncService

class NotificationService : Service(), SyncService.EventListener {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.start_service_channel_title)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            mChannel.description = name
            mChannel.enableLights(true)
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(mChannel)
        }

        updateNotification("Click Me When on Ticker List Screen!")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getBooleanExtra(SYNC_STARTED, false)?.takeIf { it }?.let {
            updateNotification("Syncing ...")
            SyncService.listener = this
            SyncService.start()
        }
        intent?.getIntExtra(PROGRESS, -1)?.takeIf { it >= 0 }?.let {
            updateNotification("Processed: $it, syncing ...")
        }
        return START_NOT_STICKY
    }

    private fun updateNotification(text: String) {
        val intent = Intent(this, NotificationService::class.java)
            .putExtra(SYNC_STARTED, true)
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Start syncing on Stock List")
            .setContentText(text)
            .setContentIntent(PendingIntent.getService(this, 0, intent, 0))
            .build()

        startForeground(1, notification)
    }


    override fun onBind(intent: Intent?): IBinder? = null

    override fun onItemProcessed(index: Int) {
        updateNotification("Processed: ${index + 1}, syncing ...")
    }

    override fun onItemProcessingFinished(items: List<String>) {
        FileManager(this).saveTickers(items)
        updateNotification("Processed: ${items.size}.")

        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        SyncService.stop()
    }

    companion object {
        fun getStartIntent(context: Context): Intent =
            Intent(context, NotificationService::class.java)

        const val NOTIFICATION_CHANNEL_ID = "START_SERVICE_ID"
        const val SYNC_STARTED = "SYNC_STARTED"
        const val PROGRESS = "PROGRESS"
    }

}