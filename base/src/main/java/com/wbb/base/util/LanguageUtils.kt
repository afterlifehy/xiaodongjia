package com.wbb.base.util

import com.wbb.base.BaseApplication


/**
 * Created by zj on 2020/11/25.
 */
object LanguageUtils {
    fun getString(strId: Int): String {
        return BaseApplication.instance().resources!!.getString(strId)
    }
}