package com.wbb.base.pf

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.preferencesKey
import com.wbb.base.util.Constant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Created by zj on 2021/3/25.
 */
class PreferencesDataStore(val context: Application) : IDataStore {
    // 指定名字
    private val PREFERENCES_NAME = "prefs_datastore"
    val corruptionHandler = ReplaceFileCorruptionHandler<Preferences> {
        mutablePreferencesOf(
            // recover preferences here
        )
    }

    // 创建dataStore
    var dataStore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCES_NAME, corruptionHandler
    )

    override suspend fun putBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        dataStore.edit { it[key] = value }
    }

    override suspend fun getBoolean(key: Preferences.Key<Boolean>): Boolean {
        return dataStore.data.map { it[key] ?: false }.first()
    }

    override suspend fun putInt(key: Preferences.Key<Int>, value: Int) {
        dataStore.edit { it[key] = value }
    }

    override suspend fun getInt(key: Preferences.Key<Int>): Int {
        return dataStore.data.map { it[key] ?: 0 }.first()
    }

    override suspend fun putLong(key: Preferences.Key<Long>, value: Long) {
        dataStore.edit { it[key] = value }
    }

    override suspend fun getLong(key: Preferences.Key<Long>): Long {
        return dataStore.data.map { it[key] ?: 0L }.first()
    }

    override suspend fun putDouble(key: Preferences.Key<Double>, value: Double) {
        dataStore.edit { it[key] = value }
    }

    override suspend fun getDouble(key: Preferences.Key<Double>): Double {
        return dataStore.data.map { it[key] ?: 0.0 }.first()
    }

    override suspend fun putFloat(key: Preferences.Key<Float>, value: Float) {
        dataStore.edit { it[key] = value }
    }

    override suspend fun getFloat(key: Preferences.Key<Float>): Float {
        return dataStore.data.map { it[key] ?: 0F }.first()
    }

    override suspend fun putString(key: Preferences.Key<String>, value: String) {
        dataStore.edit { it[key] = value }
    }

    override suspend fun getString(key: Preferences.Key<String>): String {
        return dataStore.data.map { it[key] ?: "" }.first()
    }

    override fun spToDataStore() {
        /**
         *  传入 migrations 参数，构建一个 DataStore 之后
         *  需要执行 一次读取 或者 写入，DataStore 才会自动合并 SharedPreference 文件内容
         */
        dataStore = context.createDataStore(
            name = PREFERENCES_NAME,
            migrations = listOf(
                SharedPreferencesMigration(
                    context,
                    "222"
                )
            )
        )
    }

}

// 而外一个存储 DataStore的Key的类
object PreferencesKeys {

    //新消息
    val NEW_MSG_ID = preferencesKey<String>("new_msg_id")
    val SEARCH_HISTORY = preferencesKey<String>("searchHistory")
    val LOACD_INFO = preferencesKey<String>("address_info")
    val DEFAULT_ADDRESS_INFO = preferencesKey<String>("default_address_info")
    val UUID = preferencesKey<String>(Constant.UUID)
    val INVITE_CODE = preferencesKey<String>(Constant.INVITE_CODE)
    val USER_INFP = preferencesKey<String>("user_info")
    val ISDEV = preferencesKey<Int>(Constant.ISDEV)

    //是否阅读协议
    val IS_READ = preferencesKey<Boolean>("is_read_xt")

    //是否崩溃了
    val IS_CRASH = preferencesKey<Boolean>("is_crash")

    //崩溃时间
    val CRASH_TIME = preferencesKey<Long>("crash_time")
}

// 还有一个工厂类，去生成PreferencesDataStore的实例
object StoreFactory {
    @JvmStatic
    fun providePreferencesDataStore(context: Application): IDataStore {
        return PreferencesDataStore(context)
    }
}

