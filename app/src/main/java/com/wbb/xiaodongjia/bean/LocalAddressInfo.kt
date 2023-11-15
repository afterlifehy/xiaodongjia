package com.wbb.xiaodongjia.bean

/**
 * Created by zj on 2021/2/26.
 * 城市
 */
data class LocalAddressInfo(
    var cityName: String,
    var cityId: String = "",
    var originCityId: String = ""
)
