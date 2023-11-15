package com.wbb.xiaodongjia.base

import com.wbb.base.proxy.OnAppBaseProxyLinsener
import com.wbb.xiaodongjia.BuildConfig

/**
 * Created by zj on 2021/4/1.
 */
class OnAppBaseProxyManager : OnAppBaseProxyLinsener {
    override fun onIsProxy(): Boolean {
        return BuildConfig.is_proxy
    }

    override fun onIsDebug(): Boolean {
        return BuildConfig.is_debug
    }

}