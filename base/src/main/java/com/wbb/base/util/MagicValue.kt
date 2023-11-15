package com.wbb.base.util

import com.wbb.base.BuildConfig


/**
 * Created by hy on 2021/2/1.
 */
object MagicValue {
    const val ZH = "\"zh_CN\""
    const val EN = "\"en_US\""
    const val SEARCH_TYPE_ALL = "ALL"
    const val SEARCH_TYPE_BUSINESS = "MERCHANT"
    const val SEARCH_TYPE_CLASS = "COURSE"
    const val SEARCH_TYPE_ACTIVITY = "ACTIVITY"

    var PLATFORM = when (2) {
        1 -> {
            "google"
        }
        2 -> {
            "local"
        }
        3 -> {
            "inside"
        }
        else -> {
            ""
        }
    }

    @JvmField
    var WBID: String = PLATFORM + "@wb_android_"
}