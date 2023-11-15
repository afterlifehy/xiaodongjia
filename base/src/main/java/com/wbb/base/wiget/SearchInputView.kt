package com.wbb.base.wiget

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.TextView
import com.chouyou.base.ext.gone
import com.chouyou.base.ext.show
import com.wbb.base.R
import com.wbb.base.util.InputMethodUtils
import com.wbb.base.util.LanguageUtils
import com.wbb.base.util.ToastUtils
import kotlinx.android.synthetic.main.view_search_input_layout.view.*

/**
 * Created by zj on 2021/2/22.
 */
class SearchInputView : FrameLayout {
    private var mOnSearchInputLinsener: OnSearchInputLinsener? = null

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
        View.inflate(context, R.layout.view_search_input_layout, this)
        initView()
    }

    private fun initView() {
        et_search.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!TextUtils.isEmpty(et_search.text.toString().trim())) {
                        InputMethodUtils.hideSoftInput(v)
                    } else {
                        ToastUtils.showToash(LanguageUtils.getString(R.string.请输入搜索内容))

                    }
                    mOnSearchInputLinsener?.onUserClickSearch(et_search.text.trim().toString())
                }
                return false
            }
        })
        et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                mOnSearchInputLinsener?.onInputText(s.toString())
            }

        })
        tv_cancel.setOnClickListener {
            if (TextUtils.isEmpty(et_search.text.toString())) {
                mOnSearchInputLinsener?.onCancer(et_search.text.toString())
            } else {
                et_search.setText("")
            }
        }

    }

    fun setSearchText(string: String) {
        et_search.setText(string)
        et_search.setSelection(string.length)
    }

    fun getSearchText(): String {
        return et_search.text.toString()
    }

    /**
     * 传递需要的值
     */
    fun initSearch(
        hintTex: String = "",
        isShowCancerButton: Boolean = false,
        mOnSearchInputLinsener: OnSearchInputLinsener
    ) {
        this.mOnSearchInputLinsener = mOnSearchInputLinsener
        et_search.setHint(hintTex)
        if (isShowCancerButton) {
            tv_cancel.show()
        } else {
            tv_cancel.gone()
        }

    }

    interface OnSearchInputLinsener {
        //取消
        fun onCancer(string: String)

        /**
         * 文字输入回调
         */
        fun onInputText(string: String)

        /**
         * 用户点击搜索
         */
        fun onUserClickSearch(string: String)
    }
}