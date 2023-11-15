package com.wbb.base.crash

import android.app.Application
import android.util.Log
import com.wbb.base.help.AppExitManager
import com.wbb.base.pf.PreferencesKeys
import com.wbb.base.pf.StoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Created by zj on 2021/3/30.
 */
class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null
    private var mApplication: Application? = null

    companion object {
        private var mCrashHandler: CrashHandler? = null
        fun instance(): CrashHandler? {
            if (mCrashHandler == null) {
                mCrashHandler = CrashHandler()
            }
            return mCrashHandler
        }
    }

    fun initCrash(application: Application) {
        this.mApplication = application
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        //直接退出APP
        AppExitManager.instance().exitApp()
        //把闪退状态保存
        //CrashHelp.saveCrashStatus(true)
        mDefaultHandler?.uncaughtException(t, e)

    }

    /**
     * 错误处理,收集错误信息 发送错误报告等操作均在此完成.
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        // 自定义处理错误信息
        return true
    }

}