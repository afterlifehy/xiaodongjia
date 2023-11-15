package com.wbb.xiaodongjia.adapter

import android.view.View
import android.widget.RelativeLayout
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.base.adapter.CustomBaseQuickAdapter
import com.wbb.base.bean.SearchResultBean
import com.wbb.base.ext.i18n
import com.wbb.base.util.DateUtil
import com.wbb.base.util.GlideUtils
import com.wbb.base.util.MagicValue
import com.wbb.xiaodongjia.R

/**
 * Created by hy on 2021/2/25.
 */
class SearchResultAdapter(data: MutableList<SearchResultBean>?, val onClickListener: View.OnClickListener) :
    CustomBaseQuickAdapter<SearchResultBean, BaseViewHolder>(R.layout.item_search_result, data) {

    override fun convert(holder: BaseViewHolder, item: SearchResultBean) {
        GlideUtils.getInstance().loadImage(holder.getView(R.id.riv_img), item.img, R.mipmap.ic_class_placeholder)
        holder.setText(R.id.tv_name, item.name)
        if (!item.labels.isNullOrEmpty()) {
            var labels = ""
            for (i in item.labels) {
                labels += i.labelName
            }
            holder.setText(R.id.tv_label1, labels)
        }
        when (item.searchType) {
            MagicValue.SEARCH_TYPE_BUSINESS -> {
                holder.setText(R.id.tv_label2, i18n(R.string.东家人数) + "：${item.vipNum}")
                holder.setText(R.id.tv_label3, i18n(R.string.地址) + "：${item.address}")
            }
            MagicValue.SEARCH_TYPE_CLASS -> {
                holder.setText(R.id.tv_label2, i18n(R.string.讲师) + "：${item.author}")
                holder.setText(R.id.tv_label3, i18n(R.string.课件时长) + "：${item.minutes}" + i18n(R.string.分))
            }
            MagicValue.SEARCH_TYPE_ACTIVITY -> {
                holder.setText(R.id.tv_label2, i18n(R.string.活动时间) + "：${DateUtil.getLongToString(item.startTime, "yyyy.MM.dd HH:mm")}")
                holder.setText(R.id.tv_label3, i18n(R.string.地址) + "：${item.address}")
            }
        }
        holder.getView<RelativeLayout>(R.id.rl_search).tag = item
        holder.getView<RelativeLayout>(R.id.rl_search).setOnClickListener(onClickListener)
    }
}