package com.chouyou.base.base

import android.content.Context
import android.view.View
import com.wbb.base.wiget.PagerStatesView

/**
 * Created by zj on 2020/7/24.
 */
interface BaseViewAddManagers : BaseCommentLinsener {

    fun getRootView(context: Context, mView: View): View

    fun getRootViewId(context: Context, contextId: Int): View

    fun setIsLoadNotData(isLoadNotData: Boolean)

    fun setIsShowTitle(isShowTitle: Boolean)

    fun setNetWorkStatesView(
        isShow: Boolean,
        mOnNotNetWorkClickLinsener: PagerStatesView.OnPagerClickLinsener? = null
    )
}