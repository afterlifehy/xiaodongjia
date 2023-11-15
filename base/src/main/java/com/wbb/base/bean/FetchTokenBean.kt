package com.wbb.base.bean

/**
 * Created by hy on 2021/3/4.
 */
data class FetchTokenBean(
    val accessKey: String,
    val dir: String,
    val expire: String,
    val host: String,
    val policy: String,
    val signature: String,
    val endpoint:String,
    val bucket:String
)