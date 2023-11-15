package com.wbb.base.network

/**
 * Created by zj on 2021/2/20.
 */
interface OnNetWorkViewShowLinsener {
    /**
     * 当前是否有网络
     */
    fun onCurrentNewWorkState(isNetWork: Boolean)
}