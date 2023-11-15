package com.wbb.xiaodongjia.base

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.jiguang.verifysdk.api.JVerificationInterface
import com.wbb.xiaodongjia.roomdao.AppRoomDatabase
import com.wbb.base.BaseApplication
import com.wbb.base.http.interceptor.TokenInterceptor
import com.wbb.xiaodongjia.BuildConfig
import com.wbb.xiaodongjia.http.interceptor.HostInterceptor
import com.wbb.xiaodongjia.http.interceptor.LoginExpiredInterceptor
import com.wbb.xiaodongjia.http.interceptor.NewHeaderInterceptor
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by zj on 2021/2/24.
 */
class AppApplication : BaseApplication() {
    companion object {
        var _context: BaseApplication? = null
        fun instance(): BaseApplication {
            return _context!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        _context = this
        //初始化数据库
        //支付宝沙箱环境
//        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        //一键登录初始化
        JVerificationInterface.init(this)
        JVerificationInterface.setDebugMode(true)
        //初始化数据库
        val mAppDatabase = Room.databaseBuilder(
            applicationContext,
            AppRoomDatabase::class.java, "android_room_xdj.db"
        )
            .allowMainThreadQueries()
            .addMigrations(MIGRATION_2_3)
            .build()
    }

    val MIGRATION_2_3: Migration = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
        }
    }

    override fun onAddOkHttpInterceptor(): List<Interceptor> {
        val list = ArrayList<Interceptor>()
        list.add(NewHeaderInterceptor())
        list.add(LoginExpiredInterceptor())
        list.add(HostInterceptor())
        list.add(TokenInterceptor())
        if (BuildConfig.is_debug) {
            //list.add(LogInterceptor(BuildConfig.is_debug))
            val mHttpLoggingInterceptor = HttpLoggingInterceptor()
            mHttpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            list.add(mHttpLoggingInterceptor)
        }

        return list
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

}