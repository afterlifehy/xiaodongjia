package com.wbb.xiaodongjia.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.chouyou.base.ext.gone
import com.chouyou.base.ext.hide
import com.chouyou.base.ext.show
import com.wbb.base.dialog.BaseLibDialog
import com.wbb.base.util.AppUtil
import com.wbb.xiaodongjia.R
import kotlinx.android.synthetic.main.dialog_check_update.*

/**
 * Created by hy on 2021/3/19.
 */
class CheckUpdateDialog(context: Context, val content: String, val isNecessary: Boolean, val callback: UpdateCallback) : BaseLibDialog(context), View.OnClickListener {

    init {
        initView()
    }

    private fun initView() {
        et_upgradeContent.setText(content)
        if (isNecessary) {
            rtv_cancel.gone()
        }
        rtv_cancel.setOnClickListener(this)
        tv_update.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rtv_cancel -> {
                dismiss()
            }
            R.id.tv_update -> {
                callback.updateNow()
            }
        }
    }

    fun startUpdate() {
        ll_progress.show()
        et_upgradeContent.hide()
        ll_updateOperate.hide()
    }

    override fun getHeight(): Float {
        return WindowManager.LayoutParams.WRAP_CONTENT.toFloat()
    }

    override fun getLayoutResID(): Int {
        return R.layout.dialog_check_update
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
        return AppUtil.getScreanWidth() * 0.85f
    }

    fun getSeekBar(): SeekBar {
        return sb_download
    }

    fun getProgressPop(): RelativeLayout {
        return rl_progress
    }

    interface UpdateCallback {
        fun updateNow()
    }
}