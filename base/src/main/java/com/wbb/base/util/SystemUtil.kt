package com.wbb.base.util

import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.wbb.base.BaseApplication
import com.wbb.base.pf.PreferencesKeys
import com.wbb.base.pf.StoreFactory
import kotlinx.coroutines.*
import java.lang.reflect.InvocationTargetException
import java.util.*

object SystemUtil {
    //meid
//androidID
//SN
//如果上面都没有， 则生成一个id：随机码
//获取手机的唯一标识
    @get:RequiresApi(api = Build.VERSION_CODES.O)
    val phoneSign: String
        get() {
            var sign = ""
            //meid
            val meid = mEID
            if (!TextUtils.isEmpty(meid)) {
                sign = meid
                return sign
            }
            //androidID
            val androidId = Settings.Secure.getString(
                BaseApplication.instance().getContentResolver(),
                Settings.Secure.ANDROID_ID
            )
            if (!TextUtils.isEmpty(androidId)) {
                sign = androidId
                return sign
            }
            //SN
            val serial = Build.SERIAL
            if (!TextUtils.isEmpty(serial)) {
                sign = serial
                return sign
            }
            //如果上面都没有， 则生成一个id：随机码
            val uuid = uUID
            if (!TextUtils.isEmpty(uuid)) {
                sign = uuid
                return sign
            }
            return sign
        }

    /**
     * 获取 MEID,MEID 只有一个
     */
    val mEID: String
        get() {
            try {
                val clazz = Class.forName("android.os.SystemProperties")
                val method = clazz.getMethod("get", String::class.java, String::class.java)
                val meid = method.invoke(null, "ril.cdma.meid", "") as String
                if (!TextUtils.isEmpty(meid) && "unknown" != meid) {
                    return meid
                }
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
            return ""
        }

    /**
     * 得到全局唯一UUID
     */
    val uUID: String
        get() {
            var uuid: String
            uuid = runBlocking {
                StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                    .getString(PreferencesKeys.UUID)
            }
            if (TextUtils.isEmpty(uuid)) {
                uuid = UUID.randomUUID().toString()
                GlobalScope.launch(Dispatchers.Main) {
                    StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                        .putString(
                            PreferencesKeys.UUID, uuid
                        )
                }
            }
            return uuid
        }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    val systemModel: String
        get() = Build.MODEL

    /**
     * 获取终端品牌
     */
    val brand: String
        get() = Build.BRAND
}