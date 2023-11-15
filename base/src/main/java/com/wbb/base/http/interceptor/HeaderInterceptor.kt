package com.wbb.base.http.interceptor

import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.wbb.base.util.*
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

/**
 *  Created by zhangqi on 2019/4/22.
 */
class HeaderInterceptor : Interceptor {
    private val secret = "waterbridgedsahuyong"

    companion object {}

    @RequiresApi(Build.VERSION_CODES.O)
    override fun intercept(chain: Interceptor.Chain): Response {
        val timeStep = (System.currentTimeMillis() / 1000).toString()
        var sign = ""

        val body = chain.request().body()
        val buffer = Buffer()
        body?.writeTo(buffer)
        var charset = Charset.forName("UTF-8")
        val contentType = body?.contentType()
        if (contentType == null) {
            sign = MD5.md5(secret + timeStep)
        } else if (TextUtils.equals(contentType.type() + contentType.subtype(), "applicationx-www-form-urlencoded")) {
            charset = contentType.charset(charset)
            val requestParams = buffer.readString(charset)
            sign = MD5.md5(getSortForm(requestParams) + secret + timeStep)
        } else {
            charset = contentType.charset(charset)
            val requestParams = buffer.readString(charset)
            if (TextUtils.isEmpty(requestParams)) {//防止参数为空的时候，导致闪退
                sign = MD5.md5(requestParams + secret + timeStep)
            } else {
                sign = MD5.md5(getSortJson(JSONObject(requestParams)) + secret + timeStep)
            }
        }

        val addHeader = chain.request().newBuilder()

//            .addHeader(Constant.TIMESTAMP, SpUtil.getInstance().get(Constant.TIMESTAMP))
//            .addHeader(Constant.USERID, SpUtil.getInstance().get(Constant.USERID))
//            .addHeader(Constant.TICKETCODE, SpUtil.getInstance().get(Constant.TICKETCODE))
//            .addHeader(
//                Constant.VALUATIONCURRENCY, SpUtil.getInstance().get(Constant.VALUATIONCURRENCY)
//            ).addHeader(
//                Constant.LANGUAGE,
//                AppFlag.languageSymbolMap.get(SpUtil.getInstance().get(Constant.LANGUAGE))
//            ).addHeader(Constant.WBID, MagicValue.WBID + AppUtil.getVerName())
//            .addHeader(
//                Constant.SYSTEM_MODEL, "${SystemUtil.brand}-${SystemUtil.systemModel}"
//            ).addHeader(Constant.SIGN, sign).addHeader(Constant.TIMESTEP, timeStep)
//            .addHeader(Constant.XAUTH_TOKEN, SpUtil.getInstance()[Constant.XAUTH_TOKEN])

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