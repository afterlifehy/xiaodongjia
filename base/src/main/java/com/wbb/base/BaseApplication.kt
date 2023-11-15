package com.wbb.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.wbb.base.help.ActivityCacheManager
import com.wbb.base.http.OnAddOkhttpInterceptor
import com.wbb.base.proxy.OnAppBaseProxyLinsener
import kotlin.properties.Delegates


/**
 * Created by hy on 2021/1/18.
 */
abstract class BaseApplication : Application(), Application.ActivityLifecycleCallbacks,
    OnAddOkhttpInterceptor {
    private var mOnAppBaseProxyLinsener: OnAppBaseProxyLinsener? = null

    companion object {
        var baseApplication: BaseApplication by Delegates.notNull()

        @JvmStatic
        fun instance() = baseApplication

    }

    override fun onCreate() {
        super.onCreate()
        baseApplication = this
        registerActivityLifecycleCallbacks(this)
        //分享SDK
        // MobSDK.submitPolicyGrantResult(true, null);
//   StartUpManager.applicationStartUp(this)

    }

    /**
     * 用来获取子类和父类直接的交互
     */
    fun setOnAppBaseProxyLinsener(mOnAppBaseProxyLinsener: OnAppBaseProxyLinsener?) {
        this.mOnAppBaseProxyLinsener = mOnAppBaseProxyLinsener
    }

    fun getOnAppBaseProxyLinsener(): OnAppBaseProxyLinsener? {
        return mOnAppBaseProxyLinsener
    }

    private fun initUM() {
        //   UMConfigure.setLogEnabled(true)
        var plat = ""
        if (BuildConfig.RELEASE_PLATFORM === 1) {
            plat = "google"
        } else if (BuildConfig.RELEASE_PLATFORM === 2) {
            plat = "local"
        } else if (BuildConfig.RELEASE_PLATFORM === 3) {
            plat = "inside"
        }
//        UMConfigure.init(
//            this,
//            "5f3cd24ad3093221547ad431",
//            plat,
//            UMConfigure.DEVICE_TYPE_PHONE,
//            null
//        )
//        // 选用AUTO页面采集模式
//        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL)
//        // 支持在子进程中统计自定义事件
//        UMConfigure.setProcessEvent(true)
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        ActivityCacheManager.instance().remActivity(activity)
        activity.finish()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ActivityCacheManager.instance().addActivity(activity)
        Log.i("ActivityManagers:", activity.javaClass.name)

    }

    override fun onActivityResumed(activity: Activity) {
    }

}