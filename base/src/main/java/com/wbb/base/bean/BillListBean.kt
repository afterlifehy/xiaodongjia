package com.wbb.base.bean

/**
 * Created by hy on 2021/2/24.
 */
data class BillListBean(
    val amount: String,
    val headIcon:String,
    val merchantImg:String,
    val merchantName: String,
    val orderId: String,
    val orderNo: String,
    val payTime: Long,
    val payType: String,
    val status: String,
    val isRecharge:Boolean
)