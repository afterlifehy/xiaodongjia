package com.wbb.xiaodongjia.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.wbb.base.mvvm.base.BaseViewModel
import com.chouyou.waterbridge.mvvm.base.ErrorMessage
import com.wbb.base.bean.*
import com.wbb.xiaodongjia.mvvm.repository.HomeRepositiry
import com.wbb.xiaodongjia.mvvm.repository.MerchantRepository
import com.wbb.xiaodongjia.utils.LocalAddressManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by zj on 2021/2/24.
 */
class HomeViewModel : BaseViewModel() {
    val mHomeRepositiry by lazy { HomeRepositiry() }
    val mMerchantRepository by lazy { MerchantRepository() }
    val mCategoryData = MutableLiveData<MutableList<HomeMenuInfo>>()
    val mMaecht = MutableLiveData<PaginationInfo<MerchantInfo>>()
    val mProfitData = MutableLiveData<ProfitInfo>()
    val mSourceMaterialData = MutableLiveData<List<CommenTools>>()
    val mNewMsgData = MutableLiveData<PaginationInfo<MsgData>>()
    val mMerchantStoreDetailLiveData = MutableLiveData<MerchantDetailBean>()

    companion object {
        const val NET_WORK_MAT_TA = "net_work_tag"
    }

    /**
     * 商户分类
     */
    fun getCategory() {
        var par = HashMap<String, String>()
        par["cityId"] = LocalAddressManager.instance().getCityId()
        newLaunch({
            val response = withContext(Dispatchers.IO) {
                mHomeRepositiry.getCategory(par)
            }
            executeResponse(response, {
                mCategoryData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1001))
            })
        }, NET_WORK_MAT_TA)
    }

    /**
     * 获取首页wbb
     */
    fun getProfit() {
        newLaunch({
            val response = withContext(Dispatchers.IO) {
                mHomeRepositiry.getProfit()
            }
            executeResponse(response, {
                mProfitData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1001))
            })
        })
    }

    /**
     * 获取推荐
     */
    fun getSourceMaterial(par: Map<String, String>) {
        newLaunch({
            val response = withContext(Dispatchers.IO) {
                mHomeRepositiry.getSourceMaterial(par)
            }
            executeResponse(response, {
                mSourceMaterialData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1001))
            })
        }, "")
    }

    /**
     * 获取新消息内容
     */
    fun getMineMsgPage(par: Map<String, String>) {

        newLaunch({
            val response = withContext(Dispatchers.IO) {
                mHomeRepositiry.getMineMsgPage(par)
            }
            executeResponse(response, {
                mNewMsgData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg))
            })
        }, "")
    }

    /**
     * 获取商户列表
     */
    fun getMerchantSearchList(par: Map<String, String>) {

        newLaunch({
            val response = withContext(Dispatchers.IO) {
                mMerchantRepository.getMerchantSearchList(par)
            }
            executeResponse(response, {
                mMaecht.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1002))
            })
        })
    }

    /**
     * 获取商户列表
     */
    fun merchantStoreDetail(par: Map<String, String>) {

        newLaunch({
            val response = withContext(Dispatchers.IO) {
                mHomeRepositiry.merchantStoreDetail(par)
            }
            executeResponse(response, {
                mMerchantStoreDetailLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1002))
            })
        })
    }

}