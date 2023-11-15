package com.wbb.base.help

import android.view.KeyEvent
import com.wbb.base.util.ToastUtils


/**
 * Created by zj on 2021/3/26.
 */
class AppExitManager private constructor() {
    //用户点退出的时间
    private var clickExitTime = 0L

    companion object {
        var mAppExitManager: AppExitManager? = null
        fun instance(): AppExitManager {
            if (mAppExitManager == null) {
                mAppExitManager = AppExitManager()
            }
            return mAppExitManager!!
        }
    }

    /**
     * app 退出
     */
    fun exitApp() {
        ActivityCacheManager.instance().getAllCacheActivitys().forEach {
            if (!it.isFinishing) {
                it.finish()
            }

        }

    }

    /**
     * 清除当前activity之外其余 activity
     */
    fun finishAllTopActivities() {
        try {
            while (ActivityCacheManager.instance().getAllCacheActivitys().size != 1) {
                val mActiviyt = ActivityCacheManager.instance().getAllCacheActivitys().get(0)
                if (!mActiviyt.isFinishing) {
                    mActiviyt.finish()
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 检查当前是否可以退出
     */
    fun checkIsExit(keyCode: Int, event: KeyEvent?): Boolean {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (System.currentTimeMillis() - clickExitTime > 1000) {
                ToastUtils.showToash("再次点击返回退出APP")
            } else {
                exitApp()
            }
            clickExitTime = System.currentTimeMillis()
            return true
        }
        return false
    }
}