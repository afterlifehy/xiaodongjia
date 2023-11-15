package com.wbb.xiaodongjia.mvvm.repository

import com.wbb.base.mvvm.base.BaseRepository
import com.wbb.base.bean.HttpWrapper
import com.wbb.base.bean.UserInfo

/**
 * Created by zj on 2021/2/23.
 */
class LoginRepository : BaseRepository() {
    /**
     * 短信验证码登录
     */
    suspend fun phoneLogin(login: Map<String, String>): HttpWrapper<String> {
        return mPortalServe.mobileLogin(login)
    }

    /**
     * 获取极验code
     */
    suspend fun getJyCode(par: Map<String, String>): HttpWrapper<String> {
        return mPortalServe.getJyCode(par)
    }

    /**
     * 发送登录短信
     */
    suspend fun sendMsgGeetest(par: Map<String, String>): HttpWrapper<String> {
        return mPortalServe.sendMsgGeetest(par)
    }

    /**
     * 短信登录
     */
    suspend fun loginSms(par: Map<String, String>): HttpWrapper<UserInfo> {
        return mPortalServe.loginSms(par)
    }

    /**
     * 极验一键登录
     */
    suspend fun loginTokenVerify(par: Map<String, String>): HttpWrapper<UserInfo> {
        return mPortalServe.loginTokenVerify(par)
    }
}