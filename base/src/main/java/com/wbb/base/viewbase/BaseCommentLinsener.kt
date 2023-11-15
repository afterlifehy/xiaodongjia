package com.chouyou.base.base

import android.view.View
import com.wbb.base.wiget.PagerStatesView

/**
 * Created by zj on 2020/7/24.
 */
interface BaseCommentLinsener {
    fun setTitleName(title: String)

    /**
     * 显示提示View, 如果暂无数据，网络错误等
     */
    fun showPromptView(
        text: String,
        errorIcont: Int,
        mOnNotNetWorkClickLinsener: PagerStatesView.OnPagerClickLinsener? = null
    )

    /**
     * 隐藏提示布局
     */
    fun hidePromptView()
    fun showLoadData()
    fun onMyPause()
    fun hideLoadData()
    fun showNewWorkError(mOnNotNetWorkClickLinsener: PagerStatesView.OnPagerClickLinsener? = null)
    fun goneNewWorkError()
    fun setRightIcon(icon: Int, onCLickLinsenr: View.OnClickListener)


    fun notDataIsVisib(): Boolean
}