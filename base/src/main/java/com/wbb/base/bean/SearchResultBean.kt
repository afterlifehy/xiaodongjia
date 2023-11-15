package com.wbb.base.bean

/**
 * Created by hy on 2021/2/27.
 */
data class SearchResultBean(
    val address: String,
    val author: String,
    val endTime: Long,
    val id: String,
    val img: String,
    val labels: List<Labels>,
    val latitude: Double,
    val longitude: Double,
    val searchType: String,
    val minutes: String,
    val name: String,
    val startTime: Long,
    val vipNum: Int
)

data class Labels(
    val bgColor: String,
    val color: String,
    val labelId: Int,
    val labelName: String
)