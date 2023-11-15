package com.wbb.xiaodongjia.mvvm.repository

import com.wbb.base.bean.HttpWrapper
import com.wbb.base.bean.RechargeResultBean
import com.wbb.base.mvvm.base.BaseRepository

/**
 * Created by hy on 2021/3/10.
 */
class WebviewRepository: BaseRepository() {

    /**
     *plus充值
     */
    suspend fun initRecharge(par: Map<String, String>): HttpWrapper<RechargeResultBean> {
        return mPortalServe.initRecharge(par)
    }
}