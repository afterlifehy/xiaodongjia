package com.wbb.xiaodongjia.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.base.adapter.CustomBaseQuickAdapter
import com.wbb.xiaodongjia.R

/**
 * Created by zj on 2021/2/23.
 */
class LetterAdapter(data: MutableList<String>) :
    CustomBaseQuickAdapter<String, BaseViewHolder>(R.layout.view_letter_layout, data) {
    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.tv_str, item)
        addChildClickViewIds(R.id.ll_content)
    }
}