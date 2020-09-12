package org.fknives.rstocklist.appsync

import android.view.accessibility.AccessibilityNodeInfo

abstract class TraverseRecyclerView(private var index: Int) {

    fun next(recyclerView: AccessibilityNodeInfo) {
        val collectionRowCount = recyclerView.collectionInfo?.rowCount ?: return
        if (collectionRowCount <= index) {
            return finished()
        }

        val found = recyclerView.children
            .firstOrNull { it.getRefreshedFlattenIndex() == index }
        when {
            found == null
                    && recyclerView.getSmallestFlatIndexOfChildren(index) > index
                    && recyclerView.canScrollBackward() -> {
                recyclerView.scrollBackward()
            }
            found == null && recyclerView.canScrollForward() -> {
                recyclerView.scrollForward()
            }
            found == null && index == collectionRowCount - 1 -> {
                finished()
            }
            found == null -> {
                if (recyclerView.canScrollBackward()) {
                    recyclerView.scrollBackward()
                } else {
                    finished()
                }
            }
            else -> {
                index++
                found(found)
            }
        }
    }

    protected abstract fun found(accessibilityNodeInfo: AccessibilityNodeInfo)

    protected abstract fun finished()

    companion object {

        private fun AccessibilityNodeInfo.getRefreshedFlattenIndex(): Int? {
            val maxColumnCount = parent.collectionInfo?.columnCount ?: return null
            return apply { refresh() }
                .collectionItemInfo
                ?.getFlattenIndex(maxColumnCount)
        }

        private fun AccessibilityNodeInfo.getSmallestFlatIndexOfChildren(default: Int): Int =
            children.mapNotNull { it.getFlattenIndex() }
                .minOrDefault(default)

        private val AccessibilityNodeInfo.children
            get() = (0 until childCount).mapNotNull(::getChild)

        private fun AccessibilityNodeInfo.getFlattenIndex(): Int? {
            val maxColumnCount = parent.collectionInfo?.columnCount ?: return null
            return collectionItemInfo?.getFlattenIndex(maxColumnCount)
        }

        private fun AccessibilityNodeInfo.CollectionItemInfo.getFlattenIndex(maxColumnCount: Int) =
            rowIndex

        private fun <T : Comparable<T>> List<T>.minOrDefault(default: T) = minOrNull() ?: default

        private fun AccessibilityNodeInfo.scrollBackward(delay: Long = 500): Boolean =
            performActionThenDelayIfOk(delay) {
                performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD.id)
            }

        private fun AccessibilityNodeInfo.scrollForward(delay: Long = 500): Boolean =
            performActionThenDelayIfOk(delay) {
                performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD.id)
            }

        private inline fun performActionThenDelayIfOk(delay: Long, action: () -> Boolean): Boolean {
            val result = action()
            if (result) {
                Thread.sleep(delay)
            }
            return result
        }

        private fun AccessibilityNodeInfo.canScrollBackward(): Boolean =
            canDo(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD.id)

        private fun AccessibilityNodeInfo.canScrollForward(): Boolean =
            canDo(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD.id)

        private fun AccessibilityNodeInfo.canDo(id: Int): Boolean =
            actionList?.any { it.id == id } == true
    }
}