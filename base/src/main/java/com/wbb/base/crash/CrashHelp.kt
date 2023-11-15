package com.wbb.base.crash

import com.wbb.base.BaseApplication
import com.wbb.base.pf.PreferencesKeys
import com.wbb.base.pf.StoreFactory
import kotlinx.coroutines.runBlocking

/**
 * Created by zj on 2021/3/30.
 */
object CrashHelp {
    /**
     * 保存当前状态
     */
    fun saveCrashStatus(isCrash: Boolean) {
        runBlocking {
            val providePreferencesDataStore =
                StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
            providePreferencesDataStore
                .putBoolean(PreferencesKeys.IS_CRASH, isCrash)

            providePreferencesDataStore.putLong(
                PreferencesKeys.CRASH_TIME,
                System.currentTimeMillis()
            )
        }
    }

    /**
     * 当前是否崩溃过
     */
    fun isCrash(): Boolean {
        var isCrash = false
        runBlocking {
            val providePreferencesDataStore =
                StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
            val tmpCrash = providePreferencesDataStore
                .getBoolean(PreferencesKeys.IS_CRASH)
            val crashTime = providePreferencesDataStore.getLong(PreferencesKeys.CRASH_TIME)
            if (tmpCrash && (System.currentTimeMillis() - crashTime < 5000)) {
                isCrash = true
                saveCrashStatus(false)
            }
        }
        return isCrash
    }
}