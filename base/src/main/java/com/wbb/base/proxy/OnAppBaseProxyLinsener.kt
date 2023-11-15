package com.wbb.base.proxy

/**
 * Created by zj on 2021/4/1.
 */
interface OnAppBaseProxyLinsener {
    fun onIsProxy(): Boolean
    fun onIsDebug(): Boolean
}