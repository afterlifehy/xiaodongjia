package com.chouyou.base.base

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.wbb.base.R
import com.wbb.base.wiget.NotDataView

/**
 * Created by zj on 2020/7/24.
 */
class BaseViewAddFactoryImpl : BaseViewAddFactory {


    override fun getRoootView(context: Context): View {//写在这里好统一布局
        val mRootView = View.inflate(context, R.layout.new_base_not_title_layout, null)
        return mRootView
    }


    override fun getNotDataView(context: Context): View {
        //val mAddView = View.inflate(context, R.layout.not_data_layout, null)
        return NotDataView(context)
    }

    override fun getNewWorkErrorView(context: Context): View {
        val mAddView = View.inflate(context, R.layout.network_error_data_layout, null)

        return mAddView
    }

    override fun getLoadProgressView(context: Context): View {
        val mAddView = View.inflate(context, R.layout.data_load_layout, null)
        return mAddView
    }
}