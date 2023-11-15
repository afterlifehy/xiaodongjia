package com.wbb.xiaodongjia.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.wbb.base.mvvm.base.BaseViewModel
import com.chouyou.waterbridge.mvvm.base.ErrorMessage
import com.wbb.base.bean.MerchantFolownfo
import com.wbb.base.bean.PaginationInfo
import com.wbb.xiaodongjia.mvvm.repository.AttentionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by zj on 2021/2/24.
 */
class AttentionViewModel : BaseViewModel() {
    val mAttentionRepository by lazy { AttentionRepository() }

    val mFollowList = MutableLiveData<PaginationInfo<MerchantFolownfo>>()
    val mFollowSFList = MutableLiveData<String>()

    /**
     * 获取关注列表
     */
    fun getFollowList(par: Map<String, String>) {

        launch {
            val response = withContext(Dispatchers.IO) {
                mAttentionRepository.getFollowList(par)
            }
            executeResponse(response, {
                mFollowList.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1001))
            })
        }
    }

    /**
     * 关注搜索
     */
    fun getIsFollowList(par: Map<String, String>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mAttentionRepository.getIsFollowList(par)
            }
            executeResponse(response, {
                mFollowSFList.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1001))
            })
        }
    }

    /**
     * 添加收藏
     */
    fun addFollow(followId: String, type: String): MutableLiveData<String> {
        val mAddFollow = MutableLiveData<String>()
        val par = HashMap<String, String>()
        par["followId"] = followId
        par["mid"] = ""
        par["type"] = type
        launch {
            val response = withContext(Dispatchers.IO) {
                mAttentionRepository.addFollow(par)
            }
            executeResponse(response, {
                mAddFollow.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1001))
            })
        }
        return mAddFollow
    }
}