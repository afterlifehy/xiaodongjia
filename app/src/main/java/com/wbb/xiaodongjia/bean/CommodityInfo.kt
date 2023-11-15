package com.wbb.xiaodongjia.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * Created by zj on 2021/1/26.
 */
data class CommodityInfo(var type: Int = 0, var name: String = "") : MultiItemEntity {
    override val itemType: Int
        get() = type

}
