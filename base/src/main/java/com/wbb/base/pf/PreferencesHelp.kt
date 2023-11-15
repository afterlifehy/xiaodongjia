package com.wbb.base.pf

import com.wbb.base.BaseApplication
import com.wbb.base.BuildConfig
import kotlinx.coroutines.runBlocking

/**
 * Created by zj on 2021/3/25.
 */
object PreferencesHelp {
    /**
     * 获取当前使用哪个Url
     */
    fun getDevValue(): Int {
        var isDev = 4
        runBlocking {
            val isDevIndex =
                StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                    .getInt(PreferencesKeys.ISDEV)
            if (isDevIndex == 0) {
                isDev = BuildConfig.ISDEV
            } else {
                isDev = isDevIndex
            }
        }
        return isDev
    }

    /**
     * 保存当前使用的URl
     */
    fun putDevValue(devInde: Int) {
        runBlocking {
            StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                .putInt(PreferencesKeys.ISDEV, devInde)
        }

    }

    /**
     * 获取h5需要的参数
     */
    fun getINVITE_CODE(): String {
        var recommend = ""
        runBlocking {
            recommend = StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                .getString(PreferencesKeys.INVITE_CODE)
        }
        return recommend
    }
}