package com.wbb.xiaodongjia.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.util.ARouterMap
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.xiaodongjia.R
import kotlinx.android.synthetic.main.activity_txt_show.*
import kotlinx.android.synthetic.main.layout_comment_toolbar.*

/**
 * Created by hy on 2021/3/8.
 */
@Route(path = ARouterMap.TXT_SHOW)
class TxtShowActivity : BaseDataActivityKt<BaseViewModel>(), View.OnClickListener {
    var message = ""
    override fun onReloadData() {

    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.activity_txt_show
    }

    override fun initView() {
        message = intent.getStringExtra(ARouterMap.MESSAGE).toString()
        tv_txt.text = message

        iv_back.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onGetClassTypeNam(): Any {
        return "文字"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressedSupport()
            }
        }
    }
}