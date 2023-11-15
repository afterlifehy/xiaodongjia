package com.wbb.base.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.wbb.base.R
import kotlinx.android.synthetic.main.dialog_clear_search_history.*

/**
 * Created by hy on 2021/2/1.
 */
class ClearSearchHistoryDialog(context: Context, val callBack: ClearSearchHistoryCallback) : BaseLibDialog(context, R.style.CommonBottomDialogStyle), View.OnClickListener {

    init {
        initView()
    }

    private fun initView() {
        tv_makeSure.setOnClickListener(this)
        tv_cancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_makeSure -> {
                callBack.makeSure()
                dismiss()
            }
            R.id.tv_cancel -> {
                dismiss()
            }
        }
    }

    override fun getHeight(): Float {
        return WindowManager.LayoutParams.WRAP_CONTENT.toFloat()
    }

    override fun getLayoutResID(): Int {
        return R.layout.dialog_clear_search_history
    }

    override fun getCanceledOnTouchOutside(): Boolean {
        return true
    }

    override fun getHideInput(): Boolean {
        return true
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }

    override fun getWidth(): Float {
        return WindowManager.LayoutParams.MATCH_PARENT.toFloat()
    }

    interface ClearSearchHistoryCallback {
        fun makeSure()
    }
}