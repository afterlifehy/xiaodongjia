package com.wbb.xiaodongjia.bean

/**
 * Created by hy on 2021/3/10.
 */
data class PayChannelParamsBean(
    val appid: String,
    val noncestr: String,
    val `package`: String,
    val partnerid: String,
    val prepayid: String,
    val sign: String,
    val timestamp: String
)