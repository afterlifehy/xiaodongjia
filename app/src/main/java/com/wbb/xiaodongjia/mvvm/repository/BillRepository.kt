package com.wbb.xiaodongjia.mvvm.repository

import com.wbb.base.bean.*
import com.wbb.base.mvvm.base.BaseRepository

/**
 * Created by hy on 2021/2/24.
 */
class BillRepository : BaseRepository() {

    suspend fun getOrder(par: Map<String, Any>): HttpWrapper<PaginationInfo<BillListBean>> {
        return mPortalServe.getOrder(par)
    }

    suspend fun getOrderDetail(par: Map<String, String>): HttpWrapper<BillDetailBean> {
        return mPortalServe.getOrderDetail(par)
    }

    /**
     * 获取买单页余额
     */
    suspend fun getPay(par: Map<String, String>): HttpWrapper<GetPayBalanceBean> {
        return mPortalServe.getPay(par)
    }

    /**
     *下单并支付
     */
    suspend fun addOrderAndPay(par: Map<String, Any>): HttpWrapper<PayResultBean> {
        return mPortalServe.addOrderAndPay(par)
    }

    /**
     * 微信下单
     */
    suspend fun addOrder(par: Map<String, Any>): HttpWrapper<BillListBean> {
        return mPortalServe.addOrder(par)
    }

    /**
     * 查询支付结果
     */
    suspend fun getPayStatus(par: Map<String, String>): HttpWrapper<PayStatusBean> {
        return mPortalServe.getPayStatus(par)
    }
}