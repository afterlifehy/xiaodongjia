package com.wbb.xiaodongjia.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.exoplayer2.util.Log
import com.wbb.base.ext.i18N
import com.wbb.base.util.ARouterMap
import com.wbb.base.util.Constant
import com.wbb.base.util.ScreenUtils
import com.wbb.xiaodongjia.R

/**
 * Created by zj on 2021/1/6.
 */
class PrivacyAgreementDialog(context: Context, mOnButtonClickLinsener: OnButtonClickLinsener) :
    Dialog(context, R.style.DaoxilaDialog_Alert), View.OnClickListener {
    private var mRootView: View? = null
    private var tv_title: TextView? = null
    private var tv_context_msg: TextView? = null
    private var tv_left_button: TextView? = null
    private var v_cancer_line: View? = null
    private var tv_reght_button: TextView? = null
    private var rl_left: RelativeLayout? = null
    private var mOnButtonClickLinsener: OnButtonClickLinsener? = null

    init {
        this.mOnButtonClickLinsener = mOnButtonClickLinsener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRootView =
            LayoutInflater.from(context).inflate(R.layout.dialog_xy_layout, null)
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
        val text = i18N(R.string.user_protocol_reminder)
        val mSpannableStringBuilder = SpannableStringBuilder(text)
        val colorSpan = ForegroundColorSpan(Color.parseColor("#fffbb700"))
        val mClickableSpan1 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                ARouter.getInstance().build(ARouterMap.WEBVIEW)
                    .withString(ARouterMap.URL, Constant.USER_POLICY_URL)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            override fun updateDrawState(ds: TextPaint) {
                //  super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        val mClickableSpan2 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                ARouter.getInstance().build(ARouterMap.WEBVIEW)
                    .withString(ARouterMap.URL, Constant.USER_YS_URL)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            override fun updateDrawState(ds: TextPaint) {
                //  super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        mSpannableStringBuilder.setSpan(mClickableSpan1, 105, 119, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mSpannableStringBuilder.setSpan(mClickableSpan2, 120, 127, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mSpannableStringBuilder.setSpan(
            colorSpan,
            105,
            119,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val colorSpan2 = ForegroundColorSpan(Color.parseColor("#fffbb700"))

        mSpannableStringBuilder.setSpan(
            colorSpan2,
            120,
            127,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_title?.setText("服务协议和隐私政策")
        tv_context_msg?.setText(mSpannableStringBuilder)
        tv_context_msg?.movementMethod = LinkMovementMethod.getInstance()
        tv_context_msg?.setHintTextColor(Color.parseColor("#fffbb700"))
    }

    fun bindShowViewData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rl_left -> {
                mOnButtonClickLinsener?.onLeftClickLinsener()
                dismiss()

            }
            R.id.rl_reght,
            R.id.tv_reght_button -> {
                mOnButtonClickLinsener?.onRightClickLinsener()
                dismiss()
            }
        }
    }

    interface OnButtonClickLinsener {
        fun onLeftClickLinsener(msg: String = "")
        fun onRightClickLinsener(msg: String = "")
    }
}