package com.wbb.xiaodongjia.mvvm.repository

import com.wbb.base.bean.HttpWrapper
import com.wbb.base.bean.PaginationInfo
import com.wbb.base.bean.SearchCountBean
import com.wbb.base.bean.SearchResultBean
import com.wbb.base.mvvm.base.BaseRepository

/**
 * Created by hy on 2021/2/27.
 */
class SearchRepository: BaseRepository() {

    /**
     * 搜索数目统计
     */
    suspend fun count(par: Map<String, String>): HttpWrapper<SearchCountBean> {
        return mMerchantServe.count(par)
    }

    /**
     * 课程活动商户搜索
     */
    suspend fun searchAll(par: Map<String, Any>): HttpWrapper<PaginationInfo<SearchResultBean>> {
        return mMerchantServe.searchAll(par)
    }

    /**
     * 获取热门搜索
     */
    suspend fun getKeyList(par: Map<String, String>):String{
        return mPortalServe.getKeyList(par)
    }
}