package com.wbb.base.bean

/**
 *  Created by zhangqi on 2019/4/22.
 */
data class HttpWrapper<out T>(val code: Int, val msg: String, val data: T)