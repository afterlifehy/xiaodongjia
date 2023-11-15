package com.wbb.base.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context


/**
 * Created by zj on 2020/8/14.
 */
object CopyUtils {
    /**
     * 复制到粘贴板
     */
    fun strToCopy(conent: Context, str: String) {
        val clipData = ClipData.newPlainText("", str)
        (conent.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
            clipData
        )

    }
}