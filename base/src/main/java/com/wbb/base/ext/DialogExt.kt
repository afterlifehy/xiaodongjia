package com.wbb.base.ext

import android.app.Dialog
import android.text.TextUtils
import com.wbb.base.BaseApplication

fun Dialog.i18N(id: Int): String {
    return BaseApplication.instance().resources.getString(id)
}

fun Dialog.isEmpty(value: String): Boolean {
    return TextUtils.isEmpty(value)
}