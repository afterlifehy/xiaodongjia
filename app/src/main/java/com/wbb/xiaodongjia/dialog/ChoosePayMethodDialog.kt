package com.wbb.xiaodongjia.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.wbb.base.dialog.BaseLibDialog
import com.wbb.base.ext.i18N
import com.wbb.xiaodongjia.R
import kotlinx.android.synthetic.main.dialog_choose_pay_method.*
import kotlinx.android.synthetic.main.dialog_choose_pay_method.rg_payMethod
import kotlinx.android.synthetic.main.dialog_choose_pay_method.tv_makeSure

/**
 * Created by hy on 2021/3/9.
 */
class ChoosePayMethodDialog(context: Context, themeResId: Int, val money: String, val callback: choosePayMethodCallback) : BaseLibDialog(context, themeResId), View.OnClickListener {
    var paymentType = "llaccp_wxpay"

    init {
        initView()
    }

    private fun initView() {
        tv_makeSure.text = i18N(R.string.确认支付) + " " + money + "元"
        startListener()
    }

    private fun startListener() {
        rg_payMethod.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_wx -> {
                    paymentType = "llaccp_wxpay"
                }
                R.id.rb_aliPay -> {
                    paymentType = "llaccp_alipay"
                }
            }
        }
        iv_close.setOnClickListener(this)
        tv_makeSure.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_close -> {
                dismiss()
            }
            R.id.tv_makeSure -> {
                callback.makeSure(paymentType)
                dismiss()
            }
        }
    }

    override fun getHeight(): Float {
        return WindowManager.LayoutParams.WRAP_CONTENT.toFloat()
    }

    override fun getLayoutResID(): Int {
        return R.layout.dialog_choose_pay_method
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

    interface choosePayMethodCallback {
        fun makeSure(paymentType: String)
    }
}