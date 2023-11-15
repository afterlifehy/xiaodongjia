package com.wbb.base.bean

/**
 * Created by hy on 2021/3/9.
 */
data class PayResultBean(
    val id: String,
    val payCaller: String,
    val payChannelParams: String,
    val payOrderNo: String,
    val paymentStatus: Int
)