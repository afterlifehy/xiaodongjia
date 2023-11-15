package com.wbb.base.dialog

import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import com.wbb.base.R
import com.wbb.base.util.AppUtil
import kotlinx.android.synthetic.main.dialog_loading.*

/**
 * Created by hy on 2021/3/12.
 */
class LoadingDialog(context: Context, val content: String) : BaseLibDialog(context) {

    init {
        initView()
    }

    private fun initView() {
        tv_content.text = content
        lav_loading?.setAnimation("load/load.json")
        lav_loading?.repeatCount = -1
    }

    fun updateContent(content: String) {
        tv_content.text = content
    }

    fun showDialog() {
        show()
        lav_loading?.playAnimation()
    }

    fun closeDialog() {
        lav_loading?.cancelAnimation()
        dismiss()
    }

    override fun getHeight(): Float {
        return WindowManager.LayoutParams.WRAP_CONTENT.toFloat()
    }

    override fun getLayoutResID(): Int {
        return R.layout.dialog_loading
    }

    override fun getCanceledOnTouchOutside(): Boolean {
        return false
    }

    override fun getHideInput(): Boolean {
        return true
    }

    override fun getGravity(): Int {
        return Gravity.CENTER
    }

    override fun getWidth(): Float {
        return AppUtil.getScreanWidth() * 0.4f
    }
}