package com.wbb.xiaodongjia.ui.activity.video.videolist

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * @author zsy_18 result:2018/12/20
 */
class AlivcSwipeRefreshLayout : SwipeRefreshLayout {
    private var startY = 0f
    private var startX = 0f

    /**
     * 记录是否向下拖拽的标记
     */
    private var mIsDragger = false

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        super.onInterceptTouchEvent(ev)
        if (!canChildScrollUp()) {
            val action = ev.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    // 记录手指按下的位置
                    startY = ev.y
                    startX = ev.x
                    // 初始化标记
                    mIsDragger = false
                }
                MotionEvent.ACTION_MOVE -> {
                    // 获取当前手指位置
                    val endY = ev.y
                    val distanceY = endY - startY
                    Log.e("test", "distanceY$distanceY")
                    if (distanceY > 10) {
                        mIsDragger = true
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->                     // 初始化标记
                    mIsDragger = false
                else -> {
                }
            }
            return mIsDragger
        }
        return false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val onTouchEvent = super.onTouchEvent(ev)
        Log.e("test", "onTouchEvent$onTouchEvent")
        return onTouchEvent
    }
}