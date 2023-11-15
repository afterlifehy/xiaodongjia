package com.wbb.xiaodongjia.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.chouyou.waterbridge.mvvm.base.ErrorMessage
import com.wbb.base.bean.PayResultBean
import com.wbb.base.bean.RechargeResultBean
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.xiaodongjia.mvvm.repository.WebviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by hy on 2021/3/10.
 */
class WebviewViewModel : BaseViewModel() {
    val mInitRechargeLiveData = MutableLiveData<RechargeResultBean>()

    val mWebviewRepository by lazy {
        WebviewRepository()
    }

    /**
     * 充值预创建订单
     */
    fun initRecharge(par: Map<String, String>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mWebviewRepository.initRecharge(par)
            }
            executeResponse(response, {
                mInitRechargeLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1001))
            })
        }
    }
}