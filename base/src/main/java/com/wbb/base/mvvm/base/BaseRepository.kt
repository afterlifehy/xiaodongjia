package com.wbb.base.mvvm.base

import com.wbb.base.bean.ResResponse
import com.wbb.base.http.RetrofitUtils


open class BaseRepository {

    val mPortalServe by lazy {
        RetrofitUtils.getInstance().createCoroutineRetrofit(
            Api::class.java,
            UrlManager.getPortalUrl()
        )
    }
    val mMerchantServe by lazy {
        RetrofitUtils.getInstance().createCoroutineRetrofit(
            Api::class.java,
            UrlManager.getMerchantPortalUrl()
        )
    }
    val mBisisServer by lazy {
        RetrofitUtils.getInstance().createCoroutineRetrofit(
            Api::class.java,
            UrlManager.getBasisUrl()
        )
    }

    suspend fun <T : Any> apiCall(call: suspend () -> ResResponse<T>): ResResponse<T> {
        return call.invoke()
    }

}