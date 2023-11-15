package com.wbb.base.http

import android.app.Application
import android.util.Log
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.wbb.base.BaseApplication
import com.wbb.base.http.interceptor.*
import com.wbb.base.util.Constant
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 *  Created by zhangqi on 2019/4/22.
 */
object HttpBuilder {

    /**
     * 构建Gson
     * @return
     */
    @JvmStatic
    fun getGson(): Gson = Gson()

    /**
     * 构建缓存
     * @param application
     * @return
     */
    @JvmStatic
    fun getCache(application: Application): Cache = Cache(application.cacheDir, 10 * 1024 * 1024)

    /**
     * 构建 okhttp 日志
     * @return
     */
    @JvmStatic
    fun getHttplogger(): HttpLoggingInterceptor {
        val logInterceptor = HttpLoggingInterceptor("okhttp3")
        logInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY)
        return logInterceptor
    }

    @JvmStatic
    fun getAllListInterceptor(): List<Interceptor> {
        val lissInterceptor = ArrayList<Interceptor>()
        if (BaseApplication.baseApplication is OnAddOkhttpInterceptor) {
            val list =
                (BaseApplication.baseApplication as OnAddOkhttpInterceptor).onAddOkHttpInterceptor()
            lissInterceptor.addAll(list)

        }
        return lissInterceptor
    }

    @JvmStatic
    fun getHeaderInterceptor(): HeaderInterceptor = HeaderInterceptor()

    /**
     * 构建okhttpbuilder
     * @param interceptor
     * @param headerInterceptor
     * @return
     */
    @JvmStatic
    fun getOkhttpBuilder(
        list: List<Interceptor>
    ): OkHttpClient.Builder {
        val sslParams = RetrofitUtils.getSSLParams()
        val mBuilder = OkHttpClient.Builder()
            .connectTimeout(Constant.TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constant.TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constant.TIMEOUT, TimeUnit.SECONDS)
            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)//证书
        BaseApplication.instance().getOnAppBaseProxyLinsener()?.let {
            if (!it.onIsProxy()) {//设置不允许抓包
                mBuilder.proxy(Proxy.NO_PROXY)
            }
        }
        list.forEach {
            mBuilder.addInterceptor(it)
        }
        return mBuilder

    }

    /**
     * 构建okhttpclient
     * @param builder
     * @return
     */
    @JvmStatic
    fun getOkhttpClient(builder: OkHttpClient.Builder): OkHttpClient {

        return builder.build()
    }

    /**
     * 构建retrofitbuilder
     * @param gson
     * @param client
     * @return
     */
    @JvmStatic
    fun getRetrofitBuilder(gson: Gson, client: OkHttpClient, baseurl: String): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(baseurl)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }

    /**
     * 构建支持协程的Retrofit
     */
    @JvmStatic
    fun getCoroutineRetrofitBuilder(
        gson: Gson,
        client: OkHttpClient,
        baseurl: String
    ): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(baseurl)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
    }

    /**
     * 构建retrofit
     * @param builder
     * @return
     */
    @JvmStatic
    fun getRetrofit(builder: Retrofit.Builder): Retrofit {
        return builder.build()
    }

}