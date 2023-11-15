package com.wbb.xiaodongjia.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.wbb.base.mvvm.base.BaseViewModel
import com.chouyou.waterbridge.mvvm.base.ErrorMessage
import com.wbb.base.bean.*
import com.wbb.xiaodongjia.mvvm.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by hy on 2021/2/23.
 */
class UserViewModel : BaseViewModel() {
    val mRecommendListLiveData = MutableLiveData<List<RecommendListBean>>()
    val mMineLiveData = MutableLiveData<MineBean>()
    val mCardPackageLiveData = MutableLiveData<List<CardPackageBean>>()
    val mConfigData = MutableLiveData<CityChildItemInfo>()
    val mUserRepository by lazy {
        UserRepository()
    }

    fun getRecommendList(par: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mUserRepository.getRecommendList(par)
            }
            executeResponse(response, {
                mRecommendListLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1000))
            })
        }
    }

    fun userInfoView() {
        launch {
            val response = withContext(Dispatchers.IO) {
                mUserRepository.userInfoView()
            }
            executeResponse(response, {
                mMineLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1001))
            })
        }
    }

    /**
     * 保存用户昵称和推荐人
     */
    fun saveNameRecommend(nickName: String, recommend: String): MutableLiveData<String> {
        val mSaveUserName = MutableLiveData<String>()
        val par = HashMap<String, String>()
        par["nickName"] = nickName
        par["recommend"] = recommend
        newLaunch({
            val response = withContext(Dispatchers.IO) {
                mUserRepository.saveNameRecommend(par)
            }
            executeResponse(response, {
                mSaveUserName.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1002))
            })
        }, tag = "11111111")


        return mSaveUserName
    }

    /**
     * 卡包列表
     */
    fun getMemberCard() {
        launch {
            val response = withContext(Dispatchers.IO) {
                mUserRepository.getMemberCard()
            }
            executeResponse(response, {
                mCardPackageLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1003))
            })
        }
    }

    /**
     * 获取基础配置
     */
    fun getConfigSet() {
        val par = HashMap<String, String>()
        launch {
            val response = withContext(Dispatchers.IO) {
                mUserRepository.getConfigSet(par)
            }
            executeResponse(response, {
                mConfigData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1004))
            })
        }
    }

}