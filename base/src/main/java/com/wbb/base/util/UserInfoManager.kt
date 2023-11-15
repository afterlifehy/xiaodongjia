package com.wbb.base.util

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.wbb.base.BaseApplication
import com.wbb.base.bean.UserInfo
import com.wbb.base.pf.PreferencesKeys
import com.wbb.base.pf.StoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Created by zj on 2021/2/24.
 * 用户信息保存类
 */
class UserInfoManager private constructor() {
    private var mUserInfo: UserInfo? = null
    private var mGson: Gson? = null
    val mStoreFactory by
    lazy { StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication) }

    init {
        this.mGson = Gson()
    }

    companion object {
        var mUserInfoManager: UserInfoManager? = null

        fun instance(): UserInfoManager {
            if (mUserInfoManager == null) {
                mUserInfoManager = UserInfoManager()

            }
            return mUserInfoManager!!
        }

    }

    /**
     * 保存用户登录后的信息
     */
    fun saveUserInfo(mUserInfo: UserInfo?) {
        mUserInfo?.let {
            this.mUserInfo = it
            runBlocking {
                mStoreFactory
                    .putString(PreferencesKeys.USER_INFP, mGson!!.toJson(it))
            }
        }
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(): UserInfo? {
        if (mUserInfo != null) {
            return mUserInfo
        } else {
            var mEmpUserInfo: UserInfo? = null
            runBlocking {
                val infoJson =
                    mStoreFactory
                        .getString(PreferencesKeys.USER_INFP)
                if (!TextUtils.isEmpty(infoJson)) {
                    mEmpUserInfo = GsonUtils.fromJson(infoJson, UserInfo::class.java)
                }
            }
            this.mUserInfo = mEmpUserInfo
            return mEmpUserInfo
        }

    }

    /**
     * 清除用户信息
     */
    fun clearUserInfo() {
        mUserInfo = null
        runBlocking {
            mStoreFactory
                .putString(PreferencesKeys.USER_INFP, "")
        }

    }

    /**
     * 是否登录
     */
    fun isLogin(): Boolean {
        if (getUserInfo() != null) {
            return true
        }
        return false
    }
}