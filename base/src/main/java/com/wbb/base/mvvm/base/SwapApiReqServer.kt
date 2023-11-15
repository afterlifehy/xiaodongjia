package com.chouyou.base.mvvm.base

import retrofit2.http.*

/**
 * Created by hy on 2020/11/26.
 */
interface SwapApiReqServer {


    /**
     * 币种缩略行情
     */
    @FormUrlEncoded
    @POST("market/symbol-thumb")
    suspend fun marketSysbolThumb(@FieldMap params: Map<String, String>): String


}