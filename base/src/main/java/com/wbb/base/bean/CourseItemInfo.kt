package com.wbb.base.bean

/**
 * Created by zj on 2021/3/11.
 */
data class CourseItemInfo(
    var author: String = "",
    var courseId: String = "",
    var courseName: String = "",
    var courseTypeId: String = "",
    var createTime: Long = 0L,
    var imgPath: String = "",
    var minutes: Int = 0,
    var sort: Int = 0,
    var videoPath: String = ""

)
