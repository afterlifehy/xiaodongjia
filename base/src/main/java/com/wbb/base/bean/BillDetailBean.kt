package com.wbb.base.bean

/**
 * Created by hy on 2021/2/26.
 */
data class BillDetailBean(
    val amount: String,
    val payDetails: List<PayDetail>,
    val status: Int,
    val tips: String
)

data class PayDetail(
    val name: String,
    val value: String
)