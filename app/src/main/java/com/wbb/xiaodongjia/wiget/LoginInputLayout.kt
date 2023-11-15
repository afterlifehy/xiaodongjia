package com.wbb.xiaodongjia.wiget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.alibaba.fastjson.JSONObject
import androidx.lifecycle.Observer
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.chouyou.waterbridge.mvvm.base.ErrorMessage
import com.chouyou.waterbridge.mvvm.base.SafeMutableLiveData
import com.geetest.sdk.GT3ErrorBean
import com.wbb.base.util.GraphicVerificationHelp
import com.wbb.base.util.ToastUtils
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.mvvm.viewmodel.LoginViewmodel

/**
 * Created by zj on 2021/1/27.
 */
class LoginInputLayout : FrameLayout, InputPhoneLayout.OnPhoneInputSucessLinsener,
    InputPhoneCodeLayout.OnPhoneCodeInputSucessLinsener {
    private var ihl_phone: InputPhoneLayout? = null
    private var il_phone_code: InputPhoneCodeLayout? = null
    private var mOnLoginPhoneCodeInputSucessLinsener: OnLoginPhoneCodeInputSucessLinsener? = null
    private var phone = ""//手机号，
    private var area = ""//区号
    private var phoneSucess = false
    private var codeSucess = false
    private var code = ""
    var mLoginViewmodel: LoginViewmodel? = null
    var mLifecycleOwner: LifecycleOwner? = null
    var mGraphicVerificationHelp: GraphicVerificationHelp? = null

    constructor(
        conext: Context
    ) : super(conext)

    constructor(conext: Context, attrs: AttributeSet) : super(conext, attrs)
    constructor(conext: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        conext,
        attrs,
        defStyleAttr
    )

    init {
        initView()
    }

    fun setOnLoginPhoneCodeInputSucessLinsener(
        mOnLoginPhoneCodeInputSucessLinsener: OnLoginPhoneCodeInputSucessLinsener?,
        mLoginViewmodel: LoginViewmodel?, mLifecycleOwner: LifecycleOwner
    ) {
        this.mOnLoginPhoneCodeInputSucessLinsener = mOnLoginPhoneCodeInputSucessLinsener
        this.mLoginViewmodel = mLoginViewmodel
        this.mLifecycleOwner = mLifecycleOwner
        bindError()
    }

    private fun bindError() {
        val errMsg = SafeMutableLiveData<ErrorMessage>()
        mLoginViewmodel?.registerToListen(errMsg)
        errMsg.observe(mLifecycleOwner!!, {
            if (it.code == 1000 || it.code == 1001) {
                mGraphicVerificationHelp?.dismissGeetestDialog()
                if (it.code == 1001) {
                    OnBuriedPointManager.get().getOnBuriedPointManager()
                        ?.onRegisterLoginSMS("验证码登录", phone, false)
                }
            }
            ToastUtils.showErrorToast(it.msg)

        })

        val expMsg = SafeMutableLiveData<Exception>()
        mLoginViewmodel?.registerExceptionList(expMsg)
        expMsg.observe(mLifecycleOwner!!, {
            mGraphicVerificationHelp?.dismissGeetestDialog()
        })


    }

    fun initView() {
        val mView = inflate(context, R.layout.view_login_inpu_layout, null)
        addView(mView)
        findByView(mView)
    }

    fun findByView(mView: View) {
        ihl_phone = mView.findViewById(R.id.ihl_phone)
        il_phone_code = mView.findViewById(R.id.il_phone_code)
        il_phone_code?.setOnPhoneCodeInputSucessLinsener(this)
        ihl_phone?.setOnPhoneInputSucessLinsener(this)
    }

    override fun onPhoneInputSucessLinsener(isSucess: Boolean, area: String, phone: String) {
        this.phone = phone
        this.area = area
        this.phoneSucess = isSucess
        il_phone_code?.setIsSendSms(isSucess)
        checkLogin()
    }

    /**
     * 检查是否可以去登录
     */
    fun checkLogin() {
        if (phoneSucess && codeSucess) {
            mOnLoginPhoneCodeInputSucessLinsener?.onLoginPhoneCodeInputSucess(
                true,
                area,
                phone,
                code
            )
        } else {
            mOnLoginPhoneCodeInputSucessLinsener?.onLoginPhoneCodeInputSucess(
                false,
                area,
                phone,
                code
            )
        }
    }

    interface OnLoginPhoneCodeInputSucessLinsener {
        fun onLoginPhoneCodeInputSucess(
            isSucess: Boolean,
            area: String,
            phone: String,
            code: String
        )
    }

    override fun onPhoneCodeInputSucess(codeSucess: Boolean, code: String) {
        this.codeSucess = codeSucess
        this.code = code
        checkLogin()
    }

    override fun onCheckJyCode(mInputPhoneCodeLayout: InputPhoneCodeLayout) {
        mGraphicVerificationHelp = GraphicVerificationHelp(context)
        mGraphicVerificationHelp?.startCheckGraphic(object :
            GraphicVerificationHelp.OnGraphicVerificationLinsener {
            override fun onFailed(p0: GT3ErrorBean?) {
                OnBuriedPointManager.get().getOnBuriedPointManager()
                    ?.onRegisterLoginGraphic("短信验证码登录", false)
            }

            override fun onGraphicStartSucess(mGraphicVerificationHelp: GraphicVerificationHelp) {
                OnBuriedPointManager.get().getOnBuriedPointManager()
                    ?.onRegisterLoginGraphic("短信验证码登录", true)

                mLoginViewmodel?.getJyCode(area, phone)?.observe(mLifecycleOwner!!, Observer {
                    mGraphicVerificationHelp.getGeetest(
                        org.json.JSONObject(
                            it
                        )
                    )
                })
            }

            override fun onGraphicCheckSucess(
                p0: String?,
                mGraphicVerificationHelp: GraphicVerificationHelp
            ) {
                val jsonObject = JSONObject.parseObject(p0)
                val par = HashMap<String, String>()
                par["phone"] = phone
                par["phoneArea"] = area
                par["challenge"] = jsonObject.getString("geetest_challenge")
                par["seccode"] = jsonObject.getString("geetest_seccode")
                par["validate"] = jsonObject.getString("geetest_validate")
                mLoginViewmodel?.sendMsgGeetest(par)?.observe(mLifecycleOwner!!, Observer {
                    OnBuriedPointManager.get().getOnBuriedPointManager()
                        ?.onRegisterLoginSMS("验证码登录", phone, true)
                    mGraphicVerificationHelp.dismissGeetestDialog()
                    mInputPhoneCodeLayout.startSendCodeTime()

                })
            }
        })
//
    }
}