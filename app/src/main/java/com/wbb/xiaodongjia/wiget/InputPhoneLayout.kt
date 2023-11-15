package com.wbb.xiaodongjia.wiget

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.wbb.base.observer.ObserverKey
import com.wbb.base.bean.CountrySortBean
import com.wbb.base.observer.ObserverManger
import com.wbb.base.observer.OnObserver
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.ui.activity.login.ChooseAcountryActivity

/**
 * Created by zj on 2021/1/27.
 */
class InputPhoneLayout : FrameLayout, OnObserver {
    private var tv_select_area: TextView? = null
    private var tv_phone: EditText? = null
    private var mOnPhoneInputSucessLinsener: OnPhoneInputSucessLinsener? = null
    private var iv_del: ImageView? = null

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
        ObserverManger.getInstance(ObserverKey.COUNTRY_CODE).registerObserver(this)
    }

    /**
     * 设置输入回调监听
     */
    fun setOnPhoneInputSucessLinsener(mOnPhoneInputSucessLinsener: OnPhoneInputSucessLinsener?) {
        this.mOnPhoneInputSucessLinsener = mOnPhoneInputSucessLinsener
    }

    fun initView() {
        val mView = inflate(context, R.layout.view_input_phone_layout, null)
        addView(mView)
        findByView(mView)
    }

    fun findByView(mView: View) {
        tv_select_area = mView.findViewById(R.id.tv_select_area)
        tv_phone = mView.findViewById(R.id.tv_phone)
        iv_del = mView.findViewById(R.id.iv_del)
        tv_phone?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPhoneInputSucess(s.toString())
                isShowDelectButton(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        tv_select_area?.setOnClickListener {
            ChooseAcountryActivity.startIntent(context)
        }
        iv_del?.setOnClickListener {
            tv_phone?.setText("")
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ObserverManger.getInstance(ObserverKey.COUNTRY_CODE).removeObserver(this)
    }

    /**
     * 判断删除控件是否显示
     */
    fun isShowDelectButton(str: String) {
        if (TextUtils.isEmpty(str)) {
            iv_del?.visibility = GONE
        } else {
            iv_del?.visibility = VISIBLE
        }
    }

    /**
     * 判断手机号是否输入合法
     */
    fun checkPhoneInputSucess(phone: String) {
        val area = tv_select_area?.text.toString()
        var empPhone = phone
        if (TextUtils.isEmpty(empPhone)) {
            empPhone = tv_phone?.text.toString()
        }
        if (TextUtils.isEmpty(empPhone) || TextUtils.isEmpty(area) || (area.equals("+86") && empPhone.length < 11)) {
            mOnPhoneInputSucessLinsener?.onPhoneInputSucessLinsener(false, "", "")
        } else {
            mOnPhoneInputSucessLinsener?.onPhoneInputSucessLinsener(true, area, empPhone)

        }

    }

    interface OnPhoneInputSucessLinsener {
        fun onPhoneInputSucessLinsener(isSucess: Boolean, area: String, phone: String)
    }

    override fun update(obj: Any?) {
        if (obj is CountrySortBean) {
            tv_select_area?.setText(obj.countryNumber)
        }
    }
}