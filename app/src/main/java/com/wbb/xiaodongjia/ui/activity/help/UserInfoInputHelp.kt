package com.wbb.xiaodongjia.ui.activity.help

import android.app.Activity
import android.view.View
import android.widget.ScrollView
import com.wbb.base.Keyboard.KeyboardHeightObserver
import com.wbb.base.Keyboard.KeyboardHeightProvider
import com.wbb.base.util.AppUtil
import com.wbb.xiaodongjia.R

/**
 * Created by zj on 2021/2/19.
 */
class UserInfoInputHelp(mActivity: Activity, mRootView: View?) : KeyboardHeightObserver {
    private var mActivity: Activity? = null
    var mKeyboardHeightProvider: KeyboardHeightProvider? = null
    private var mRootView: View? = null
    private var mBomView: View? = null
    private var sc_scroll: ScrollView? = null


    init {
        this.mActivity = mActivity
        this.mRootView = mRootView
        mKeyboardHeightProvider = KeyboardHeightProvider(mActivity)
        mBomView = mRootView?.findViewById(R.id.v_bom_view)
        sc_scroll = mRootView?.findViewById(R.id.sc_scroll)
        mBomView?.post {
            mKeyboardHeightProvider?.start()
        }
        mKeyboardHeightProvider?.setKeyboardHeightObserver(this)

    }

    private fun setBomViewHeight(height: Int) {
        mBomView?.let {
            val layoutParams = it.layoutParams
            layoutParams.height = height
            it.layoutParams = layoutParams
        }
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        val empHeight = height + AppUtil.dp2px(60f)
        if (height > 0) {//软键盘弹出
            setBomViewHeight(empHeight)
            sc_scroll?.smoothScrollBy(0, empHeight)
        } else {
            setBomViewHeight(empHeight)
            sc_scroll?.smoothScrollBy(0, -empHeight)
        }
    }

    override fun onDestroy() {
        mKeyboardHeightProvider?.setKeyboardHeightObserver(null)
    }
}