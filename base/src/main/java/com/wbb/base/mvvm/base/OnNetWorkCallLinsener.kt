package com.wbb.base.mvvm.base

import java.lang.Exception

/**
 * Created by zj on 2021/3/5.
 */
interface OnNetWorkCallLinsener {
    fun onNewWorkErrorCall(tag: String, ext: Exception?)
}