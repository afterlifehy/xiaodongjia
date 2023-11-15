package com.wbb.xiaodongjia.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.wbb.base.mvvm.base.BaseViewModel
import com.chouyou.waterbridge.mvvm.base.ErrorMessage
import com.wbb.base.bean.CityItemInfo
import com.wbb.xiaodongjia.mvvm.repository.HomeRepositiry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by zj on 2021/2/24.
 */
class AddressSelectViewModel : BaseViewModel() {
    val mHomeRepositiry by lazy { HomeRepositiry() }
    val mOpenCity = MutableLiveData<MutableList<CityItemInfo>>()
    val mAllCity = MutableLiveData<MutableList<CityItemInfo>>()


    /**
     * 获取已开通城市列表
     */
    fun getOpenCity() {
        launch {
            val response = withContext(Dispatchers.IO) {
                mHomeRepositiry.getOpenCity()
            }
            executeResponse(response, {
                mOpenCity.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1001))
            })
        }
    }

    /**
     * 获取所有城市
     */
    fun getAllSort() {
        launch {
            val response = withContext(Dispatchers.IO) {
                mHomeRepositiry.getAllSort()
            }
            executeResponse(response, {
                mAllCity.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1001))
            })
        }
    }
}