package com.wbb.xiaodongjia.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.wbb.base.mvvm.base.BaseViewModel
import com.chouyou.waterbridge.mvvm.base.ErrorMessage
import com.wbb.base.bean.UserInfo
import com.wbb.xiaodongjia.mvvm.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by zj on 2021/2/23.
 */
class LoginViewmodel : BaseViewModel() {
    val mLoginRepository by lazy {
        LoginRepository()
    }

    val mSmsLogin = MutableLiveData<UserInfo>()
    val mOnLogin = MutableLiveData<UserInfo>()
    /**
     * 获取极验code
     */
    fun getJyCode(phonearea: String, phone: String): MutableLiveData<String> {
        val par = HashMap<String, String>()
        par["phonearea"] = phonearea.replace("+", "")
        par["phone"] = phone
        val mJyCode = MutableLiveData<String>()
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.getJyCode(par)
            }
            executeResponse(response, {
                mJyCode.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1000))
            })
        }
        return mJyCode
    }

    /**
     * 发送登录短信
     */
    fun sendMsgGeetest(par: Map<String, String>): MutableLiveData<String> {
        val mSendCode = MutableLiveData<String>()
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.sendMsgGeetest(par)
            }
            executeResponse(response, {
                mSendCode.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1001))
            })
        }
        return mSendCode
    }

    /**
     * 短信登录
     */
    fun loginSms(par: Map<String, String>) {

        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.loginSms(par)
            }
            executeResponse(response, {
                mSmsLogin.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1002))
            })
        }
    }

    /**
     * 一键登录
     */
    fun loginTokenVerify(par: Map<String, String>) {

        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.loginTokenVerify(par)
            }
            executeResponse(response, {
                mOnLogin.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1003))
            })
        }
    }

}