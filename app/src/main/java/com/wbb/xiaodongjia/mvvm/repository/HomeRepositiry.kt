package com.wbb.xiaodongjia.mvvm.repository

import com.wbb.base.bean.*
import com.wbb.xiaodongjia.roomdao.bean.MsgDataInfo
import com.wbb.base.mvvm.base.BaseRepository

/**
 * Created by zj on 2021/2/24.
 */
class HomeRepositiry : BaseRepository() {
    /**
     * 商家分类
     */
    suspend fun getCategory(par: Map<String, Any>): HttpWrapper<MutableList<HomeMenuInfo>> {
        return mMerchantServe.getCategory(par)
    }

    /**
     * 获取已开通城市列表
     */
    suspend fun getOpenCity(): HttpWrapper<MutableList<CityItemInfo>> {
        return mMerchantServe.getOpenCity()
    }

    /**
     * 获取所有城市
     */
    suspend fun getAllSort(): HttpWrapper<MutableList<CityItemInfo>> {
        return mBisisServer.getAllSort()
    }

    /**
     * 获取首页Wbb
     */
    suspend fun getProfit(): HttpWrapper<ProfitInfo> {
        return mBisisServer.getProfit()
    }

    /**
     * 获取首页Wbb
     */
    suspend fun getSourceMaterial(par: Map<String, String>): HttpWrapper<List<CommenTools>> {
        return mBisisServer.getSourceMaterial(par)
    }

    /**
     * 获取新消息内容
     */
    suspend fun getMineMsgPage(par: Map<String, String>): HttpWrapper<PaginationInfo<MsgData>> {
        return mBisisServer.getMineMsgPage(par)
    }

    /**
     * 用户端查询商户详情
     */
    suspend fun merchantStoreDetail(par:Map<String,String>):HttpWrapper<MerchantDetailBean>{
        return mMerchantServe.merchantStoreDetail(par)
    }
}