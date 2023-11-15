package com.wbb.xiaodongjia.adapter

import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.base.bean.VideoPlayInfo
import com.wbb.base.help.OnItemClickLinsener
import com.wbb.xiaodongjia.R

/**
 * Created by zj on 2021/2/2.
 */
class CourseListAdapter(
    list: MutableList<VideoPlayInfo>,
    mOnItemClickLinsener: OnItemClickLinsener<VideoPlayInfo>? = null
) :
    BaseQuickAdapter<VideoPlayInfo, BaseViewHolder>(R.layout.view_cous_item_layout, list) {
    private var mOnItemClickLinsener: OnItemClickLinsener<VideoPlayInfo>? = null

    init {
        this.mOnItemClickLinsener = mOnItemClickLinsener
    }

    override fun convert(holder: BaseViewHolder, item: VideoPlayInfo) {
        holder.setText(R.id.tv_cous_nam, item.courseName)
        holder.getView<LinearLayout>(R.id.ll_content).setOnClickListener {
            mOnItemClickLinsener?.onItemClick(holder.layoutPosition, item)
        }
    }
}