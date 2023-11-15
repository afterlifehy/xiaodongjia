package com.wbb.base.map

import android.Manifest
import android.content.Context
import android.util.Log
import com.alibaba.fastjson.JSON
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationListener
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.github.dfqin.grantor.PermissionListener
import com.github.dfqin.grantor.PermissionsUtil
import com.wbb.base.util.RxTimerUtil
import com.wbb.base.util.RxTimerUtil.IRxNext

/**
 * Created by zj on 2019/10/14.
 */
class DxlNewLocationManager(private val context: Context) : AMapLocationListener {
    private var mOnCallback: OnCallback? = null

    //声明AMapLocationClient类对象
    private var mLocationClient: AMapLocationClient? = null
    private var mRxTimerUtil: RxTimerUtil? = null
    private fun initData() {
        //初始化定位
        mLocationClient = AMapLocationClient(context.applicationContext)
        //设置定位回调监听
        mLocationClient!!.setLocationListener(this)
        mRxTimerUtil = RxTimerUtil()
    }

    override fun onLocationChanged(aMapLocation: AMapLocation) {
        Log.i("location:", "aMapLocation:" + JSON.toJSONString(aMapLocation))
        if (aMapLocation != null) {

            if (aMapLocation.errorCode == 0) {
                onLoactionSucess(aMapLocation)
                //解析定位结果
            } else {
                onLocalationTimeOut()
            }
        }
    }

    /**
     * 去定位
     */
    fun startPositioning(context: Context, mOnCallback: OnCallback?) {
        PermissionsUtil.requestPermission(
            context, object : PermissionListener {
                override fun permissionGranted(permission: Array<out String>) {
                    startLocation(mOnCallback)
                }

                override fun permissionDenied(permission: Array<out String>) {
                    OnBuriedPointManager.get().getOnBuriedPointManager()?.OnGetUserLocation("0", "")
                    mOnCallback?.onNotGetAuthority()
                    mRxTimerUtil?.cancel()
                }
            }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    /**
     * 启动定位
     */
    private fun startLocation(mOnCallback: OnCallback?) {
        this.mOnCallback = mOnCallback
        mLocationClient!!.startLocation()
        mRxTimerUtil!!.timer(DEFAULT_LBS_TIME_OUT.toLong(), object : IRxNext {
            override fun doNext(number: Long) {
                onLocalationTimeOut()
            }
        })
    }


    /**
     * 定位成功
     *
     * @param cityName
     */
    private fun onLoactionSucess(aMapLocation: AMapLocation) {
        mLocationClient!!.stopLocation()
        if (mOnCallback != null) {
            mOnCallback!!.onLocation(aMapLocation)
            mOnCallback = null
        }
        mRxTimerUtil?.cancel()
    }

    /**
     * 定位失败
     */
    private fun onLocalationTimeOut() {
        OnBuriedPointManager.get().getOnBuriedPointManager()?.OnGetUserLocation("1", "1")
        if (mOnCallback != null) {
            mOnCallback!!.onTimeout()
        }
        stopLocation()
        mRxTimerUtil?.cancel()
    }

    /**
     * 停止定位
     */
    fun stopLocation() {
        mLocationClient!!.stopLocation()
        mRxTimerUtil?.cancel()
    }

    interface OnCallback {
        fun onLocation(aMapLocation: AMapLocation?)
        fun onTimeout()
        fun onNotGetAuthority()
    }

    companion object {
        private const val DEFAULT_LBS_TIME_OUT = 10 * 1000 // 5秒定位超时
    }

    init {
        initData()
    }
}