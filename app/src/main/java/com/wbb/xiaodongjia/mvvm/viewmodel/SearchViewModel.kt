package com.wbb.xiaodongjia.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSONObject
import com.chouyou.waterbridge.mvvm.base.ErrorMessage
import com.wbb.base.bean.PaginationInfo
import com.wbb.base.bean.SearchCountBean
import com.wbb.base.bean.SearchResultBean
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.xiaodongjia.mvvm.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by hy on 2021/2/27.
 */
class SearchViewModel : BaseViewModel() {
    val mCountLiveData = MutableLiveData<SearchCountBean>()
    val mSearchResultLiveData = MutableLiveData<PaginationInfo<SearchResultBean>>()
    val mhotSearchLiveData = MutableLiveData<String>()

    val mSearchRepository by lazy {
        SearchRepository()
    }

    /**
     * 搜索数目统计
     */
    fun count(par: Map<String, String>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mSearchRepository.count(par)
            }
            executeResponse(response, {
                mCountLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1001))
            })
        }
    }

    fun searchAll(par: Map<String, Any>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mSearchRepository.searchAll(par)
            }
            executeResponse(response, {
                mSearchResultLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1002))
            })
        }
    }

    fun getKeyList(par: Map<String, String>){
        launch {
            val response = withContext(Dispatchers.IO){
                mSearchRepository.getKeyList(par)
            }
            executeResponse(response,{
                mhotSearchLiveData.value = JSONObject.parseObject(response).getString("data")
            },{
                traverseErrorMsg(ErrorMessage(msg = JSONObject.parseObject(response).getString("msg"), code = 1003))
            })
        }
    }
}