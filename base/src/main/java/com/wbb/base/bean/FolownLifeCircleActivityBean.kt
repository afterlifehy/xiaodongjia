package com.wbb.base.bean

/**
 * Created by hy on 2021/2/24.
 */
data class FolownLifeCircleActivityBean(
    val activityId: String,
    val attend: Boolean,
    val attendEndTime: String,
    val attendIcons: List<String>,
    val attendNum: Int,
    val attendStartTime: String,
    val bannerUrl: String,
    val remainNum: Int,
    val title: String,
    val createTime: String,
    val endTime: String,
    val startTime: String,
    val areaName: String,
    val cityName: String,
    var createTimeLong: Long = 0L,
    var activityImgList: List<ActivityImgList>? = null,
    var address: String = ""
)

data class ActivityImgList(var imgPath: String = "")

