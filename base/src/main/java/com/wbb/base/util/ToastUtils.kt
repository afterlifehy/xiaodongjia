package com.wbb.base.util

import android.os.Handler
import android.os.Looper
import com.wbb.base.R

/**
 * Created by zj on 2021/1/28.
 */
object ToastUtils {
    fun showToash(msg: String?) {
        showToash(msg, -1)
    }

    fun showToash(msg: String?, icon: Int = -1) {
        val mView = ToastViewManager.get().getTostView(msg!!, icon)
        val mTost = ToastViewManager.get().getToast()
        mTost.view = mView
        mTost.show()
        Handler(Looper.getMainLooper()).postDelayed({ mTost.cancel() }, 1000)
    }

    /**
     * 成功消息提示
     */
    fun showSucessToast(msg: String?) {
        msg?.let {
            showToash(it, R.mipmap.tty_icon)
        }
    }

    /**
     * 成功消息提示
     */
    fun showErrorToast(msg: String?) {
        msg?.let {
            showToash(it, R.mipmap.toast_icon_warning)

        }
    }
}