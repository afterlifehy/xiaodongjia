package com.wbb.base.help

import android.content.Intent
import com.alibaba.android.arouter.launcher.ARouter
import com.wbb.base.util.ARouterMap
import com.wbb.base.util.Constant

/**
 * Created by zj on 2021/3/1.
 */
object AppToH5Manager {
    /**
     * 跳转到h5的商户详情
     */
    fun toMerchantDetails(merchantStoreId: String) {
        ARouter.getInstance().build(ARouterMap.WEBVIEW).withString(
            ARouterMap.URL,
            Constant.MERCHANT_DETAIL + "?id=${merchantStoreId}"
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .navigation()
    }

    /**
     * 跳转到活动详情
     */
    fun toActivityDetails(id: String) {
        ARouter.getInstance().build(ARouterMap.WEBVIEW)
            .withString(ARouterMap.URL, Constant.ACTIVITY_DETAIL + "?id=${id}")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .navigation()
    }
}