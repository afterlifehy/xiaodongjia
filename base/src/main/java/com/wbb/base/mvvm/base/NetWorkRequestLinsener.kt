package com.wbb.base.mvvm.base

import com.wbb.base.bean.NetWorkRequestData

/**
 * Created by zj on 2021/3/2.
 */
interface NetWorkRequestLinsener {
    /**
     * 网络超时之类的错误
     */
    fun onNetWorkRequestError(errror: NetWorkRequestData)

    /**
     * 无网络错误
     */
    fun onNotNetWorkErrror(errror: NetWorkRequestData)
}