package com.wbb.base.bean

/**
 * Created by hy on 2021/3/23.
 */
data class UpdateBean(
    val appType: String,
    val appVesion: String,
    val channel: String,
    val content: String,
    val forceUpdate: Int,
    val id: Int,
    val packageName: String,
    val url: String
)