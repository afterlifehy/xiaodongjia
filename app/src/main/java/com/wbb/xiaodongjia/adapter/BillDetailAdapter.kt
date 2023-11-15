package com.wbb.xiaodongjia.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.base.bean.PayDetail
import com.wbb.xiaodongjia.R

/**
 * Created by hy on 2021/2/3.
 */
class BillDetailAdapter(data: MutableList<PayDetail>?) : BaseQuickAdapter<PayDetail, BaseViewHolder>(R.layout.item_bill_detail, data) {

    override fun convert(holder: BaseViewHolder, item: PayDetail) {
        holder.setText(R.id.tv_label, item.name)
        holder.setText(R.id.tv_content, item.value)
    }
}