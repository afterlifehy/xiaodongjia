package com.wbb.xiaodongjia.http.interceptor

import android.app.Dialog
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.alibaba.android.arouter.launcher.ARouter
import com.wbb.base.dialog.DialogHelp
import com.wbb.base.help.ActivityCacheManager
import com.wbb.base.help.AppExitManager
import com.wbb.base.util.ARouterMap
import com.wbb.base.util.GsonUtils
import com.wbb.base.util.UserInfoManager
import com.wbb.xiaodongjia.bean.LoginExpiredCheckData
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException

/**
 * Created by zj on 2018/1/24 0024.
 * 登录过期拦截器
 */
class LoginExpiredInterceptor : Interceptor {
    private var mHandler: LoginExpiredHandler


    init {
        mHandler = LoginExpiredHandler()
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code() == 403) {
            val mediaType = response.body()!!.contentType()
            val content = response.body()!!.string()
            if (content.contains("code") && content.contains("403003")) {
                val mLoginExpiredCheckData =
                    GsonUtils.fromJson(content, LoginExpiredCheckData::class.java)
                if (mLoginExpiredCheckData.code == 403003) {//登录失效
                    val mMessage = mHandler.obtainMessage()
                    mMessage.what = 403003
                    mMessage.obj = mLoginExpiredCheckData
                    mHandler.sendMessage(mMessage)

                }
                return response.newBuilder()
                    .body(ResponseBody.create(mediaType, content))
                    .build()

            } else {
                return response
            }

        } else {
            return response
        }
    }

    private class LoginExpiredHandler : Handler(Looper.getMainLooper()) {
        private var mDialog: Dialog? = null
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                403003 -> {//登录失效
                    val mActivity = ActivityCacheManager.instance().getLastActivity()
                    if (mActivity == null || mActivity.isFinishing) {
                        return
                    }
                    if (mDialog != null && mDialog!!.isShowing) {
                        return
                    }
                    mDialog = DialogHelp.Builder.setTitle("登录提示")
                        .setContentMsg(
                            "您的帐户是在未知设备登录，请注意并保护资金安全。" +
                                    "如果是您自己操作的，请忽略它"
                        )
                        .setLeftMsg("退出APP").setRightMsg("从新登录")
                        .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                            override fun onLeftClickLinsener(msg: String) {
                                UserInfoManager.instance().clearUserInfo()
                                AppExitManager.instance().exitApp()
                            }

                            override fun onRightClickLinsener(msg: String) {
                                UserInfoManager.instance().clearUserInfo()
                                ARouter.getInstance().build(ARouterMap.LOGIN)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                                mActivity.finish()
                            }

                        }).build(mActivity).showDailog()

                }
            }


        }
    }
}