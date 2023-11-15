package com.wbb.xiaodongjia.ui.activity.pay

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.ext.i18N
import com.wbb.base.util.ARouterMap
import com.wbb.base.util.GlideUtil
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.xiaodongjia.R
import kotlinx.android.synthetic.main.activity_pay_result.*
import kotlinx.android.synthetic.main.layout_comment_toolbar.*

/**
 * Created by hy on 2021/2/24.
 */
@Route(path = ARouterMap.PAY_RESULT)
class PayResultActivity : BaseDataActivityKt<BaseViewModel>(), View.OnClickListener {
    var resultType = 2
    var from = 1

    override fun onReloadData() {

    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.activity_pay_result
    }

    override fun initView() {
        resultType = intent.getIntExtra(ARouterMap.PAYMENT_STATUS, 1)
        from = intent.getIntExtra(ARouterMap.PAY_FROM, 1)
        when (resultType) {
            2 -> {
                when (from) {
                    //1消费，2充值
                    1 -> {
                        tv_payResult.text = i18N(R.string.支付成功)
                        tv_payTips.text = i18N(R.string.本次交易支付成功)
                        tv_operate.text = i18N(R.string.返回商家首页)
                    }
                    2 -> {
                        tv_payResult.text = i18N(R.string.充值成功)
                        tv_payTips.text = i18N(R.string.恭喜本次充值成功)
                        tv_operate.text = i18N(R.string.返回)
                    }
                }
                GlideUtil.loadImagePreview(R.mipmap.ic_pay_success, iv_payResult)
            }
            3 -> {
                when(from){
                    1->{
                        tv_payResult.text = i18N(R.string.支付失败)
                        tv_payTips.text = i18N(R.string.本次交易支付失败)
                        tv_operate.text = i18N(R.string.重新支付)
                    }
                    2->{
                        tv_payResult.text = i18N(R.string.充值失败)
                        tv_payTips.text = i18N(R.string.本次充值失败)
                        tv_operate.text = i18N(R.string.重新支付)
                    }
                }
                GlideUtil.loadImagePreview(R.mipmap.ic_pay_fail, iv_payResult)
            }
        }
        startListener()
    }

    private fun startListener() {
        iv_back.setOnClickListener(this)
        tv_operate.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressedSupport()
            }
            R.id.tv_operate -> {
                when (resultType) {
                    2 -> {
                        onBackPressedSupport()
                    }
                    3 -> {
                        onBackPressedSupport()
                    }
                }
            }
        }
    }

    override fun onGetClassTypeNam(): Any {
        return "支付结果"
    }
}