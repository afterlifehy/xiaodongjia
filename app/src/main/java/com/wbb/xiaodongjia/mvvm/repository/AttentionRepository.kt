package com.wbb.xiaodongjia.mvvm.repository

import com.wbb.base.mvvm.base.BaseRepository
import com.wbb.base.bean.HttpWrapper
import com.wbb.base.bean.MerchantFolownfo
import com.wbb.base.bean.PaginationInfo

/**
 * Created by zj on 2021/2/24.
 */
class AttentionRepository : BaseRepository() {
    /**
     * 短信验证码登录
     */
    suspend fun getFollowList(login: Map<String, String>): HttpWrapper<PaginationInfo<MerchantFolownfo>> {
        return mPortalServe.getFollowList(login)
    }

    /**
     * 添加关注
     */
    suspend fun addFollow(login: Map<String, String>): HttpWrapper<String> {
        return mPortalServe.addFollow(login)
    }

    /**
     * 关注搜索
     */
    suspend fun getIsFollowList(login: Map<String, String>): HttpWrapper<String> {
        return mPortalServe.getIsFollowList(login)
    }
}