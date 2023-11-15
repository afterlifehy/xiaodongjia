package com.wbb.xiaodongjia.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.chouyou.waterbridge.mvvm.base.ErrorMessage
import com.wbb.base.bean.UpdateBean
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.xiaodongjia.mvvm.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by zj on 2021/2/26.
 */
class MainViewModel : BaseViewModel() {
    val mUserRepository by lazy { UserRepository() }
    val isBing = MutableLiveData<Boolean>()
    val mAppVersionLiveData = MutableLiveData<UpdateBean>()

    /**
     * 获取当前推荐关系是否绑定
     */
    fun getLevelCount() {
        newLaunch({
            val response = withContext(Dispatchers.IO) {
                mUserRepository.getLevelCount()
            }
            executeResponse(response, {
                isBing.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1001))
            })
        })
    }

    /**
     * app更新
     */
    fun appVersion() {
        launch {
            val response = withContext(Dispatchers.IO) {
                mUserRepository.appVersion()
            }
            executeResponse(response, {
                mAppVersionLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1005))
            })
        }
    }
}