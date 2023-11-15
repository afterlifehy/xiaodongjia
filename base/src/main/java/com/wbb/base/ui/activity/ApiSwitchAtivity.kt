package com.wbb.base.ui.activity

import android.util.Log
import android.view.View
import com.wbb.base.R
import com.wbb.base.event.ChangeApiEvent
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.pf.PreferencesHelp
import com.wbb.base.util.UserInfoManager
import com.wbb.base.viewbase.BaseDataActivityKt
import kotlinx.android.synthetic.main.activity_api_switch.*
import kotlinx.android.synthetic.main.layout_base_toolbar.*
import org.greenrobot.eventbus.EventBus

class ApiSwitchAtivity : BaseDataActivityKt<BaseViewModel>(), View.OnClickListener {

    override fun isRegEventBus(): Boolean {
        return true
    }

    override fun onReloadData() {

    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.activity_api_switch
    }

    override fun initView() {
        iv_back.setOnClickListener(this)
        tv_develop.setOnClickListener(this)
        tv_test.setOnClickListener(this)
        tv_pre.setOnClickListener(this)
        tv_formal.setOnClickListener(this)
    }

    override fun initData() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressedSupport()
            }
            R.id.tv_develop -> {
                changeApi(1)
                finish()
            }
            R.id.tv_test -> {
                changeApi(2)
                finish()
            }
            R.id.tv_pre -> {
                changeApi(3)
                finish()
            }
            R.id.tv_formal -> {
                changeApi(4)
                finish()
            }
        }
    }

    fun changeApi(mode: Int) {
        PreferencesHelp.putDevValue(mode)
        UserInfoManager.instance().clearUserInfo()
        EventBus.getDefault().post(ChangeApiEvent(mode))
    }

    override fun onBackPressedSupport() {
        super.onBackPressedSupport()
    }

    override fun onGetClassTypeNam(): Any {
        return "api切换"
    }

    override fun onDestroy() {
        super.onDestroy()
        iv_back.setOnClickListener(null)
        tv_formal.setOnClickListener(null)
        tv_pre.setOnClickListener(null)
        tv_test.setOnClickListener(null)
    }

}