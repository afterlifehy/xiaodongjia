package com.wbb.base.wiget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.chouyou.base.base.BaseViewAddFactory
import com.wbb.base.R

/**
 * Created by zj on 2021/2/20.
 */
class PagerStatesView : FrameLayout {
    constructor(
        conext: Context
    ) : super(conext)

    constructor(conext: Context, attrs: AttributeSet) : super(conext, attrs)
    constructor(conext: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        conext,
        attrs,
        defStyleAttr
    )

    init {
        initView()
    }

    private fun initView() {

    }

    /**
     * 添加一个暂无网络数据
     */
    fun addErrorNetWorkView(
        isShow: Boolean,
        mOnNotNetWorkClickLinsener: OnPagerClickLinsener? = null
    ) {
        removeViewws()
        if (isShow) {
            visibility = View.VISIBLE
            val net_work_view = BaseViewAddFactory.getInsten().getNewWorkErrorView(context)
            net_work_view.setOnClickListener {
                mOnNotNetWorkClickLinsener?.onPagerClick()
            }
            addView(net_work_view)
        } else {
            visibility = View.GONE
        }

    }

    fun removeViewws() {
        if (getChildAt(0) != null) {
            removeAllViews()
        }
    }

    /**
     * 显示暂无数据
     */
    fun addNotDataView(
        isShow: Boolean,
        iconId: Int = 0,
        notStr: String = "暂无数据",
        mOnNotNetWorkClickLinsener: OnPagerClickLinsener? = null
    ) {
        removeViewws()
        if (isShow) {
            visibility = View.VISIBLE
            val not_data_view = BaseViewAddFactory.getInsten().getNotDataView(context)
            not_data_view.setOnClickListener {
                mOnNotNetWorkClickLinsener?.onPagerClick()
            }
            val iv_not_data_icon = not_data_view.findViewById<ImageView>(R.id.iv_not_data_icon)
            val tv_not_data_text = not_data_view.findViewById<TextView>(R.id.tv_not_data_text)
            tv_not_data_text.setText(notStr)
            iv_not_data_icon.setImageResource(iconId)
            addView(not_data_view)
        } else {
            visibility = View.GONE
        }
    }

    /**
     * 显示加载框效果
     */
    fun addLoadProgress(isShow: Boolean) {
        removeViewws()
        if (isShow) {
            visibility = View.VISIBLE
            val not_data_view = BaseViewAddFactory.getInsten().getLoadProgressView(context)
            val pager_load_progress =
                not_data_view.findViewById<LottieAnimationView>(R.id.pager_load_progress)
            pager_load_progress.setAnimation("load/load.json")
            pager_load_progress.repeatCount = -1
            pager_load_progress.playAnimation()
            addView(not_data_view)
        } else {
            visibility = View.GONE
        }
    }

    interface OnPagerClickLinsener {
        fun onPagerClick()
    }
}