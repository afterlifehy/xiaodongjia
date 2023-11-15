package com.chouyou.waterbridge.mvvm.base

/**
 * Created by zj on 2020/11/11.
 * 用来返回错误信息
 */
data class ErrorMessage(
    var msg: String,
    var type: String = "",
    var code: Int = 0,
    var data: Any? = null
) {
}