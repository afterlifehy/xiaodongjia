package com.wbb.xiaodongjia.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.wbb.base.bean.CityItemInfo

/**
 * Created by zj on 2021/2/25.
 */
data class CityInfo(
    var mType: Int = 1,
    var mCityItemInfo: CityItemInfo? = null
) : MultiItemEntity {
    override val itemType: Int
        get() = mType
}
