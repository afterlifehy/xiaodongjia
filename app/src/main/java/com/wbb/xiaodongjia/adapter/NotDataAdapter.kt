package com.wbb.xiaodongjia.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.xiaodongjia.R

/**
 * Created by zj on 2021/3/5.
 */
class NotDataAdapter(data: MutableList<String>) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.not_data_layout, data) {
    override fun convert(holder: BaseViewHolder, item: String) {
    }
}