package com.wbb.xiaodongjia.mvvm.repository

import com.wbb.base.mvvm.base.BaseRepository
import com.wbb.base.bean.HttpWrapper
import com.wbb.base.bean.PaginationInfo
import com.wbb.base.bean.LifeCircleActivityBean

/**
 * Created by hy on 2021/2/24.
 */
class LifeCircleRepository : BaseRepository() {

    suspend fun activityList(par: Map<String, Any>): HttpWrapper<List<LifeCircleActivityBean>> {
        return mMerchantServe.activityList(par)
    }
}