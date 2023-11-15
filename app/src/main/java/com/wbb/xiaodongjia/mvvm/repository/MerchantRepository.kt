package com.wbb.xiaodongjia.mvvm.repository

import com.wbb.base.bean.HttpWrapper
import com.wbb.base.bean.MerchantInfo
import com.wbb.base.bean.PaginationInfo
import com.wbb.base.mvvm.base.BaseRepository

/**
 * Created by zj on 2021/2/25.
 * 商户相关
 */
class MerchantRepository : BaseRepository() {
    /**
     * 直接推荐间接推荐
     */
    suspend fun getMerchantSearchList(par: Map<String, String>): HttpWrapper<PaginationInfo<MerchantInfo>> {
        return mMerchantServe.getMerchantSearchList(par)
    }
}