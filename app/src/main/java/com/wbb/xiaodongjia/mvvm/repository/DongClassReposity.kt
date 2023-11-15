package com.wbb.xiaodongjia.mvvm.repository

import com.wbb.base.bean.*
import com.wbb.base.mvvm.base.BaseRepository

/**
 * Created by hy on 2021/3/8.
 */
class DongClassReposity : BaseRepository() {

    /**
     * 课程搜索
     */
    suspend fun courseTypeSearch(par: Map<String, Any>): HttpWrapper<PaginationInfo<DongClassBean>> {
        return mMerchantServe.courseTypeSearch(par)
    }

    /**
     * 获取课程列表
     */
    suspend fun courseList(par: Map<String, String>): HttpWrapper<PaginationInfo<VideoPlayInfo>> {
        return mMerchantServe.courseList(par)
    }

    /**
     * 获取课程详情
     */
    suspend fun getCourseDetail(par: Map<String, String>): HttpWrapper<VideoPlayInfo> {
        return mMerchantServe.getCourseDetail(par)
    }

}