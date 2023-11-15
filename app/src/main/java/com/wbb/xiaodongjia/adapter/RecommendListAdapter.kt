package com.wbb.xiaodongjia.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.base.adapter.CustomBaseQuickAdapter
import com.wbb.xiaodongjia.R
import com.wbb.base.bean.RecommendListBean
import com.wbb.base.util.*

/**
 * Created by hy on 2021/2/23.
 */
class RecommendListAdapter(data: MutableList<RecommendListBean>?) : CustomBaseQuickAdapter<RecommendListBean, BaseViewHolder>(R.layout.item_recommend, data) {
    var sizes = intArrayOf(24, 12)
    var colors = intArrayOf(R.color.color_ff171b20, R.color.color_ff808a96)

    override fun convert(holder: BaseViewHolder, item: RecommendListBean) {
        GlideUtils.getInstance().loadImage(holder.getView(R.id.riv_head), item.icon, R.mipmap.ic_header_placeholder)
        holder.setText(R.id.tv_passport, item.recommend)
        holder.setText(R.id.tv_time, DateUtil.getLongToString(item.times * 1000, "yyyy-MM-dd"))
        holder.setText(R.id.tv_phone, item.phone)
        var strings = arrayOf(item.incomeTotal.toString(), item.incomeTotalUnit.toString())
        holder.setText(R.id.tv_income, StringUtil.getSpan(strings, sizes, colors))
    }

}