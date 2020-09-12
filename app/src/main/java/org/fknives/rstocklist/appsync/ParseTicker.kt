package org.fknives.rstocklist.appsync

import android.view.accessibility.AccessibilityNodeInfo
import org.fknives.rstocklist.BuildConfig

class ParseTicker {

    operator fun invoke(accessibilityNodeInfo: AccessibilityNodeInfo): String? =
        if (accessibilityNodeInfo.isStockViewGroup()) {
            accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(BuildConfig.CONFIG_COMPANY_TICKER_ID)
                .firstOrNull()
                ?.text
                ?.toString()
        } else {
            null
        }

    private fun AccessibilityNodeInfo.isStockViewGroup(): Boolean =
        companyIds.all { hasChildWithId(it) }

    companion object {
        private val companyIds = listOf(
            BuildConfig.CONFIG_COMPANY_IMG_ID,
            BuildConfig.CONFIG_COMPANY_NAME_ID,
            BuildConfig.CONFIG_COMPANY_TICKER_ID,
            BuildConfig.CONFIG_COMPANY_SHARE_PRICE_ID,
            BuildConfig.CONFIG_COMPANY_CHANGE_PERCENT_ID
        )


        private fun AccessibilityNodeInfo.hasChildWithId(id: String): Boolean =
            findAccessibilityNodeInfosByViewId(id)?.isNotEmpty() == true
    }
}