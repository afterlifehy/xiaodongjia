package com.wbb.base.adapter

import android.widget.ImageView
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.chouyou.base.base.ShareItemData
import com.wbb.base.R

/**
 * Created by zj on 2020/11/27.
 */
class ShareItemAdapter(data: MutableList<ShareItemData>) :
    BaseQuickAdapter<ShareItemData, BaseViewHolder>(R.layout.view_shar_item_layout, data) {
    override fun convert(holder: BaseViewHolder, item: ShareItemData) {
        holder.getView<ImageView>(R.id.iv_share_iccon).setImageResource(item.iconId)
        holder.setText(R.id.tv_show_name, item.shareName)
        holder.getView<LinearLayout>(R.id.ll_content).setOnClickListener {
            getOnItemClickListener()?.onItemClick(this, it, holder.layoutPosition)
        }
    }
}