package com.wbb.xiaodongjia.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.wbb.base.dialog.BaseLibDialog
import com.wbb.base.util.AppUtil
import com.wbb.xiaodongjia.R
import kotlinx.android.synthetic.main.dialog_select_pic.*

class SelectPicDialog(context: Context, private val callback: Callback) : BaseLibDialog(context), View.OnClickListener {

    init {
        initView()
    }

    private fun initView() {
        rtv_selectFromAlbum.setOnClickListener(this)
        rtv_cancel.setOnClickListener(this)
        tv_takePhoto.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rtv_selectFromAlbum -> callback.onPickPhoto()
            R.id.tv_takePhoto -> callback.onTakePhoto()
            R.id.rtv_cancel -> {
            }
        }
        dismiss()
    }

    override fun getHideInput(): Boolean {
        return false
    }

    override fun getWidth(): Float {
        return (AppUtil.getScreanWidth() * 0.9).toFloat()
    }

    override fun getHeight(): Float {
        return WindowManager.LayoutParams.WRAP_CONTENT.toFloat()
    }

    override fun getLayoutResID(): Int {
        return R.layout.dialog_select_pic
    }

    override fun getCanceledOnTouchOutside(): Boolean {
        return false
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }

    interface Callback {

        fun onTakePhoto()

        fun onPickPhoto()

    }
}