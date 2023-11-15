package com.wbb.xiaodongjia.adapter.help

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager


/**
 * Created by zj on 2021/1/26.
 */
class StaggeredDividerItemDecoration(context: Context?, interval: Int) :
    RecyclerView.ItemDecoration() {
    private var context: Context? = null
    private var interval = 0

    init {
        this.context = context
        this.interval = interval
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val params = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        // 获取item在span中的下标
        val spanIndex = params.spanIndex
        val interval = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            interval.toFloat(), context!!.resources.displayMetrics
        ).toInt()

        // 中间间隔
        if (spanIndex % 2 == 0) {
            outRect.right = interval / 2
            outRect.left = 0
        } else {
            // item为奇数位，设置其左间隔为5dp
            outRect.left = interval / 2
            outRect.right = 0
        }
        // 下方间隔
        outRect.bottom = interval
    }
}