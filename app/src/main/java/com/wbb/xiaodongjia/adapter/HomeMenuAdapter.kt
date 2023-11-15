package com.wbb.xiaodongjia.adapter

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.exoplayer2.util.Log
import com.makeramen.roundedimageview.RoundedImageView
import com.wbb.base.bean.HomeMenuInfo
import com.wbb.base.help.OnItemClickLinsener
import com.wbb.base.util.AppUtil
import com.wbb.base.util.GlideUtil
import com.wbb.base.util.GlideUtils
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.wiget.VerticalViewPager2

/**
 * Created by zj on 2021/1/25.
 */
class HomeMenuAdapter(data: MutableList<HomeMenuInfo>) :
    BaseQuickAdapter<HomeMenuInfo, BaseViewHolder>(R.layout.view_home_menu_layout, data) {
    var mOnItemClickLinsener: OnItemClickLinsener<HomeMenuInfo>? = null
    var itemWidth = AppUtil.dp2px(58f)

    init {
        checkItemWidth(data.size)
    }

    fun checkItemWidth(itemSize: Int) {
        if (itemSize < 5) {
            val interval = 20 * (itemSize - 1)
            itemWidth =
                (AppUtil.getScreanWidth() - AppUtil.dp2px(32f) - AppUtil.dp2px(interval.toFloat())) / itemSize
        } else {
            itemWidth = AppUtil.dp2px(60f)
        }
    }

    override fun setList(list: Collection<HomeMenuInfo>?) {
        list?.let {
            checkItemWidth(it.size)
        }
        super.setList(list)

    }

    /**
     * 设置点击事件
     */
    fun setOnItemClickLinsener(mOnItemClickLinsener: OnItemClickLinsener<HomeMenuInfo>?) {
        this.mOnItemClickLinsener = mOnItemClickLinsener
    }

    override fun convert(holder: BaseViewHolder, item: HomeMenuInfo) {
        holder.setText(R.id.tv_menu_name, item.name)
        val ll_conent = holder.getView<LinearLayout>(R.id.ll_conent)
        val layPar = ll_conent.layoutParams as RecyclerView.LayoutParams
        if (holder.layoutPosition == data.size - 1) {
            layPar.setMargins(0, 0, 0, 0)
        } else {
            layPar.setMargins(0, 0, AppUtil.dip2px(20f), 0)
        }
        layPar.width = itemWidth


        ll_conent.setOnClickListener {
            mOnItemClickLinsener?.onItemClick(holder.layoutPosition, item)
        }
        val iv_icon = holder.getView<RoundedImageView>(R.id.iv_icon)
        GlideUtils.getInstance().loadImage(iv_icon, item.icon, R.mipmap.home_menu_default_icon)
    }
}