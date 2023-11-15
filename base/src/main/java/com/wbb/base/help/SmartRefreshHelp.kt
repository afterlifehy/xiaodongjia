package com.wbb.base.help

import android.content.Context
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.*
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.wbb.base.view.MyRefreshHeader

/**
 * Created by zj on 2021/1/25.
 */
object SmartRefreshHelp {
    /**
     * 初始化全局的加载效果
     */
    fun initRefHead() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(object : DefaultRefreshHeaderCreator {
            override fun createRefreshHeader(
                context: Context,
                layout: RefreshLayout
            ): RefreshHeader {
                return MyRefreshHeader(context)
            }
        })
        SmartRefreshLayout.setDefaultRefreshFooterCreator(object : DefaultRefreshFooterCreator {
            override fun createRefreshFooter(
                context: Context,
                layout: RefreshLayout
            ): RefreshFooter {
                //指定为经典Footer，默认是 BallPulseFooter
                val footer = ClassicsFooter(context).setDrawableSize(15f)
                footer.setTextSizeTitle(12f)
                footer.setFinishDuration(0)
                return footer
            }
        })
    }
}