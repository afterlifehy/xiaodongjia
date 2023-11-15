package com.wbb.base.bean

/**
 * Created by hy on 2021/2/23.
 */
data class RecommendListBean(
    val icon: String?,
    val incomeTotal: String?,
    val incomeTotalUnit:String?,
    val phone: String?,
    val recommend: String?,
    val times: Long = 0,
    val uid: String?
)