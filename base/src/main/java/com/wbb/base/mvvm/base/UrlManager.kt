package com.wbb.base.mvvm.base

import com.wbb.base.pf.PreferencesHelp
import com.wbb.base.util.AppFlag


/**
 * Created by zj on 2021/2/23.
 */
object UrlManager {
    //    const val HOST = "http://dev.wbbapi.tss885.com:1080/"
//    const val BASE_URL = HOST + "wallet-api/"
    const val DEVELOP_HOST = "http://dev.app.jinilife.com:8888"
    const val TEST_HOST = "http://test.app.jinilife.com:8888"
    const val PRE_HOST = "http://pre.api.jinilife.com:8888"
    const val FORMLA_HOST = "https://api.jinilife.com"

    var PORTAL_URL = AppFlag.serverPathMap[PreferencesHelp.getDevValue()] + "/sgw/portal/"
    var MERCHANT_PORTAL_URL =
        AppFlag.serverPathMap[PreferencesHelp.getDevValue()] + "/sgw/merchant/portal/"
    var BASIS_URL = AppFlag.serverPathMap[PreferencesHelp.getDevValue()] + "/sgw/"

    fun getPortalUrl(): String {
        return AppFlag.serverPathMap[PreferencesHelp.getDevValue()] + "/sgw/portal/"
    }

    fun getMerchantPortalUrl(): String {
        return AppFlag.serverPathMap[PreferencesHelp.getDevValue()] + "/sgw/merchant/portal/"
    }

    fun getBasisUrl(): String {
        return AppFlag.serverPathMap[PreferencesHelp.getDevValue()] + "/sgw/"
    }

    fun getBasicUrl(): String {
        return AppFlag.serverPathMap[PreferencesHelp.getDevValue()] + "/sgw/basic/"
    }
}