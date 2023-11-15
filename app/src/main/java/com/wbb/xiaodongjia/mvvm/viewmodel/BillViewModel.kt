package com.wbb.xiaodongjia.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.wbb.base.mvvm.base.BaseViewModel
import com.chouyou.waterbridge.mvvm.base.ErrorMessage
import com.wbb.base.bean.*
import com.wbb.xiaodongjia.mvvm.repository.BillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by hy on 2021/2/24.
 */
class BillViewModel : BaseViewModel() {
    val mBillListLiveData = MutableLiveData<PaginationInfo<BillListBean>>()
    val mBillDetailLiveData = MutableLiveData<BillDetailBean>()
    val mPayBalanceLiveData = MutableLiveData<GetPayBalanceBean>()
    val mPayResultLiveData = MutableLiveData<PayResultBean>()
    val mAddOrderLiveData = MutableLiveData<BillListBean>()
    val mPayStatusLiveData = MutableLiveData<PayStatusBean>()

    val billRepository by lazy {
        BillRepository()
    }

    fun getOrder(par: Map<String, Any>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                billRepository.getOrder(par)
            }
            executeResponse(response, {
                mBillListLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1000))
            })
        }
    }

    fun getOrderDetail(par: Map<String, String>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                billRepository.getOrderDetail(par)
            }
            executeResponse(response, {
                mBillDetailLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1001))
            })
        }
    }


    /**
     * 获取买单余额
     */
    fun getPay(par: Map<String, String>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                billRepository.getPay(par)
            }
            executeResponse(response, {
                mPayBalanceLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1002))
            })
        }
    }

    /**
     * 获取买单余额
     */
    fun addOrderAndPay(par: Map<String, Any>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                billRepository.addOrderAndPay(par)
            }
            executeResponse(response, {
                mPayResultLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1003))
            })
        }
    }

    /**
     * 微信买单
     */
    fun addOrder(par: Map<String, Any>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                billRepository.addOrder(par)
            }
            executeResponse(response, {
                mAddOrderLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1004))
            })
        }
    }

    /**
     * 查询支付结果
     */
    fun getPayStatus(par: Map<String, String>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                billRepository.getPayStatus(par)
            }
            executeResponse(response, {
                mPayStatusLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1004))
            })
        }
    }
}