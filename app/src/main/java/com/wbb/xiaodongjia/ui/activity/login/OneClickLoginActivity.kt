package com.wbb.xiaodongjia.ui.activity.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.base.util.ToastUtils
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.mvvm.viewmodel.LoginViewmodel
import com.wbb.xiaodongjia.ui.activity.MainActivity
import com.wbb.xiaodongjia.utils.OneClickLoginManager
import com.wbb.base.util.UserInfoManager

/**
 * Created by zj on 2020/12/2.
 * 一键登录主界面，这里只做弹出一键登录界面，
 * 和判断不能支持一键登录，就跳转到登录界面
 */

class OneClickLoginActivity : BaseDataActivityKt<LoginViewmodel>() {
    var from = 0
    var isStartOnLogin = false

    companion object {
        const val FROM_TYPE = "from_type"
        fun startIntent(context: Context, from: Int = 0) {
            val intent = Intent(context, OneClickLoginActivity::class.java)
            intent.putExtra(FROM_TYPE, from)
            context.startActivity(intent)

        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mOnLogin.observe(this@OneClickLoginActivity, {
                dismissProgressDialog()
                OnBuriedPointManager.get().getOnBuriedPointManager()
                    ?.onLoginResult("一键登录", true, "")
                UserInfoManager.instance().saveUserInfo(it)
                startActivity(Intent(this@OneClickLoginActivity, MainActivity::class.java))
                finish()
            })
            errMsg.observe(this@OneClickLoginActivity, {
                dismissProgressDialog()
                OnBuriedPointManager.get().getOnBuriedPointManager()
                    ?.onLoginResult("一键登录", false, it.msg)
                ToastUtils.showErrorToast(it.msg)
                toLoginActivity(1)
                finish()
            })
        }
    }

    override fun providerVMClass(): Class<LoginViewmodel>? {
        return LoginViewmodel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentValue()
        startOneLogin()
    }

    override fun onReloadData() {
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        getIntentValue()
        startOneLogin()
    }

    override fun onGetClassTypeNam(): Any {
        return "一键登录"
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.activity_one_login
    }

    override fun initView() {
    }

    fun removeActivityCall() {

    }

    /**
     * 启动一键登录
     */
    fun startOneLogin() {
        if (OneClickLoginManager.isStandByLogin(this)) {//当前支持一键登录就去登录
            isStartOnLogin = true
            OneClickLoginManager.startOnClickLogin(
                this,
                object : OneClickLoginManager.OnClickLoginLinsener {
                    override fun onLoginCancel() {
                        isStartOnLogin = false
                        removeActivityCall()
                        toLoginActivity(1)

                    }

                    override fun onLoginError(errorCode: String) {
                        isStartOnLogin = false
                        removeActivityCall()
                        toLoginActivity(2)
                        OnBuriedPointManager.get().getOnBuriedPointManager()
                            ?.onLoginResult("一键登录", false, "一键登录错误:${errorCode}")
                    }

                    override fun onLoginSucesss(content: String, operator: String) {
                        OnBuriedPointManager.get().getOnBuriedPointManager()
                            ?.OneClickLogin("一键登录")
                        showProgressDialog()
                        removeActivityCall()
                        isStartOnLogin = false
                        val parmap = HashMap<String, String>()
                        parmap["loginToken"] = content
                        showProgressDialog()
                        mViewModel.loginTokenVerify(parmap)


                    }
                })
        } else {
            toLoginActivity(3)
        }
    }

    fun getIntentValue() {
        from = intent.getIntExtra(FROM_TYPE, 0)
    }

    override fun initData() {
    }

    fun toLoginActivity(index: Int) {
        val intent = Intent(this@OneClickLoginActivity, LoginActivity::class.java)
        intent.putExtra("from", from)
        startActivity(intent)
        finish()

    }

    override fun onDestroy() {
        super.onDestroy()
        removeActivityCall()
    }


}