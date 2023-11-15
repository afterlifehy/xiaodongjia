package com.wbb.xiaodongjia.adapter

import android.view.View
import android.widget.LinearLayout
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.base.adapter.CustomBaseQuickAdapter
import com.wbb.base.bean.DongClassBean
import com.wbb.base.ext.i18n
import com.wbb.base.util.GlideUtils
import com.wbb.xiaodongjia.R

/**
 * Created by hy on 2021/1/28.
 */
class DongClassAdapter(data: MutableList<DongClassBean>?, val onClickListener: View.OnClickListener) : CustomBaseQuickAdapter<DongClassBean, BaseViewHolder>(R.layout.item_class, data) {

    override fun convert(holder: BaseViewHolder, item: DongClassBean) {
        holder.setText(R.id.tv_desc, item.title)
        GlideUtils.getInstance().loadImage(holder.getView(R.id.riv_class), item.imgPath, R.mipmap.ic_class_placeholder)
        holder.setText(R.id.tv_videoCount, item.num + i18n(R.string.个视频))
        holder.getView<LinearLayout>(R.id.ll_class).tag = item
        holder.getView<LinearLayout>(R.id.ll_class).setOnClickListener(onClickListener)
    }
}