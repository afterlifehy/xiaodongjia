package com.wbb.xiaodongjia.ui.activity.pay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.chouyou.base.ext.gone
import com.chouyou.base.ext.show
import com.lianlianpay.cashier.PayService
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.wbb.base.BuildConfig
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.buriedpoint.ShenCeManagerKt
import com.wbb.base.dialog.LoadingDialog
import com.wbb.base.ext.i18N
import com.wbb.base.ext.isEmpty
import com.wbb.base.observer.ObserverKey
import com.wbb.base.observer.ObserverManger
import com.wbb.base.observer.OnObserver
import com.wbb.base.util.*
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.bean.PayResult
import com.wbb.xiaodongjia.mvvm.viewmodel.BillViewModel
import kotlinx.android.synthetic.main.activity_pay_bill.*
import kotlinx.android.synthetic.main.layout_comment_toolbar.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * Created by hy on 2021/2/23.
 */
@Route(path = ARouterMap.PAYBILL)
class PayBillActivity : BaseDataActivityKt<BillViewModel>(), View.OnClickListener, OnObserver {
    var consumeTotal = 0.00
    var merchantId = ""
    var paymentType = 1 //0商户余额支付 1PLUS余额支付 2支付宝支付 3微信支付 4混合支付 5线下支付
    var plusAmount = 0.00
    var merchantAmount = 0.0
    var orderId = ""
    var loadingDialog: LoadingDialog? = null

    override fun onReloadData() {

    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun providerVMClass(): Class<BillViewModel>? {
        return BillViewModel::class.java
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_pay_bill
    }

    override fun initView() {
        merchantId = intent.getStringExtra(ARouterMap.MERCHANTID).toString()

        tv_title.text = i18N(R.string.买单)
        et_consumeTotal.isFocusable = true
        et_consumeTotal.isFocusableInTouchMode = true
        et_consumeTotal.requestFocus()
        startListener()
        //默认显示plush会员支付
        tv_balanceChangeTitle.text = i18N(R.string.PLUS余额)
        rl_balanceChange.show()
    }

    /**
     * 计算金额
     */
    fun checkPayment() {
        var mBalance = 0.0
        when (paymentType) {
            1 -> {
                mBalance = plusAmount
            }
            0 -> {
                mBalance = merchantAmount
            }

        }
        var text = et_consumeTotal.text.toString()
        val s = et_consumeTotal.text
        if (text.contains(".") && text.length - 1 - text.indexOf(".") > 2) {
            et_consumeTotal.setText(BigDecimalUtils.toDownValue(text, 2))
            et_consumeTotal.setSelection(et_consumeTotal.text.toString().length - 1)
            return
        } else {
            if (text.length > 1 && text.startsWith("0")) {
                if (!TextUtils.equals(s?.substring(1, 2), ".")) {
                    s?.replace(0, 1, "")
                }
            } else if (text.startsWith(".")) {
                et_consumeTotal.setText("0.")
                et_consumeTotal.setSelection(2)
                return
            }
        }
        if (!isEmpty(text)) {
            var mHandleValue = text
            if (mBalance > 0) {//如果余额大于0，就需要减掉可用余额
                if (mBalance > text.toDouble()) {//如果余额大于支付金额，就应付为0
                    mHandleValue = "0.00"
                } else {
                    val endBalance = text.toDouble() - mBalance
                    if (endBalance <= 0) {
                        mHandleValue = "0.00"
                    } else {
                        mHandleValue = endBalance.toString()

                    }
                }
            }
            consumeTotal = BigDecimalUtils.toDownValue(text, 2).toDouble()
            tv_consumeAmount.text = "¥" + BigDecimalUtils.toDownValue(text, 2)
            tv_shouldPay.text = "¥" + BigDecimalUtils.toDownValue(mHandleValue, 2)
            tv_balanceChange.text = "-¥" + BigDecimalUtils.toDownValue(text, 2)
            tv_makeSure.text = i18N(R.string.确认支付) + " " + BigDecimalUtils.toDownValue(
                BigDecimalUtils.scienceToString(consumeTotal.toString()), 2
            ) + "元"
        } else {
            consumeTotal = 0.00
            tv_consumeAmount.text = "¥0.00"
            tv_shouldPay.text = "¥0.00"
            tv_balanceChange.text = "-¥0.00"
            tv_makeSure.text = i18N(R.string.确认支付) + " 0.00元"
        }
    }

    private fun startListener() {
        et_consumeTotal.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                checkPayment()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        et_notPreferentialAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.contains(".") && text.length - 1 - text.indexOf(".") > 2) {
                    et_notPreferentialAmount.setText(BigDecimalUtils.toDownValue(text, 2))
                    et_notPreferentialAmount.setSelection(et_notPreferentialAmount.text.toString().length - 1)
                    return
                } else {
                    if (text.length > 1 && text.startsWith("0")) {
                        if (!TextUtils.equals(s?.substring(1, 2), ".")) {
                            s?.replace(0, 1, "")
                        }
                    } else if (text.startsWith(".")) {
                        et_notPreferentialAmount.setText("0.")
                        et_notPreferentialAmount.setSelection(2)
                        return
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        rg_payMethod.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_plus -> {
                    paymentType = 1
                    tv_balanceChangeTitle.text = i18N(R.string.PLUS余额)
                    rl_balanceChange.show()
                    checkPayment()
                }
                R.id.rb_dong -> {
                    paymentType = 0
                    tv_balanceChangeTitle.text = i18N(R.string.东家会员余额)
                    rl_balanceChange.show()
                    checkPayment()
                }
                R.id.rb_wx -> {
                    paymentType = 7
                    rl_balanceChange.gone()
                    checkPayment()
                }
                R.id.rb_aliPay -> {
                    paymentType = 8
                    rl_balanceChange.gone()
                    checkPayment()
                }
            }
        }
        iv_back.setOnClickListener(this)
        tv_makeSure.setOnClickListener(this)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mPayBalanceLiveData.observe(this@PayBillActivity, Observer {
                plusAmount = it.plusAmount
                merchantAmount = it.merchantAmount
                tv_plusBalance.text = "(当前余额 ¥${plusAmount})"
                tv_dongBalance.text = "(当前余额 ¥${merchantAmount})"
            })
            errMsg.safeObserve(this@PayBillActivity, Observer {
                ToastUtils.showErrorToast(it.msg)
            })
            mPayResultLiveData.observe(this@PayBillActivity, Observer {
                when (paymentType) {
                    0,
                    1 -> {
                        when (it.paymentStatus) {
                            2 -> {
                                ARouter.getInstance().build(ARouterMap.PAY_RESULT)
                                    .withInt(ARouterMap.PAYMENT_STATUS, it.paymentStatus)
                                    .withInt(ARouterMap.PAY_FROM, 1)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                                finish()
                            }
                            3 -> {
                                ARouter.getInstance().build(ARouterMap.PAY_RESULT)
                                    .withInt(ARouterMap.PAYMENT_STATUS, it.paymentStatus)
                                    .withInt(ARouterMap.PAY_FROM, 1)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                                et_consumeTotal.setText("")
                            }
                        }
                    }
                    7 -> {
//                        val payChannelParamsBean = GsonUtils.fromJson(it.payChannelParams, PayChannelParamsBean::class.java)
//                        val api: IWXAPI = WXAPIFactory.createWXAPI(BaseApplication.instance(), null)
//                        val request = PayReq()
//                        request.appId = payChannelParamsBean.appid//子商户appid
//                        request.partnerId = payChannelParamsBean.partnerid//子商户号
//                        request.prepayId = payChannelParamsBean.prepayid
//                        request.packageValue = payChannelParamsBean.`package`
//                        request.nonceStr = payChannelParamsBean.noncestr
//                        request.timeStamp = payChannelParamsBean.timestamp
//                        request.sign = payChannelParamsBean.sign
//                        api.sendReq(request)
                    }
                    8 -> {
                        var orderInfo = it.payChannelParams
//                        var orderInfo = "alipay_root_cert_sn=687b59193f3f462dd5336e5abf83c5d8_02941eef3187dddf3d3b83462e1dfcf6&alipay_sdk=alipay-sdk-java-4.10.182.ALL&app_cert_sn=8ccb51b84bb6d1c6a93cc064f0bcfd99&app_id=2021000117605967&biz_content=%7B%22body%22%3A%22%E7%94%A8%E6%88%B7%E8%B4%AD%E4%B9%B0%22%2C%22out_trade_no%22%3A%22031010461316153%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22%E7%94%A8%E6%88%B7%E8%B4%AD%E4%B9%B0%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%220.01%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fceaf486287e5.ngrok.io%2Fpay%2Fcert_notify_url&sign=dDgECgodPpePV5HMVdwUHprQE3uPkNWKSHEJgTMjxHO4rCcS2odqgt89vyLQZnQ538siEP%2F8yOTbg7X6FsfqlI%2BzrRlS4GlnMkOoMJ%2B2tirU6UlVr7iHFTsg1ZUIzj7HqC2DwjcUtyOW4oaVTyXG1dvWc7bIHBpQu02hLDkFQ6vtjP7KYQra%2F76Gl3qY4SEgLX%2FASucM4K1qHf7kxMuhjamB1nPGvN8lSEYzVaC3lH7jUtjsjnVQQy6hKVXQ0i6%2Fchi83Whz86X0kyZX5TYXa6VAaounWRHDnQCzgdfhptpqXgG7FW4Iyb3TSgJf%2ByQ0sOEWNtKzWxehAsosPnqG3A%3D%3D&sign_type=RSA2&timestamp=2021-03-10+10%3A46%3A13&version=1.0"
//                        PayService.pay(this@PayBillActivity,mHandler,"","")
                    }
                }
            })
            mAddOrderLiveData.observe(this@PayBillActivity, Observer {
                orderId = it.orderId
                callWxApplet(orderId)
            })
            mPayStatusLiveData.observe(this@PayBillActivity, Observer {
                when (it.status) {
                    1 -> {
                        Handler(Looper.getMainLooper()).postDelayed({ getPayStatus() }, 1000)
                    }
                    2 -> {
                        ARouter.getInstance().build(ARouterMap.PAY_RESULT)
                            .withInt(ARouterMap.PAYMENT_STATUS, 2).withInt(ARouterMap.PAY_FROM, 1)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                        loadingDialog?.dismiss()
                    }
                    3,
                    4 -> {
                        ARouter.getInstance().build(ARouterMap.PAY_RESULT)
                            .withInt(ARouterMap.PAYMENT_STATUS, 3).withInt(ARouterMap.PAY_FROM, 1)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                        loadingDialog?.dismiss()
                    }
                }
            })
            mException.observe(this@PayBillActivity, Observer {
                LogUtil.v(it.toString())
            })
        }
    }

    private fun callWxApplet(orderId: String) {
        val appId: String = Constant.WX_APP_ID // 填应用AppId
        val api = WXAPIFactory.createWXAPI(this, appId)

        val req = WXLaunchMiniProgram.Req()
        req.userName = Constant.WX_APPLET_ID // 填小程序原始id
        req.path =
            "/pages/logs/logs?orderid=${orderId}&amount=${et_consumeTotal.text}" //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        req.miniprogramType = if (BuildConfig.ISDEV == 4) {//正式环境
            WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
        } else {
            WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW
        } // 可选打开 开发版，体验版和正式版
        api.sendReq(req)
    }

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what === PayService.PAY) {
                if (msg.obj == null || (msg.obj as JSONObject).size === 0) {

                } else {
                    val result = msg.obj.toString()

                }
            }
            val payResult = PayResult(msg.obj as Map<String?, String?>)
            when (payResult.resultStatus) {
                "9000" -> {
                    ARouter.getInstance().build(ARouterMap.PAY_RESULT)
                        .withInt(ARouterMap.PAYMENT_STATUS, 2).withInt(ARouterMap.PAY_FROM, 1)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                }
                else -> {
                    ARouter.getInstance().build(ARouterMap.PAY_RESULT)
                        .withInt(ARouterMap.PAYMENT_STATUS, 3).withInt(ARouterMap.PAY_FROM, 1)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                }
            }
            ToastUtils.showSucessToast(payResult.result)
        }
    }

    override fun initData() {
        ObserverManger.getInstance(ObserverKey.WEXIN_PAY_RESULT).registerObserver(this)
        getData()
    }

    override fun getData() {
        super.getData()
        getPay()
    }

    private fun getPay() {
        var par = HashMap<String, String>()
        par["merchantId"] = merchantId
        mViewModel.getPay(par)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressedSupport()
            }
            R.id.tv_makeSure -> {
                if (consumeTotal <= 0) {
                    ToastUtils.showErrorToast(i18N(R.string.input_moey_not_null))
                    return
                }
                when (paymentType) {
                    0 -> {
                        if (consumeTotal > merchantAmount) {
                            ToastUtils.showErrorToast(i18N(R.string.余额不足))
                            return
                        }
                        OnBuriedPointManager.get().getOnBuriedPointManager()?.PayOrder(
                            if (et_notPreferentialAmount.text.toString()
                                    .isEmpty()
                            ) "0.00" else et_notPreferentialAmount.text.toString(),
                            et_consumeTotal.text.toString(),
                            "0.00",
                            et_consumeTotal.text.toString(),
                            "PLUS支付",
                            "支付成功",
                            "", merchantId
                        )
                    }
                    1 -> {
                        if (consumeTotal > plusAmount) {
                            ToastUtils.showErrorToast(i18N(R.string.余额不足))
                            return
                        }
                        OnBuriedPointManager.get().getOnBuriedPointManager()?.PayOrder(
                            if (et_notPreferentialAmount.text.toString()
                                    .isEmpty()
                            ) "0.00" else et_notPreferentialAmount.text.toString(),
                            "0.00",
                            et_consumeTotal.text.toString(),
                            et_consumeTotal.text.toString(),
                            "东家支付",
                            "支付成功",
                            "", merchantId
                        )
                    }
                    7 -> {
                        OnBuriedPointManager.get().getOnBuriedPointManager()?.PayOrder(
                            if (et_notPreferentialAmount.text.toString()
                                    .isEmpty()
                            ) "0.00" else et_notPreferentialAmount.text.toString(),
                            "0.00",
                            "0.00",
                            et_consumeTotal.text.toString(),
                            "微信支付",
                            "支付成功",
                            "",
                            merchantId
                        )
                    }
                    8 -> {
                        OnBuriedPointManager.get().getOnBuriedPointManager()?.PayOrder(
                            if (et_notPreferentialAmount.text.toString()
                                    .isEmpty()
                            ) "0.00" else et_notPreferentialAmount.text.toString(),
                            "0.00",
                            "0.00",
                            et_consumeTotal.text.toString(),
                            "支付宝支付",
                            "支付成功",
                            "",
                            merchantId
                        )
                    }
                }
                goPay()
            }
        }
    }


    fun goPay() {
        var paymentTypeList = ArrayList<Int>()
        paymentTypeList.add(paymentType)
        var par = HashMap<String, Any>()
        par["amount"] = et_consumeTotal.text.toString()
        par["merchantId"] = merchantId
        par["noDiscountAmount"] = if (et_notPreferentialAmount.text.toString()
                .isEmpty()
        ) "0.00" else et_notPreferentialAmount.text.toString()
        par["orderType"] = "1"
        par["payScene"] = "1"
        par["paymentType"] = paymentTypeList
        if (paymentType == 7) {
            mViewModel.addOrder(par)
        } else {
            mViewModel.addOrderAndPay(par)
        }
    }

    override fun onGetClassTypeNam(): Any {
        return "买单"
    }

    override fun update(obj: Any?) {
        if (obj is BaseResp) {
            when (obj.errCode) {
                BaseResp.ErrCode.ERR_OK -> {
                    loadingDialog = LoadingDialog(this, i18N(R.string.等待返回支付结果))
                    loadingDialog?.showDialog()
                    getPayStatus()
                }
            }

        }
    }

    fun getPayStatus() {
        val par = HashMap<String, String>()
        par["orderId"] = orderId
        mViewModel.getPayStatus(par)
    }

    override fun onDestroy() {
        super.onDestroy()
        ObserverManger.getInstance(ObserverKey.WEXIN_PAY_RESULT).removeObserver(this)
    }
}