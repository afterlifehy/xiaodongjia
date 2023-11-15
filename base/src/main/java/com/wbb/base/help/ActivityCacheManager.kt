package com.wbb.base.help

import android.app.Activity

/**
 * Created by zj on 2021/3/26.
 */
class ActivityCacheManager private constructor() {
    private val mActivityList = ArrayList<Activity>()

    companion object {
        var mActivityManager: ActivityCacheManager? = null
        fun instance(): ActivityCacheManager {
            if (mActivityManager == null) {
                mActivityManager = ActivityCacheManager()
            }
            return mActivityManager!!
        }
    }

    /**
     * 获取所有activity
     */
    fun getAllCacheActivitys(): List<Activity> {
        return mActivityList
    }

    /**
     * 添加activity
     */
    fun addActivity(activity: Activity) {
        mActivityList.add(activity)
    }

    /**
     * 移除activity
     */
    fun remActivity(activity: Activity) {
        mActivityList.remove(activity)
    }

    /**
     * 获取最后一个
     */
    fun getLastActivity(): Activity? {
        if (mActivityList.size > 0) {
            return mActivityList.get(mActivityList.size - 1)
        }
        return null
    }
}