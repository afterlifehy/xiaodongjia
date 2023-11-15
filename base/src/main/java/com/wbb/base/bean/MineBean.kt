package com.wbb.base.bean

/**
 * Created by hy on 2021/2/24.
 */
data class MineBean(
    val balance: String,
    val commNum: String,
    val icon: String,
    val mid: String,
    val nickName: String,
    val recNum: String,
    val recommend: String,
    val plusBalance: String,
    val urls: List<String>,
    val utils: List<CommenTools>
)

data class CommenTools(
    val appUrl: String,
    val h5Url: String,
    val img: String,
    val name: String
)