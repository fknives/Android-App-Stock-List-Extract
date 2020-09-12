package org.fknives.rstocklist.appsync

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import org.fknives.rstocklist.BuildConfig

class SyncService : AccessibilityService() {

    private var traverseRecyclerView: TraverseRecyclerView? = null
    private val parseTicker = ParseTicker()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (syncState == SyncState.STOP){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                disableSelf()
            }
            return
        }
        synchronized(this) {
            isStarted = true
            if (syncState == SyncState.NOT_STARTED) return
            if (syncState == SyncState.RESET) {
                syncState = SyncState.WORKING
                traverseRecyclerView = object : TraverseRecyclerView(0) {
                    val tickers = mutableListOf<String>()

                    override fun found(accessibilityNodeInfo: AccessibilityNodeInfo) {
                        parseTicker(accessibilityNodeInfo)?.let(tickers::add)
                        listener?.onItemProcessed(tickers.size - 1)
                    }

                    override fun finished() {
                        syncState = SyncState.NOT_STARTED
                        listener?.onItemProcessingFinished(tickers)
                    }

                }
            }
            val recycler = findRecycler() ?: return
            traverseRecyclerView?.next(recycler)
        }
    }

    override fun onInterrupt() {
        isStarted = false
    }

    private fun findRecycler() =
        RECYCLER_VIEW_IDS.asSequence().mapNotNull {
            rootInActiveWindow.findAccessibilityNodeInfosByViewId(it)?.firstOrNull()
        }.firstOrNull()

    enum class SyncState {
        RESET, WORKING, NOT_STARTED, STOP
    }

    interface EventListener {
        fun onItemProcessed(index: Int)

        fun onItemProcessingFinished(items: List<String>)
    }

    companion object {

        private val RECYCLER_VIEW_IDS = listOf(
            BuildConfig.CONFIG_RECYCLER_ID1,
            BuildConfig.CONFIG_RECYCLER_ID2
        )

        private var isStarted: Boolean = false
        private var syncState: SyncState = SyncState.NOT_STARTED
        var listener : EventListener? = null

        fun start() {
            syncState = SyncState.RESET
        }

        fun stop() {
            syncState = SyncState.STOP
        }

        fun canStart(): Boolean = isStarted
    }
}