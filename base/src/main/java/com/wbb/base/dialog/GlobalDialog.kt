package com.wbb.base.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.chouyou.base.ext.gone
import com.wbb.base.R
import com.wbb.base.util.ScreenUtils

/**
 * Created by zj on 2021/1/6.
 */
class GlobalDialog(context: Context, mDialogHelp: DialogHelp) :
    Dialog(context, R.style.DaoxilaDialog_Alert), View.OnClickListener {
    private var mRootView: View? = null
    private var mDialogHelp: DialogHelp? = null
    private var tv_title: TextView? = null
    private var tv_context_msg: TextView? = null
    private var tv_left_button: TextView? = null
    private var v_cancer_line: View? = null
    private var tv_reght_button: TextView? = null
    private var rl_left: RelativeLayout? = null

    init {
        this.mDialogHelp = mDialogHelp
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRootView =
            LayoutInflater.from(context).inflate(R.layout.dialog_context_layout, null)
        setContentView(mRootView!!)
        init()
        findView()
        bindShowViewData()
    }

    fun init() {
        //        //宽度全屏显示
        val layoutParams = window?.getAttributes()
        layoutParams?.width = (ScreenUtils.getScreenWidth() * 0.85).toInt()
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams?.gravity = Gravity.CENTER
        window?.setAttributes(layoutParams)
    }

    fun findView() {
        tv_title = findViewById(R.id.tv_title)
        tv_context_msg = findViewById(R.id.tv_context_msg)
        tv_left_button = findViewById(R.id.tv_left_button)
        v_cancer_line = findViewById(R.id.v_cancer_line)
        tv_reght_button = findViewById(R.id.tv_reght_button)
        rl_left = findViewById(R.id.rl_left)
        rl_left?.setOnClickListener(this)
        findViewById<RelativeLayout>(R.id.rl_reght).setOnClickListener(this)
        tv_reght_button?.setOnClickListener(this)

    }

    fun bindShowViewData() {
        mDialogHelp?.let {
            if (!TextUtils.isEmpty(it.title)) {
                tv_title?.text = it.title
            } else {
                tv_title?.gone()
            }
            if (!TextUtils.isEmpty(it.contentMsg)) {
                tv_context_msg?.text = it.contentMsg
            } else {
                tv_context_msg?.gone()
            }

            if (it.isAloneButton) {
                rl_left?.gone()
                v_cancer_line?.gone()
            } else {
                tv_left_button?.text = it.leftMsg
            }
            tv_reght_button?.text = it.rightMsg
            setCancelable(it.cancelable)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rl_left -> {
                mDialogHelp?.mOnButtonClickLinsener?.onLeftClickLinsener()
                dismiss()

            }
            R.id.rl_reght,
            R.id.tv_reght_button -> {
                mDialogHelp?.mOnButtonClickLinsener?.onRightClickLinsener()
                dismiss()
            }
        }
    }
}