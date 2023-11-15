package com.wbb.xiaodongjia.base

/**
 * Created by zj on 2021/2/26.
 */
data class HomeUpDataInfo(
    var mTypeCode: String = "", var data: Any? = null
) {
    companion object {
        const val UP_ADDRESS = "up_address"
    }
}