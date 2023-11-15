package com.wbb.base.emuns

/**
 * Created by zj on 2021/3/3.
 */
enum class WEIXIN_SHAR_TYPE(var type: Int, var description: String = "") {
    TEXT(1, "文本"),
    IMAGE(2, "图片"),
    GRAPHIC(3, "图文")
}