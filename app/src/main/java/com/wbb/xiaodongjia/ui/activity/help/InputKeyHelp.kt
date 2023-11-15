package com.wbb.xiaodongjia.ui.activity.help

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.wbb.base.Keyboard.KeyboardHeightObserver
import com.wbb.base.Keyboard.KeyboardHeightProvider
import com.wbb.base.util.AppUtil
import com.wbb.xiaodongjia.R

/**
 * Created by zj on 2021/1/29.
 */
class InputKeyHelp(mActivity: Activity) : KeyboardHeightObserver {
    var mKeyboardHeightProvider: KeyboardHeightProvider? = null
    private var mActivity: Activity? = null
    private var sc_scroll: ScrollView? = null
    private var rl_bom: View? = null

    init {
        this.mActivity = mActivity
        mKeyboardHeightProvider = KeyboardHeightProvider(mActivity)

        sc_scroll = mActivity.findViewById(R.id.sc_scroll)
        rl_bom = mActivity.findViewById(R.id.rl_bom)
        rl_bom?.post {
            mKeyboardHeightProvider?.start()
        }
        mKeyboardHeightProvider!!.setKeyboardHeightObserver(this)
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        val empHeight = height + AppUtil.dp2px(20f)

        if (height > 0) {
            val layoutParams = rl_bom?.getLayoutParams() as ViewGroup.LayoutParams
            layoutParams.height = empHeight
            rl_bom?.setLayoutParams(layoutParams)
            sc_scroll?.smoothScrollBy(0, empHeight)
        } else {
            val layoutParams = rl_bom?.getLayoutParams() as ViewGroup.LayoutParams
            layoutParams.height = 0
            rl_bom?.setLayoutParams(layoutParams)
            sc_scroll?.smoothScrollBy(0, -empHeight)
        }
    }

    override fun onDestroy() {
        mKeyboardHeightProvider?.setKeyboardHeightObserver(null)
    }
}