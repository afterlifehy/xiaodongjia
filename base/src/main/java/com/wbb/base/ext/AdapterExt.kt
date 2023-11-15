package com.wbb.base.ext

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.base.BaseApplication

fun BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>.i18N(id: Int): String {
    return BaseApplication.instance().resources.getString(id)
}

fun BaseQuickAdapter<Any, BaseViewHolder>.i18N(id: Int): String {
    return BaseApplication.instance().resources.getString(id)
}