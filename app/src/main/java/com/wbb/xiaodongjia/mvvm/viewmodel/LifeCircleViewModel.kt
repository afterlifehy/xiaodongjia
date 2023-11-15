package com.wbb.xiaodongjia.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.wbb.base.mvvm.base.BaseViewModel
import com.chouyou.waterbridge.mvvm.base.ErrorMessage
import com.wbb.base.bean.PaginationInfo
import com.wbb.base.bean.LifeCircleActivityBean
import com.wbb.xiaodongjia.mvvm.repository.LifeCircleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by hy on 2021/2/24.
 */
class LifeCircleViewModel : BaseViewModel() {
    var mActivityListLiveData = MutableLiveData<List<LifeCircleActivityBean>>()

    companion object {
        const val NET_WORK_MAT_TA = "net_work_tag"
    }

    val mLifeCircleRepository by lazy {
        LifeCircleRepository()
    }

    fun activityList(par: Map<String, Any>) {
        newLaunch({
            val response = withContext(Dispatchers.IO) {
                mLifeCircleRepository.activityList(par)
            }
            executeResponse(response, {
                mActivityListLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1000))
            })
        }, NET_WORK_MAT_TA)
    }
}