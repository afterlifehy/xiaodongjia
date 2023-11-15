package com.wbb.xiaodongjia.base

import android.app.Application
import android.os.Process
import android.text.TextUtils
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.liulishuo.filedownloader.FileDownloader
import com.sensorsdata.analytics.android.sdk.SAConfigOptions
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.wbb.base.BaseApplication
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.buriedpoint.ShenCeManagerKt
import com.wbb.base.crash.CrashHandler
import com.wbb.base.help.SmartRefreshHelp
import com.wbb.base.network.NetWorkMonitorManager
import com.wbb.base.start.AppInitManager
import com.wbb.base.util.AppUtil
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


/**
 * Created by zj on 2021/2/27.
 */
class AppStartUpManager private constructor() : AppInitManager() {
    companion object {
        var mAppStartUpManager: AppStartUpManager? = null
        fun instance(): AppStartUpManager {
            if (mAppStartUpManager == null) {
                mAppStartUpManager = AppStartUpManager()
            }
            return mAppStartUpManager!!
        }

    }

    override fun applicationInit(application: Application) {
        CrashHandler.instance()?.initCrash(application)
        BaseApplication.instance().setOnAppBaseProxyLinsener(OnAppBaseProxyManager())
        //初始化全局的刷新
        SmartRefreshHelp.initRefHead()
        //初始化网络状态监听
        regNewWorkState(application)
        /**
         * 初始化神策
         */
        initSc(application)
        initRoom()
        initBugly(application)
    }

    /**
     * 初始化腾讯Bugly
     */
    private fun initBugly(application: Application) {
        // 获取当前进程名
        val processName =
            AppUtil.getProcessName(Process.myPid())
        // 设置是否为上报进程
        val strategy = UserStrategy(application)
        strategy.isUploadProcess = processName == null || processName == application.packageName
        CrashReport.initCrashReport(
            application,
            "106e653bfa",
            true,
            strategy
        )
    }


    override fun delayInit(application: Application) {
        FileDownloader.setup(BaseApplication.instance())
    }

    fun initRoom() {
    }

    val MIGRATION_0_1: Migration = object : Migration(0, 1) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("create table audio_play_schedule_info(id TEXT NOT NULL primary key,schedule INTEGER NOT NULL,name TEXT NOT NULL,commodityId INTEGER NOT NULL,courseId INTEGER NOT NULL,userId TEXT NOT NULL)")
        }
    }

    /**
     * 注册全局的网络状态广播
     */
    private fun regNewWorkState(application: Application) {
        NetWorkMonitorManager.getInstance().init(application)
    }


    /**
     * 初始化神策
     */
    private fun initSc(application: Application) {
        val saConfigOptions = SAConfigOptions(ShenCeManagerKt.getSC_URL())
        //在初始化时，利用 SAConfigOptions 对象开启全埋点
        saConfigOptions.setAutoTrackEventType(
            SensorsAnalyticsAutoTrackEventType.APP_CLICK or
                    SensorsAnalyticsAutoTrackEventType.APP_START or
                    SensorsAnalyticsAutoTrackEventType.APP_END or
                    SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN
        ).enableLog(false)
        saConfigOptions.enableTrackAppCrash() //开启错误日志上传监听
        SensorsDataAPI.sharedInstance(application, saConfigOptions)
        //初始化 SDK 之后，开启自动采集 Fragment 页面浏览事件
        SensorsDataAPI.sharedInstance().trackFragmentAppViewScreen()
        //神策的依赖只能加在主APP里面，通过代理让model使用
        OnBuriedPointManager.get().initProxy(ShenCeManagerKt.get())
        ShenCeManagerKt.get().registerSuperProperties()
    }
}