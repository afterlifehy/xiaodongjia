package com.wbb.base.bean

/**
 * Created by hy on 2021/2/24.
 */
data class LifeCircleActivityBean(
    val activityId: String,
    val attend: Boolean,
    val attendEndTime: Long,
    val attendIcons: List<String>,
    val attendNum: Int,
    val attendStartTime: Long,
    val bannerUrl: String,
    val remainNum: Int,
    val title: String
)