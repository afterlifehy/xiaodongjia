package com.wbb.base.wiget

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.chouyou.base.ext.gone
import com.chouyou.base.ext.show
import com.wbb.base.R
import com.wbb.base.ext.i18N
import com.wbb.base.util.InputMethodUtils
import com.wbb.base.util.ToastUtils

/**
 * Created by zj on 2021/1/28.
 * 自定义一个搜索输入框
 */
class SearchForLayout : FrameLayout, TextWatcher {
    private var et_conttent: EditText? = null
    private var iv_del: ImageView? = null
    private var mOnEditTextChangedLinsener: OnEditTextChangedLinsener? = null
    private var mOnEditorActionListener: OnEditorActionListener? = null

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

    fun initView() {
        val mView = inflate(context, R.layout.view_search_for_layout, null)
        addView(mView)
        findByView(mView)
    }

    fun findByView(mView: View) {
        et_conttent = mView.findViewById(R.id.et_conttent)
        iv_del = mView.findViewById(R.id.iv_del)
        iv_del?.setOnClickListener {
            et_conttent?.setText("")
        }
        et_conttent?.addTextChangedListener(this)
        et_conttent?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                mOnEditorActionListener?.onEditorAction(v, actionId, event)!!
                return false
            }
        })
    }

    /**
     * 设置提示信息
     */
    fun setHintText(str: String) {
        et_conttent?.hint = str
    }

    fun getEditString(): String {
        return et_conttent?.text.toString()
    }

    fun setEditString(str: String) {
        et_conttent?.setText(str)
    }

    /**
     * 设置文字监听回调
     */
    fun setOnEditTextChangedLinsener(mOnEditTextChangedLinsener: OnEditTextChangedLinsener? = null) {
        this.mOnEditTextChangedLinsener = mOnEditTextChangedLinsener
    }

    /**
     * 设置搜索回调
     */
    fun setOnEditorActionListener(mOnEditorActionListener: OnEditorActionListener? = null) {
        this.mOnEditorActionListener = mOnEditorActionListener
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkShowDelete(s.toString())
    }

    override fun afterTextChanged(s: Editable?) {
        mOnEditTextChangedLinsener?.onInpuTextChangedLinsener(s.toString())
    }

    fun checkShowDelete(str: String) {
        if (TextUtils.isEmpty(str)) {
            iv_del?.visibility = View.GONE
        } else {
            iv_del?.visibility = View.VISIBLE
        }
    }

    interface OnEditTextChangedLinsener {
        fun onInpuTextChangedLinsener(str: String)
    }

    interface OnEditorActionListener {
        fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?)
    }
}