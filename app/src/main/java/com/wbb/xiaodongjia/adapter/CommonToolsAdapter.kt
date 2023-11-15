package com.wbb.xiaodongjia.adapter

import android.view.View
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.xiaodongjia.R
import com.wbb.base.bean.CommenTools
import com.wbb.base.util.GlideUtil
import com.wbb.base.util.GlideUtils

/**
 * Created by hy on 2021/2/19.
 */
class CommonToolsAdapter(data: MutableList<CommenTools>?, val onClickListener: View.OnClickListener) : BaseQuickAdapter<CommenTools, BaseViewHolder>(R.layout.item_common_tools, data) {

    override fun convert(holder: BaseViewHolder, item: CommenTools) {
        GlideUtils.getInstance().loadImage(holder.getView(R.id.iv_logo),item.img,R.mipmap.ic_tools_placeholder)
        holder.setText(R.id.tv_name, item.name)
        holder.getView<LinearLayout>(R.id.ll_tools).setTag(item)
        holder.getView<LinearLayout>(R.id.ll_tools).setOnClickListener(onClickListener)
    }
}