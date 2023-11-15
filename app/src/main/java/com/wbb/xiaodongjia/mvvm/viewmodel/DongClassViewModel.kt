package com.wbb.xiaodongjia.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.chouyou.waterbridge.mvvm.base.ErrorMessage
import com.wbb.base.bean.DongClassBean
import com.wbb.base.bean.PaginationInfo
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.xiaodongjia.mvvm.repository.DongClassReposity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by hy on 2021/3/8.
 */
class DongClassViewModel : BaseViewModel() {
    var mDongClassLiveData = MutableLiveData<PaginationInfo<DongClassBean>>()

    val mDongClassReposity by lazy {
        DongClassReposity()
    }

    fun courseTypeSearch(par: Map<String, Any>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mDongClassReposity.courseTypeSearch(par)
            }
            executeResponse(response, {
                mDongClassLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1000))
            })
        }
    }

}