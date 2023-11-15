package com.wbb.xiaodongjia.wiget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.bean.TagViewBean

/**
 * Created by zj on 2021/1/26.
 */
class RandomView : FrameLayout, View.OnClickListener {
    private var mOnWaterDropletsClickLinsener: OnWaterDropletsClickLinsener? = null
    private var mTagViewBean: TagViewBean? = null

    constructor(conext: Context, mTagViewBean: TagViewBean) : super(conext) {
        this.mTagViewBean = mTagViewBean
    }

    constructor(conext: Context, attrs: AttributeSet) : super(conext, attrs)
    constructor(conext: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        conext,
        attrs,
        defStyleAttr
    )

    init {

        initView()
    }

    /**
     * 设置点击事件传递
     */
    fun setOnActiivtyClickLinsener(mOnWaterDropletsClickLinsener: OnWaterDropletsClickLinsener?) {
        this.mOnWaterDropletsClickLinsener = mOnWaterDropletsClickLinsener

    }

    /**
     * 生成一个上下动的动画
     */
    private fun animation(): TranslateAnimation {
        val animation = TranslateAnimation(0f, 0f, -10f, 10f)
        animation.repeatCount = Animation.INFINITE
        animation.duration = 1000
        animation.repeatMode = Animation.REVERSE
        return animation
    }

    fun initView() {
        val mView = inflate(context, R.layout.view_random_layout, null)
        addView(mView)
        setOnClickListener(this)
        startAnimation(animation())
    }

    override fun onClick(v: View?) {
        clearAnimation()
        mOnWaterDropletsClickLinsener?.onClickLinsener(this, mTagViewBean)

    }

}