package com.wbb.xiaodongjia.utils

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import cn.jiguang.verifysdk.api.*
import com.wbb.base.util.Constant
import com.wbb.base.util.ScreenUtils
import com.wbb.base.util.ToastUtils
import com.wbb.xiaodongjia.R


/**
 * Created by zj on 2020/11/30.
 */
object OneClickLoginManager {
    /**
     * 一键登录预处理
     */
    fun preLogin(context: Context, mPreLoginListener: PreLoginListener? = null) {
        mPreLoginListener?.let {

        }
        if (mPreLoginListener == null) {
            //极光一键登录预先取号
            JVerificationInterface.preLogin(context, 3000) { code, content ->

            }
        } else {
            JVerificationInterface.preLogin(context, 3000, mPreLoginListener)
        }

    }

    /**
     * 清理预登陆缓存
     */
    fun clearPreLoginCache() {
        JVerificationInterface.clearPreLoginCache();
    }

    /**
     * 是否支持一键登录
     */
    fun isStandByLogin(context: Context): Boolean {
        if (JVerificationInterface.isInitSuccess() && JVerificationInterface.checkVerifyEnable(
                context
            )
        ) {
            return true
        }
        return false
    }

    /**
     * 发起一键登录
     */
    fun startOnClickLogin(mContext: Context, mOnClickLoginLinsener: OnClickLoginLinsener?) {
        setCustomizeUi(mContext, mOnClickLoginLinsener)
        val settings = LoginSettings()
        settings.isAutoFinish = true //设置登录完成后是否自动关闭授权页

        settings.timeout = 15 * 1000 //设置超时时间，单位毫秒。 合法范围（0，30000],范围以外默认设置为10000

        settings.authPageEventListener = object : AuthPageEventListener() {
            override fun onEvent(cmd: Int, msg: String) {
                //do something...
            }
        } //设置授权页事件监听

        JVerificationInterface.loginAuth(mContext, settings,
            { code, content, operator ->
                if (code == 6000) {
                    mOnClickLoginLinsener?.onLoginSucesss(content, operator)
                } else if (code == 6002) {//取消一键登录

                } else {
                    mOnClickLoginLinsener?.onLoginError(code.toString())
                    ToastUtils.showErrorToast(mContext.resources.getString(R.string.one_click_login_error))
                }
            })
    }

    /**
     * 关闭登录界面
     */
    fun closeLogin() {
        JVerificationInterface.dismissLoginAuthActivity();
    }

    /**
     * 创建手机号登录view
     */
    fun createPhoneLoginView(context: Context): View {
        val inflater = LayoutInflater.from(context)
        val codeButton = inflater.inflate(R.layout.view_code_layout, null)
        val mLayoutParams1 = RelativeLayout.LayoutParams(
            (ScreenUtils.dip2px(context, 300f)),
            ScreenUtils.dip2px(context, 50f)
        )
//        val mLayoutParams1 = RelativeLayout.LayoutParams(
//            RelativeLayout.LayoutParams.MATCH_PARENT,
//            RelativeLayout.LayoutParams.WRAP_CONTENT
//        )
//        mLayoutParams1.setMargins(
//            ScreenUtils.dip2px(context, 29f),
//            ScreenUtils.dip2px(context, 350f),
//            ScreenUtils.dip2px(context, 29f),
//            0
//        )
        mLayoutParams1.setMargins(
            0,
            ScreenUtils.dip2px(context, 360f),
            0,
            0
        )
        mLayoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL)
        codeButton.setLayoutParams(mLayoutParams1)
        return codeButton
    }

    /**
     * 自定义UI
     */
    fun setCustomizeUi(context: Context, mOnClickLoginLinsener: OnClickLoginLinsener?) {
        val uiConfig = JVerifyUIConfig.Builder()
            .setAuthBGImgPath("main_bg")
            .setNavHidden(true)//不显示title
            .setNavColor(Color.WHITE)
            .setNavText("登录")
            .setNavTextColor(Color.BLACK)
            .setStatusBarDarkMode(true)
            .setNavReturnImgPath("umcsdk_return_bg")
            .setLogoWidth(217)
            .setLogoHeight(38)
            .setStatusBarColorWithNav(true)
            .setLogoHidden(false)
            .setNumberColor(Color.parseColor("#171B20"))
            .setNumberSize(18)
            .setNumberTextBold(true)
            .setLogBtnText(context.resources.getString(R.string.one_click_login))
            .setLogBtnWidth(315)
            .setLogBtnHeight(50).setLogBtnTextSize(18)
            .setLogBtnTextColor(Color.parseColor("#221503"))
            .setLogBtnImgPath("on_login_bg")
            .setAppPrivacyOne("《集你小东家APP》", Constant.USER_POLICY_URL)
            .setAppPrivacyColor(Color.parseColor("#808A96"), Color.parseColor("#171B20"))
            .setAppPrivacyTwo("《隐私政策》", Constant.USER_YS_URL)
            //.setUncheckedImgPath("on_click_login")
//            .setCheckedImgPath("on_click_login")
            .setLogoOffsetY(100)//一键登录按钮高度
            .setLogoImgPath("on_login")
            .setNumFieldOffsetY(220)//授权号码
            .setSloganOffsetY(250)//服务体系
            .setSloganTextColor(Color.parseColor("#808A96"))
            .setSloganTextSize(14)
            .setLogBtnOffsetY(300)//一键登录位置
            .setNavTransparent(false)
            .setPrivacyState(true)
            // .setPrivacyNavReturnBtn()
            .addCustomView(
                createPhoneLoginView(context),
                false
            ) { context, view ->
                //一键登录自定义事件
                mOnClickLoginLinsener?.onLoginCancel()
                closeLogin()
            }

            .setPrivacyOffsetY(30).build()
        JVerificationInterface.setCustomUIWithConfig(uiConfig)

    }

    interface OnClickLoginLinsener {
        /**
         * 一键登录成功
         */
        fun onLoginSucesss(content: String, operator: String)

        /**
         * 登录取消
         */
        fun onLoginCancel()

        /**
         * 登录异常
         */
        fun onLoginError(errorCode: String)
    }
}