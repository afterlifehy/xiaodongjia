package com.wbb.xiaodongjia.adapter

import android.graphics.Color
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.aries.ui.view.radius.RadiusLinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.bean.MainTabInfo
import com.wbb.xiaodongjia.wiget.AnimationRadioView

/**
 * Created by zj on 2021/1/25.
 */
class MainTabAdapter(data: MutableList<MainTabInfo>, mOnTabSelectLinsener: OnTabSelectLinsener?) :
    BaseQuickAdapter<MainTabInfo, BaseViewHolder>(R.layout.view_main_tab_layout, data) {
    var mSelectTabIndex = 0
    var mSelectFragment: Fragment? = null
    var mOnTabSelectLinsener: OnTabSelectLinsener? = null

    init {
        this.mOnTabSelectLinsener = mOnTabSelectLinsener
    }

    override fun convert(holder: BaseViewHolder, item: MainTabInfo) {
        if (data.indexOf(item) == 0) {
            holder.getView<RadiusLinearLayout>(R.id.rll_conent).delegate.setTopLeftRadius(50f)
            holder.getView<RadiusLinearLayout>(R.id.rll_conent).delegate.init()
        } else if (data.indexOf(item) == data.size - 1) {
            holder.getView<RadiusLinearLayout>(R.id.rll_conent).delegate.setTopRightRadius(50f)
            holder.getView<RadiusLinearLayout>(R.id.rll_conent).delegate.init()
        }
        val tv_tab_name = holder.getView<TextView>(R.id.tv_tab_name)
        val lav_tab_icon = holder.getView<AnimationRadioView>(R.id.lav_tab_icon)
        tv_tab_name.setText(item.tabName)
        lav_tab_icon.setAnimation(item.mAnimationName)
        lav_tab_icon.imageAssetsFolder = item.imageAssetsFolder
        lav_tab_icon.repeatCount = 0
        if (holder.layoutPosition == mSelectTabIndex) {
            lav_tab_icon.isChecked = true
            tv_tab_name.setTextColor(Color.parseColor("#050505"))
        } else {
            lav_tab_icon.isChecked = false
            tv_tab_name.setTextColor(Color.parseColor("#918BB2"))
        }
        holder.getView<RadiusLinearLayout>(R.id.rll_conent).setOnClickListener {
            lav_tab_icon.playAnimation()
            val oderSelct = mSelectTabIndex
            mSelectTabIndex = holder.layoutPosition
            notifyItemChanged(mSelectTabIndex)
            notifyItemChanged(oderSelct)
            mOnTabSelectLinsener?.onTabSelectLinsener(holder.layoutPosition, item)
        }
    }

    interface OnTabSelectLinsener {
        fun onTabSelectLinsener(
            tabIndex: Int = 0,
            item: MainTabInfo? = null
        )
    }
}