package com.wbb.xiaodongjia.utils

import android.text.TextUtils
import com.amap.api.location.AMapLocation
import com.google.gson.Gson
import com.wbb.base.BaseApplication
import com.wbb.base.bean.OnAppExitLinsener
import com.wbb.base.pf.PreferencesKeys
import com.wbb.base.pf.StoreFactory
import com.wbb.base.util.GsonUtils
import com.wbb.xiaodongjia.bean.LocalAddressInfo
import kotlinx.coroutines.*

/**
 * Created by zj on 2021/2/26.
 * 保存本地
 */
class LocalAddressManager private constructor() : OnAppExitLinsener {
    private var mLocalAddressInfo: LocalAddressInfo? = null
    private var mGson: Gson? = null

    //定位到的地址
    private var mAMapLocation: AMapLocation? = null

    //后端返回的城市定位
    private var mDefaultAddressInfo: LocalAddressInfo? = null

    init {
        this.mGson = Gson()
    }

    companion object {
        private var mLocalAddressManager: LocalAddressManager? = null
        fun instance(): LocalAddressManager {
            if (mLocalAddressManager == null) {
                mLocalAddressManager = LocalAddressManager()

            }
            return mLocalAddressManager!!
        }
    }

    /**
     * 保存当前定位地址
     */
    fun saveCurrentAMapLocation(mAMapLocation: AMapLocation?) {
        this.mAMapLocation = mAMapLocation
    }

    /**
     * 网络请求需要传获取这个的城市信息
     */
    fun getNetworkRequestDataCityInfo(): LocalAddressInfo {
        if (getUserSelecetCityInfo() != null) {//用户选择的优先获取
            return getUserSelecetCityInfo()!!
        } else if (mAMapLocation != null) {//在获取当前定位信息

            val empCode = userLocalAddress(mAMapLocation!!.adCode)
            return LocalAddressInfo(mAMapLocation!!.city, empCode)
        } else {//最后在获取后端返回的默认城市
            return getDefaultCityInfo()!!
        }

    }

    /**
     * 自己处理城市ID
     *   //高德给的城市ID，跟后端的不匹配，根据规则。取前4位，默认补00
     */
    fun userLocalAddress(cityId: String): String {
        if (TextUtils.isEmpty(cityId)) {
            return ""
        }
        return cityId.substring(0, 4) + "00"
    }

    /**
     * 获取当前定位地址
     */
    fun getCurrentAMapLocation(): AMapLocation? {
        return this.mAMapLocation
    }

    /**
     * 获取城市id
     */
    fun getCityId(): String {
        val saveInfo = getNetworkRequestDataCityInfo()
        if (saveInfo != null) {
            return saveInfo.cityId
        }
        return ""
    }

    /**
     * 获取城市
     */
    fun getCity(): String {
        val saveInfo = getNetworkRequestDataCityInfo()
        if (saveInfo != null) {
            return saveInfo.cityName
        }
        return "上海市"
    }

    /**
     * 保存用户选择城市信息
     */
    fun onSaveUserSelectCityInfo(info: LocalAddressInfo?) {
        info?.let {
            this.mLocalAddressInfo = it
            GlobalScope.launch(Dispatchers.Main) {
                StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                    .putString(PreferencesKeys.LOACD_INFO, mGson!!.toJson(it))
            }
        }
    }

    /**
     * 获取用户选择城市
     */
    fun getUserSelecetCityInfo(): LocalAddressInfo? {
        if (mLocalAddressInfo != null) {
            return mLocalAddressInfo
        } else {
            var mEmpLocalAddressInfo: LocalAddressInfo? = null
            runBlocking {
                val infoJson =
                    StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                        .getString(PreferencesKeys.LOACD_INFO)
                if (!TextUtils.isEmpty(infoJson)) {
                    mEmpLocalAddressInfo = GsonUtils.fromJson(
                        infoJson,
                        LocalAddressInfo::class.java
                    )
                }
            }
            this.mLocalAddressInfo = mEmpLocalAddressInfo
            return mEmpLocalAddressInfo
        }

    }

    /**
     * 保存从后端返回的默认城市信息
     */
    fun onSaveDefaultCityInfo(mDefaultAddressInfo: LocalAddressInfo?) {
        mDefaultAddressInfo?.let {
            this.mDefaultAddressInfo = it
            GlobalScope.launch(Dispatchers.Main) {
                StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                    .putString(PreferencesKeys.DEFAULT_ADDRESS_INFO, mGson!!.toJson(it))
            }
        }
    }

    /**
     * 获取默认的城市信息
     */
    fun getDefaultCityInfo(): LocalAddressInfo? {
        if (mDefaultAddressInfo != null) {
            return mDefaultAddressInfo
        } else {
            var mEmpLocalAddressInfo: LocalAddressInfo? = null
            runBlocking {
                val infoJson =
                    StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                        .getString(PreferencesKeys.DEFAULT_ADDRESS_INFO)
                mEmpLocalAddressInfo = GsonUtils.fromJson(infoJson, LocalAddressInfo::class.java)
            }
            this.mDefaultAddressInfo = mEmpLocalAddressInfo
            return mEmpLocalAddressInfo
        }
    }

    /**
     * 清楚选择城市 和默认城市信息
     */
    override fun clearSaveInfo() {
        mLocalAddressInfo = null
        GlobalScope.launch(Dispatchers.Main) {
            StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                .putString(PreferencesKeys.LOACD_INFO, "")
            StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                .putString(PreferencesKeys.DEFAULT_ADDRESS_INFO, "")
        }
    }
}