package com.wbb.xiaodongjia.ui.activity.video.videolist

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import androidx.recyclerview.widget.RecyclerView.Recycler

/**
 * ViewPager效果的LayoutManager
 * @author xlx
 */
class PagerLayoutManager(context: Context?) : LinearLayoutManager(context) {
    private var mPagerSnapHelper: PagerSnapHelper? = null
    private var mOnViewPagerListener: OnViewPagerListener? = null

    /**
     * 移动方向
     */
    private var direction = 0
    private fun init() {
        mPagerSnapHelper = PagerSnapHelper()
    }

    override fun onAttachedToWindow(recyclerView: RecyclerView) {
        super.onAttachedToWindow(recyclerView)
        mPagerSnapHelper!!.attachToRecyclerView(recyclerView)
        recyclerView.addOnChildAttachStateChangeListener(mChildAttachStateChangeListener)
    }

    /**
     * 滑动状态的改变
     *
     * @param state 滑动状态
     */
    override fun onScrollStateChanged(state: Int) {
        when (state) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                val viewIdle = mPagerSnapHelper!!.findSnapView(this) ?: return
                val positionIdle = getPosition(viewIdle)
                if (mOnViewPagerListener != null && childCount == 1) {
                    Log.d("video-tag", "onScrollStateChanged: $positionIdle")
                    mOnViewPagerListener!!.onPageSelected(
                        positionIdle,
                        positionIdle == itemCount - 1
                    )
                }
            }
            else -> {
            }
        }
    }

    /**
     * 监听竖直方向的相对偏移量
     *
     * @param dy y方向位移
     * @param recycler recyclerView
     * @param state 滑动状态
     * @return
     */
    override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: RecyclerView.State): Int {
        direction = dy
        return super.scrollVerticallyBy(dy, recycler, state)
    }

    /**
     * 监听水平方向的相对偏移量
     *
     * @param dx
     * @param recycler
     * @param state
     * @return
     */
    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler, state: RecyclerView.State): Int {
        direction = dx
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    /**
     * 设置监听
     * @param listener
     */
    fun setOnViewPagerListener(listener: OnViewPagerListener?) {
        mOnViewPagerListener = listener
    }

    private val mChildAttachStateChangeListener: OnChildAttachStateChangeListener =
        object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                if (mOnViewPagerListener != null && childCount == 1) {
                    mOnViewPagerListener!!.onInitComplete()
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                if (direction >= 0) {
                    if (mOnViewPagerListener != null) {
                        mOnViewPagerListener!!.onPageRelease(true, getPosition(view))
                    }
                } else {
                    if (mOnViewPagerListener != null) {
                        mOnViewPagerListener!!.onPageRelease(false, getPosition(view))
                    }
                }
            }
        }

    interface OnViewPagerListener {
        /**
         * 初始化完成
         */
        fun onInitComplete()

        /**
         * 页面不可见, 释放
         * @param isNext 是否有下一个
         * @param position 下标
         */
        fun onPageRelease(isNext: Boolean, position: Int)

        /**
         * 选中的index
         * @param position 下标
         * @param b 是否到底部
         */
        fun onPageSelected(position: Int, b: Boolean)
    }

    init {
        init()
    }
}