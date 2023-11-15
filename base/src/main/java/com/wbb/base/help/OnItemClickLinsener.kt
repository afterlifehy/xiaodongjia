package com.wbb.base.help

/**
 * Created by zj on 2021/2/27.
 * 用来处理adapter点击事件
 */
interface OnItemClickLinsener<T> {
    fun onItemClick(postion: Int, mInfo: T?)
}