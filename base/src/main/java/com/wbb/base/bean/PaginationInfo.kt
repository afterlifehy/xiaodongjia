package com.wbb.base.bean

/**
 * Created by zj on 2021/2/25.
 * 分页
 */
data class PaginationInfo<T>(var page: Int = 0, var total: Int = 0, var list: MutableList<T>)
