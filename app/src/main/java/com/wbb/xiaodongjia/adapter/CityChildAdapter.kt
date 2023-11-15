package com.wbb.xiaodongjia.adapter

import android.widget.TextView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.base.adapter.CustomBaseQuickAdapter
import com.wbb.base.bean.CityChildItemInfo
import com.wbb.xiaodongjia.R

/**
 * Created by zj on 2021/2/23.
 */
class CityChildAdapter(
    data: MutableList<CityChildItemInfo>
    ) :
    CustomBaseQuickAdapter<CityChildItemInfo, BaseViewHolder>(
        R.layout.view_city_child_layout,
        data
    ) {
    private var mOnSelectLinsener: CityListAdapter.OnSelectLinsener? = null

    fun setOnSelectLinsener(mOnSelectLinsener: CityListAdapter.OnSelectLinsener) {
        this.mOnSelectLinsener = mOnSelectLinsener
    }

    override fun convert(holder: BaseViewHolder, item: CityChildItemInfo) {
        val tv_content = holder.getView<TextView>(R.id.tv_content)
        tv_content.setText(item.cityName)
        tv_content.setOnClickListener {
            mOnSelectLinsener?.onSelectCityLinsener(item)
        }
    }
}