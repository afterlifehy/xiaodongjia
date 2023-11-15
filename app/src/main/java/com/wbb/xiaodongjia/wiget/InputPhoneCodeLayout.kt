package com.wbb.xiaodongjia.wiget

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import com.wbb.base.util.LanguageUtils
import com.wbb.base.util.RxTimerUtil
import com.wbb.xiaodongjia.R

/**
 * Created by zj on 2021/1/27.
 * 登录手机输入框
 */
class InputPhoneCodeLayout : FrameLayout, View.OnClickListener {
    private var tv_get_code_button: TextView? = null
    private var met_phone_code: EditText? = null
    private var mRxTimerUtil: RxTimerUtil? = null
    private var isSendMsg = false
    private var mOnPhoneCodeInputSucessLinsener: OnPhoneCodeInputSucessLinsener? = null

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
        mRxTimerUtil = RxTimerUtil()
        initView()
    }

    override fun detachViewFromParent(child: View?) {
        super.detachViewFromParent(child)
        mRxTimerUtil?.cancel()
    }

    fun initView() {
        val mView = inflate(context, R.layout.view_input_phone_code_layout, null)
        addView(mView)
        findByView(mView)
    }

    fun setOnPhoneCodeInputSucessLinsener(mOnPhoneCodeInputSucessLinsener: OnPhoneCodeInputSucessLinsener) {
        this.mOnPhoneCodeInputSucessLinsener = mOnPhoneCodeInputSucessLinsener
    }

    fun findByView(mView: View) {
        met_phone_code = mView.findViewById(R.id.et_phone_code)
        tv_get_code_button = mView.findViewById(R.id.tv_get_code_button)
        met_phone_code?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkCode(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        tv_get_code_button?.setOnClickListener(this)
    }

    fun checkCode(code: String) {
        if (!TextUtils.isEmpty(code) && code.length == 4) {
            mOnPhoneCodeInputSucessLinsener?.onPhoneCodeInputSucess(true, code)
        } else {
            mOnPhoneCodeInputSucessLinsener?.onPhoneCodeInputSucess(false, code)

        }
    }

    /**
     * 设置是否可以点击发送验证码按钮，以及改变颜色
     */
    fun setIsSendSms(isCheck: Boolean) {
        if (isSendMsg) {//正在倒计时就不去更改状态，需要等倒计时结束
            return
        }
        tv_get_code_button?.isEnabled = isCheck
        if (isCheck) {
            tv_get_code_button?.setTextColor(resources!!.getColor(R.color.color_FFBA00))
        } else {
            tv_get_code_button?.setTextColor(resources!!.getColor(R.color.color_B7BEC7))
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_get_code_button -> {
                mOnPhoneCodeInputSucessLinsener?.onCheckJyCode(this)
            }
        }


    }

    /**
     * 开始倒计时
     */
    fun startSendCodeTime() {
        if (isSendMsg) {
            return
        }
        isSendMsg = true
        mRxTimerUtil?.interval(1000, object : RxTimerUtil.IRxNext {
            override fun doNext(number: Long) {
                upTime(number)
            }
        }, 60)

    }

    fun upTime(time: Long) {
        if (time < 60) {
            tv_get_code_button?.setText("${60 - time}'")
            tv_get_code_button?.setTextColor(resources!!.getColor(R.color.color_171B20))
        } else {
            isSendMsg = false
            tv_get_code_button?.setText(LanguageUtils.getString(R.string.get_phone_code))
            tv_get_code_button?.setTextColor(resources!!.getColor(R.color.color_FFBA00))

        }
    }

    interface OnPhoneCodeInputSucessLinsener {
        fun onPhoneCodeInputSucess(codeSucess: Boolean, code: String)

        /**
         * 发送验证码要先去严重极验
         */
        fun onCheckJyCode(mInputPhoneCodeLayout: InputPhoneCodeLayout)
    }
}