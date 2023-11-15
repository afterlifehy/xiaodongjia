package com.wbb.xiaodongjia.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.base.util.GlideUtils
import com.wbb.xiaodongjia.R

/**
 * Created by hy on 2021/2/25.
 */
class ParticipantsAdapter(data: MutableList<String>?) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_activity_participants, data) {

    override fun convert(holder: BaseViewHolder, item: String) {
        GlideUtils.getInstance().loadImage(holder.getView(R.id.riv_participants),item,R.mipmap.ic_header_placeholder)
    }
}