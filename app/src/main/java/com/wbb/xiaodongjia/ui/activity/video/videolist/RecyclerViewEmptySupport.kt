package com.wbb.xiaodongjia.ui.activity.video.videolist

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author zsy_18 result:2019/1/17
 */
class RecyclerViewEmptySupport : RecyclerView {
    private var emptyView: View? = null
    private val emptyObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            val adapter = adapter
            if (adapter != null && emptyView != null) {
                if (adapter.itemCount == 0) {
                    emptyView!!.visibility = VISIBLE
                    this@RecyclerViewEmptySupport.visibility = GONE
                } else {
                    emptyView!!.visibility = GONE
                    this@RecyclerViewEmptySupport.visibility = VISIBLE
                }
            }
        }
    }

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(emptyObserver)

        //emptyObserver.onChanged();
    }

    fun setEmptyView(emptyView: View?) {
        this.emptyView = emptyView
    }
}