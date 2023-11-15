package com.wbb.xiaodongjia.mvvm.repository

import com.wbb.base.bean.*
import com.wbb.base.mvvm.base.BaseRepository

/**
 * Created by hy on 2021/2/23.
 */
class UserRepository : BaseRepository() {

    /**
     * 直接推荐间接推荐
     */
    suspend fun getRecommendList(par: Map<String, Any?>): HttpWrapper<List<RecommendListBean>> {
        return mPortalServe.getRecommendList(par)
    }

    /**
     * 保存用户昵称
     */
    suspend fun saveNameRecommend(par: Map<String, String>): HttpWrapper<String> {
        return mPortalServe.saveNameRecommend(par)
    }

    /**
     * 我的
     */
    suspend fun userInfoView(): HttpWrapper<MineBean> {
        return mPortalServe.userInfoView()
    }

    /**
     * 获取当前推荐关系是否同步
     */
    suspend fun getLevelCount(): HttpWrapper<Boolean> {
        return mPortalServe.getLevelCount()
    }

    /**
     * 卡包列表
     */
    suspend fun getMemberCard(): HttpWrapper<List<CardPackageBean>> {
        return mPortalServe.getMemberCard()
    }

    /**
     * 获取基础数据配置
     */
    suspend fun getConfigSet(par: Map<String, String>): HttpWrapper<CityChildItemInfo> {
        return mBisisServer.getConfigSet(par)
    }

    /**
     * app更新
     */
    suspend fun appVersion(): HttpWrapper<UpdateBean> {
        return mPortalServe.appVersion()
    }
}