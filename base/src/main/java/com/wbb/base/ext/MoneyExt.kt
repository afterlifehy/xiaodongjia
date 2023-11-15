package com.wbb.base.ext

import java.text.DecimalFormat


// 转万元保留两位小数
fun String.toWan00(): String{
    return (this.toFloat() / 10000).m2()
}

// 转万元不保留两位小数
fun String.toWan(): String{
    return (this.toFloat() / 10000).toInt().toString()
}

/**
 * 保留两个小数点
 */
fun Float.m2(): String {
    val df = DecimalFormat("#.00")
    val d = df.format(this)
    return when {
        d == ".00" -> "0.00"
        d.substring(0, 1) == "." -> "0$d"
        else -> d
    }
}

