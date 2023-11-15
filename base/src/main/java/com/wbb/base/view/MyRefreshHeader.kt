package com.wbb.base.view

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.airbnb.lottie.LottieAnimationView
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.wbb.base.R
import com.wbb.base.help.SmartRefreshPullLinsener


class MyRefreshHeader : LinearLayout, RefreshHeader {
    private var lav_refresh_header: LottieAnimationView? = null
    private var view: LinearLayout? = null
    private var mAnimPull: AnimationDrawable? = null
    private var mSmartRefreshPullLinsener: SmartRefreshPullLinsener? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context?, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    /**
     * 这个是用来监听刷新的开始和完成
     *
     * @param mSmartRefreshPullLinsener
     */
    fun setmSmartRefreshPullLinsener(mSmartRefreshPullLinsener: SmartRefreshPullLinsener?) {
        this.mSmartRefreshPullLinsener = mSmartRefreshPullLinsener
    }

    init {
        view = View.inflate(context, R.layout.layout_refresh_header, this) as LinearLayout
        lav_refresh_header = view?.findViewById(R.id.lav_refresh_header)
    }

    /**
     * 2，获取真实视图（必须返回，不能为null）一般就是返回当前自定义的view
     */
    override fun getView(): View {
        return this
    }

    /**
     * 3，获取变换方式（必须指定一个：平移、拉伸、固定、全屏）,Translate指平移，大多数都是平移
     */
    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate
    }

    /**
     * 4，执行下拉的过程
     */
    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {
        if (percent < 1) {
            lav_refresh_header?.scaleX = percent
            lav_refresh_header?.scaleY = percent
        }
    }

    /**
     * 5，一般可以理解为一下case中的三种状态，在达到相应状态时候开始改变
     * 注意：这三种状态都是初始化的状态
     */
    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        when (newState) {
            //1,下拉刷新的开始状态：下拉可以刷新
            RefreshState.PullDownToRefresh -> {
                mSmartRefreshPullLinsener?.onPulling()
//                lav_refresh_header?.imageAssetsFolder = "refresh"
                lav_refresh_header?.setAnimation("refresh/refresh.json")
                lav_refresh_header?.repeatCount = -1
                lav_refresh_header?.playAnimation()
            }
            //2,下拉到最底部的状态：释放立即刷新
            RefreshState.ReleaseToRefresh -> {
            }
            //3,下拉到最底部后松手的状态：正在刷新
            RefreshState.Refreshing -> {
            }
            RefreshState.RefreshFinish -> {
            }
            RefreshState.None -> {//取消
                mSmartRefreshPullLinsener?.onReleasing()
            }
        }
    }

    /**
     * 6，结束下拉刷新的时候需要关闭动画
     */
    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        return 0
    }

    override fun setPrimaryColors(vararg colors: Int) {

    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

}