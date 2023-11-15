package com.wbb.base.bean

import java.io.Serializable

data class VideoPlayInfo(
    var author: String,
    var courseId: String,
    var sort: Int,
    var courseName: String,
    var follow: Boolean = false,
    var minutes: Int = 0,
    var videoPath: String = ""

) : Serializable {
}