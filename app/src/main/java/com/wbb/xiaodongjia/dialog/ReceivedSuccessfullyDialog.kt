package com.wbb.xiaodongjia.dialog

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import com.wbb.base.util.AppUtil
import com.wbb.base.util.StatusBarUtils
import com.wbb.xiaodongjia.R

/**
 * Created by zj on 2021/1/26.
 */
class ReceivedSuccessfullyDialog(context: Context) : Dialog(context, R.style.DaoxilaDialog_Alert) {
    private var mDownTime = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setContentView(R.layout.dialog_get_sucess_layout)
    }

    fun init() {
        //        //宽度全屏显示
        val layoutParams = window?.getAttributes()
        layoutParams?.width = AppUtil.getScreanWidth() - AppUtil.dp2px(20f)
        layoutParams?.height = ActionBar.LayoutParams.WRAP_CONTENT
        layoutParams?.gravity = Gravity.CENTER
        window?.setAttributes(layoutParams)
        StatusBarUtils.setImmersiveStatus(window, false)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_UP -> {
                val time = System.currentTimeMillis() - mDownTime
                if (time >= 40 && time <= 200) {//算点击事件
                    if (event.getY() > 845) {
                        dismiss()
                        return true
                    }
                }

            }
        }
        return super.onTouchEvent(event)
    }
}