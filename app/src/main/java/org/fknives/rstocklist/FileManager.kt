package org.fknives.rstocklist

import android.content.Context
import androidx.annotation.MainThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.io.File

class FileManager private constructor(private val context: Context) {

    private val fileDir get() = context.cacheDir
    private var last = 0
    private val stateFlow = MutableStateFlow(last)
    val tickersWithLastLoadedAtFlow = stateFlow.map { loadTickersAndLastUpdatedAt() }

    fun saveTickers(tickers: List<String>) {
        context.cacheDir.listFiles()?.forEach { it.delete() }
        val fileIntoSave = File(context.cacheDir, "$STATIC_PART_OF_FILENAME${System.currentTimeMillis()}$FILE_EXTENSION")
        fileIntoSave.writeText(tickers.firstOrNull().orEmpty())
        tickers.drop(1).forEach {
            fileIntoSave.appendText(",$it")
        }
        last = (last + 1) % 2
        stateFlow.value = last
    }

    private fun loadTickersAndLastUpdatedAt(): Pair<Long, List<String>>? =
        lastFile()?.let {
                it.getTimestampFromFile() to it.readText().split(",")
            }

    private fun File.getTimestampFromFile(): Long =
        name.drop(STATIC_PART_OF_FILENAME.length).dropLast(FILE_EXTENSION.length).toLongOrNull() ?: 0L

    fun lastFile(): File? =
        fileDir.listFiles()
            ?.filter { it.name.contains(STATIC_PART_OF_FILENAME) }
            ?.maxByOrNull { it.getTimestampFromFile() }

    companion object {
        private const val STATIC_PART_OF_FILENAME = "stock_list_"
        private const val FILE_EXTENSION = ".csv"
        private var fileManager: FileManager? = null

        @MainThread
        operator fun invoke(context: Context): FileManager  =
            fileManager ?: FileManager(context.applicationContext).also { fileManager = it }
    }
}