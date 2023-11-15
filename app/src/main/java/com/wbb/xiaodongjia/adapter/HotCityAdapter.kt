package com.wbb.xiaodongjia.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.base.adapter.CustomBaseQuickAdapter
import com.wbb.xiaodongjia.R

/**
 * Created by zj on 2021/2/23.
 */
class HotCityAdapter(data: MutableList<String>) :
    CustomBaseQuickAdapter<String, BaseViewHolder>(R.layout.view_hot_city_layout, data) {
    override fun convert(holder: BaseViewHolder, item: String) {
    }
}