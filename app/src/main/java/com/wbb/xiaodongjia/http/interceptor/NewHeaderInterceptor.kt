package com.wbb.xiaodongjia.http.interceptor

import android.text.TextUtils
import com.wbb.base.BuildConfig
import com.wbb.base.util.AppUtil
import com.wbb.base.util.Constant
import com.wbb.base.util.MD5
import com.wbb.base.util.UserInfoManager
import com.zj.cpp.AppKeyManager
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by zj on 2021/2/25.
 * 添加http请求的head
 */
class NewHeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var sortParam = ""

        val body = chain.request().body()
        val buffer = Buffer()
        body?.writeTo(buffer)
        var charset = Charset.forName("UTF-8")
        val contentType = body?.contentType()
        if (contentType == null) {
            sortParam = ""
        } else if (TextUtils.equals(contentType.type() + contentType.subtype(), "applicationx-www-form-urlencoded")) {
            charset = contentType.charset(charset)
            val requestParams = buffer.readString(charset)
            sortParam = getSortForm(requestParams).toString()
        } else {
            charset = contentType.charset(charset)
            val requestParams = buffer.readString(charset)
            if (TextUtils.isEmpty(requestParams)) {//防止参数为空的时候，导致闪退
                sortParam = requestParams
            } else {
                sortParam = getSortJson(JSONObject(requestParams)).toString()
            }
        }

        val addHeader = chain.request().newBuilder()
        var plat = "LOCAL"
        if (BuildConfig.RELEASE_PLATFORM === 1) {
            plat = "GOOGLE"
        } else if (BuildConfig.RELEASE_PLATFORM === 2) {
            plat = "LOCAL"
        } else if (BuildConfig.RELEASE_PLATFORM === 3) {
            plat = "INSIDE"
        }
        val timeStamp = System.currentTimeMillis().toString()
        addHeader.addHeader(Constant.USER_TERMINAL, "PORTAL")
        addHeader.addHeader("Content-Type", "applicationx-www-form-urlencoded")
            .addHeader("DJIA-CLIENT-TYPE", "Android")
            .addHeader("DJIA-CLIENT-CHANNEL", plat)
            .addHeader("DJIA-CLIENT-VESION", AppUtil.getVerName().toString())
            .addHeader("DJIA-CLIENT-TIMESTAMP", timeStamp)
            .addHeader("DJIA-CLIENT-SIGN", MD5.md5(sortParam + timeStamp + AppKeyManager().httpKey()).toLowerCase(Locale.getDefault()))

        val userInfo = UserInfoManager.instance().getUserInfo()
        if (userInfo != null) {
            val token = userInfo.token
            addHeader.addHeader(Constant.USER_TOKEN, token)
        }
        val request = addHeader.build()
        return chain.proceed(request)
    }

    fun getSortJson(json: JSONObject): String? {
        val iteratorKeys: Iterator<String> = json.keys().iterator()
        val map: TreeMap<String?, String?> = TreeMap()
        while (iteratorKeys.hasNext()) {
            val key = iteratorKeys.next()
            val value = json.getString(key)
            map[key] = value
        }
        var sort = ""
        val keySets: List<String> = ArrayList<String>(map.keys)
        for (i in 0 until map.size) {
            val key = keySets[i]
            val value = map[key].toString()
            sort += key + value
        }
        return sort
    }

    fun getSortForm(form: String): String? {
        val list = ArrayList<String>()
        if (form.contains("&")) {
            val temp = form.split("&")
            list.addAll(temp)
        } else {
            list.add(form)
        }
        Collections.sort(list)
        var sort = ""
        for (i in list.indices) {
            list[i].replace("=", "")
            sort += list[i]
        }
        return sort
    }
}
