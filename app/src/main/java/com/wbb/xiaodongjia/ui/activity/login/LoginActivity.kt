package com.wbb.xiaodongjia.ui.activity.login

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.ext.i18N
import com.wbb.base.help.AppExitManager
import com.wbb.base.util.*
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.mvvm.viewmodel.LoginViewmodel
import com.wbb.xiaodongjia.ui.activity.MainActivity
import com.wbb.xiaodongjia.ui.activity.help.InputKeyHelp
import com.wbb.xiaodongjia.wiget.LoginInputLayout
import kotlinx.android.synthetic.main.veiw_login_layout.*

/**
 * Created by zj on 2021/1/27.
 */
@Route(path = ARouterMap.LOGIN)
class LoginActivity : BaseDataActivityKt<LoginViewmodel>(),
    LoginInputLayout.OnLoginPhoneCodeInputSucessLinsener, View.OnClickListener {
    private var mInputKeyHelp: InputKeyHelp? = null
    private var code = ""
    private var phone = ""
    private var area = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setTranslucentStatus(this)
        StatusBarUtils.setNavigationBarColor(this, R.color.white)
    }

    override fun providerVMClass(): Class<LoginViewmodel>? {
        return LoginViewmodel::class.java
    }

    override fun onReloadData() {

    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.veiw_login_layout
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mException.observe(this@LoginActivity, Observer {
                //ToastUtils.showErrorToast(it.message)
                dismissProgressDialog()

            })
            errMsg.observe(this@LoginActivity, Observer {
                when (it.code) {
                    1002 -> {
                        ToastUtils.showErrorToast(it.msg)
                        OnBuriedPointManager.get().getOnBuriedPointManager()
                            ?.onLoginResult("验证码登录", false, it.msg)
                    }
                }
                dismissProgressDialog()
            })
            mSmsLogin.observe(this@LoginActivity, Observer {
                OnBuriedPointManager.get().getOnBuriedPointManager()
                    ?.onLoginResult("验证码登录", true, "")
                dismissProgressDialog()
                UserInfoManager.instance().saveUserInfo(it)
                ToastUtils.showSucessToast(i18N(R.string.login_sucess))
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            })
        }
    }

    override fun initView() {
        ll_phone_code.setOnLoginPhoneCodeInputSucessLinsener(this, mViewModel, this)
        mInputKeyHelp = InputKeyHelp(this)
        tv_login_butt.setOnClickListener(this)
        tv_userPolicy.setOnClickListener(this)
        ys_xy.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onDestroy() {
        super.onDestroy()
        mInputKeyHelp?.onDestroy()
    }

    override fun onGetClassTypeNam(): Any {
        return "登录"
    }

    override fun onLoginPhoneCodeInputSucess(
        isSucess: Boolean,
        area: String,
        phone: String,
        code: String
    ) {
        this.code = code
        this.phone = phone
        this.area = area
        if (isSucess) {
            tv_login_butt.isEnabled = true
        } else {
            tv_login_butt.isEnabled = false

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_login_butt -> {
                if (!cb_chekc.isChecked) {
                    ToastUtils.showToash("请勾选协议")
                    return
                }
                OnBuriedPointManager.get().getOnBuriedPointManager()?.OneClickLogin("验证码登录")
                val par = HashMap<String, String>()
                par["msgCode"] = code
                par["phone"] = phone
                par["phoneArea"] = area.replace("+", "")
                mViewModel.loginSms(par)
                showProgressDialog()

            }
            R.id.tv_userPolicy -> {
                ARouter.getInstance().build(ARouterMap.WEBVIEW)
                    .withString(ARouterMap.URL, Constant.USER_POLICY_URL)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
            R.id.ys_xy -> {//跳转到隐私协议
                ARouter.getInstance().build(ARouterMap.WEBVIEW)
                    .withString(ARouterMap.URL, Constant.USER_YS_URL)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (AppExitManager.instance().checkIsExit(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}