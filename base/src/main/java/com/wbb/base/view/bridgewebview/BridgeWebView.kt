package com.chouyou.base.view.bridgewebview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Looper
import android.os.SystemClock
import android.text.TextUtils
import android.util.AttributeSet
import android.webkit.*
import android.webkit.WebView
import androidx.core.content.ContextCompat
import com.wbb.base.BaseApplication
import com.wbb.base.R
import com.wbb.base.util.*
import com.wbb.base.view.bridgewebview.*
import okhttp3.OkHttpClient
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.net.URLDecoder
import java.util.*
import javax.net.ssl.HttpsURLConnection


@SuppressLint("SetJavaScriptEnabled")
class BridgeWebView : WebView, WebViewJavascriptBridge {

    private val TAG = "BridgeWebView"
    var t = 0
    private var context1: Context? = null
    internal var responseCallbacks: MutableMap<String, CallBackFunction> = HashMap()
    internal var messageHandlers: MutableMap<String, BridgeHandler> = HashMap()
    private var startupMessage: MutableList<Message>? = ArrayList()
    private var webViewUrlChangeListener: WebViewUrlChangeListener? = null
    private var mWebViewScrollChanged: WebViewScrollChanged? = null
    private var uniqueId: Long = 0
    private var sslParams: HttpsUtils.SSLParams? = null
    private var mOkHttpClient: OkHttpClient? = null

    fun setWebViewScrollChanged(mWebViewScrollChanged: WebViewScrollChanged) {
        this.mWebViewScrollChanged = mWebViewScrollChanged
    }

    fun setWebViewUrlChangeListener(webViewUrlChangeListener: WebViewUrlChangeListener) {
        this.webViewUrlChangeListener = webViewUrlChangeListener
    }

    fun getStartupMessage(): List<Message>? {
        return startupMessage
    }

    fun setStartupMessage(startupMessage: MutableList<Message>?) {
        this.startupMessage = startupMessage
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.context1 = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        this.context1 = context
        init()
    }

    constructor(context: Context) : super(context) {
        this.context1 = context
        init()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        val url = url
        if (!TextUtils.isEmpty(url) && url?.contains("order.html")!!) {
            this.t = t
        }
    }

    private fun init() {
//        var byteArrayInputStream = ByteArrayInputStream(Constant.cerStr.toByteArray(Charsets.UTF_8))
//        sslParams = HttpsUtils.getSslSocketFactory(byteArrayInputStream)
//        mOkHttpClient = OkHttpClient.Builder()
//            .sslSocketFactory(sslParams!!.sSLSocketFactory, sslParams!!.trustManager).build()//证书

        this.isVerticalScrollBarEnabled = false
        this.isHorizontalScrollBarEnabled = false
        this.settings.javaScriptEnabled = true
        this.settings.databaseEnabled = true
        this.settings.allowFileAccess = true
        this.settings.savePassword = false
        this.settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        this.settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        this.settings.setAppCacheEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        this.settings.javaScriptCanOpenWindowsAutomatically = true
        this.settings.pluginState = WebSettings.PluginState.ON // 支持插件
        this.settings.defaultTextEncodingName = "UTF-8"
        this.settings.useWideViewPort = false
        this.settings.loadsImagesAutomatically = true//自动加载图片
        this.webViewClient = generateBridgeWebViewClient()

        this.webChromeClient = WebChromClient()
        setBackgroundColor(ContextCompat.getColor(BaseApplication.instance(), R.color.white_bg))
    }

    private fun generateBridgeWebViewClient(): BridgeWebViewClient {
        return BridgeWebViewClient()
    }

    internal fun handlerReturnData(url: String?) {
        val functionName = BridgeUtil.getFunctionFromReturnUrl(url!!)
        val f = responseCallbacks[functionName]
        val data = BridgeUtil.getDataFromReturnUrl(url)
        if (f != null) {
            f.onCallBack(data!!)
            responseCallbacks.remove(functionName)
            return
        }
    }

    private fun doSend(handlerName: String?, data: String, responseCallback: CallBackFunction?) {
        val m = Message()
        if (!TextUtils.isEmpty(data)) {
            m.data = data
        }
        if (responseCallback != null) {
            val callbackStr = String.format(
                BridgeUtil.CALLBACK_ID_FORMAT,
                "" + (++uniqueId) + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis())
            )
            responseCallbacks[callbackStr] = responseCallback
            m.callbackId = callbackStr
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.handlerName = handlerName
        }
        queueMessage(m)
    }

    private fun queueMessage(m: Message) {
        if (startupMessage != null) {
            startupMessage!!.add(m)
        } else {
            dispatchMessage(m)
        }
    }

    internal fun dispatchMessage(m: Message) {
        var messageJson = m.toJson()
        //escape special characters for json string
        messageJson = messageJson!!.replace("(\\\\)([^utrn])".toRegex(), "\\\\\\\\$1$2")
        messageJson = messageJson.replace("(?<=[^\\\\])(\")".toRegex(), "\\\\\"")
        val javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson)
        if (Thread.currentThread() === Looper.getMainLooper().thread) {
            this.loadUrl(javascriptCommand)
        }
    }

    internal fun flushMessageQueue() {
        if (Thread.currentThread() === Looper.getMainLooper().thread) {
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, object : CallBackFunction {

                override fun onCallBack(data: String) {
                    // deserializeMessage
                    var list: List<Message>? = null
                    try {
                        list = Message.toArrayList(data)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return
                    }

                    if (list == null || list.size == 0) {
                        return
                    }
                    for (i in list.indices) {
                        val m = list[i]
                        val responseId = m.responseId
                        // 是否是response
                        if (!TextUtils.isEmpty(responseId)) {
                            val function = responseCallbacks[responseId]
                            val responseData = m.responseData
                            function!!.onCallBack(responseData!!)
                            responseCallbacks.remove(responseId)
                        } else {
                            var responseFunction: CallBackFunction? = null
                            // if had callbackId
                            val callbackId = m.callbackId
                            if (!TextUtils.isEmpty(callbackId)) {
                                responseFunction = object : CallBackFunction {
                                    override fun onCallBack(data: String) {
                                        val responseMsg = Message()
                                        responseMsg.responseId = callbackId
                                        responseMsg.responseData = data
                                        queueMessage(responseMsg)
                                    }
                                }
                            } else {
                                responseFunction = object : CallBackFunction {
                                    override fun onCallBack(data: String) {
                                        // do nothing
                                    }
                                }
                            }
                            var handler: BridgeHandler? = null
                            if (!TextUtils.isEmpty(m.handlerName)) {
                                handler = messageHandlers[m.handlerName]
                            } else {
                                // no handler found
                            }
                            handler?.handler(m.data!!, responseFunction)
                        }
                    }
                }
            })
        }
    }

    fun loadUrl(jsUrl: String, returnCallback: CallBackFunction) {
        this.loadUrl(jsUrl)
        responseCallbacks[BridgeUtil.parseFunctionName(jsUrl)] = returnCallback
    }

    /**
     * register handler,so that javascript can call it
     *
     * @param handlerName
     * @param handler
     */
    fun registerHandler(handlerName: String, handler: BridgeHandler?) {
        if (handler != null) {
            messageHandlers[handlerName] = handler
        }
    }

    /**
     * call javascript registered handler
     *
     * @param handlerName
     * @param data
     * @param callBack
     */
    fun callHandler(handlerName: String, data: String, callBack: CallBackFunction) {
        doSend(handlerName, data, callBack)
    }

    override fun send(data: String) {
        send(data, null!!)
    }

    override fun send(data: String, responseCallback: CallBackFunction) {
        doSend(null, data, responseCallback)
    }

    public override fun computeVerticalScrollRange(): Int {
        return super.computeVerticalScrollRange()
    }

    inner class WebChromClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (webViewUrlChangeListener != null) {
                webViewUrlChangeListener!!.loadProgress(newProgress)
            }
            super.onProgressChanged(view, newProgress)
        }

        // For Android 5.0+
        override fun onShowFileChooser(
            webView: WebView?,
            valueCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: WebChromeClient.FileChooserParams?
        ): Boolean {
            return super.onShowFileChooser(webView, valueCallback, fileChooserParams)
        }
    }

    inner class BridgeWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            var url = url
            try {
                url = URLDecoder.decode(url, "UTF-8")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (url!!.startsWith(BridgeUtil.WVJB_BRIDGE_LOADED)) {
                BridgeUtil.webViewLoadLocalJs(view!!, toLoadJs)

                if (getStartupMessage() != null) {
                    for (m in getStartupMessage()!!) {
                        dispatchMessage(m)
                    }
                    setStartupMessage(null)
                }
                return true
            } else if (url.startsWith(BridgeUtil.WVJB_RETURN_DATA)) { // 如果是返回数据
                handlerReturnData(url)
                return true
            } else if (url.startsWith(BridgeUtil.WVJB_QUEUE_HAS_MESSAGE)) { // 消息队列有数据
                flushMessageQueue()
                return true
            } else if (url.endsWith("apk")) {
                StringUtil.copyString(url)
                AppUtil.goBrowser(url)
                return true
            } else return if (url.startsWith(BridgeUtil.INTENT)) {
                true
            } else {
                super.shouldOverrideUrlLoading(view, url)
            }
        }

        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (webViewUrlChangeListener != null) {
                webViewUrlChangeListener!!.onPageFinsh()
            }
            if (startupMessage != null) {
                for (m in startupMessage!!) {
                    dispatchMessage(m)
                }
                startupMessage = null
            }
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            if (webViewUrlChangeListener != null) {
                view!!.url?.let { webViewUrlChangeListener!!.changedUrl(it) }
            }
            super.onLoadResource(view, url)
        }

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            //6.0以下执行
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return
            }
            if (webViewUrlChangeListener != null) {
                webViewUrlChangeListener!!.onPageError()
            }
        }

        override fun onReceivedError(p0: WebView?, p1: WebResourceRequest?, p2: WebResourceError?) {
            super.onReceivedError(p0, p1, p2)
            if (p1!!.isForMainFrame) {
                if (webViewUrlChangeListener != null) {
                    webViewUrlChangeListener!!.onPageError()
                }
            }
        }

        override fun onReceivedHttpError(
            p0: WebView?,
            p1: WebResourceRequest?,
            p2: WebResourceResponse?
        ) {
            super.onReceivedHttpError(p0, p1, p2)
            // 这个方法在 android 6.0才出现
            val statusCode: Int = p2!!.statusCode
            if (404 == statusCode || 500 == statusCode) {
                if (webViewUrlChangeListener != null) {
                    if (!p0?.url?.startsWith(BridgeUtil.PPT)!!) {
                        webViewUrlChangeListener!!.onPageError()
                    }
                }
            }
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            handler!!.proceed()
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            return view?.let { request?.url?.let { it1 -> checkSsl(it, it1) } };
        }

        override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
            return view?.let { checkSsl(it, Uri.parse(url)) };
        }
    }

    private fun checkSsl(view: WebView, uri: Uri): WebResourceResponse? {
        val urlPath = uri.toString()
//        if (!urlPath.contains(Constant.INSIDE_URL)) {
//            return null
//        }

//        val request: Request = Request.Builder().url(urlPath)
//            .get() //默认就是GET请求，可以不写
//            .build()
//        val call: Call = mOkHttpClient!!.newCall(request)
//        try {
//            val response = call.execute()
//            return WebResourceResponse(
//                response.body()!!.contentType()!!.toString(),
//                null,
//                response.body()!!.byteStream()
//            )
//
//        } catch (e: java.lang.Exception) {
//            return WebResourceResponse(null, null, null)
//        }


        var urlConnection: URLConnection? = null
        try {
            val url = URL(urlPath)
            urlConnection = url.openConnection()
            if (urlConnection is HttpsURLConnection) {
                val httpsURLConnection: HttpsURLConnection? = urlConnection
                httpsURLConnection?.instanceFollowRedirects = false
                httpsURLConnection?.sslSocketFactory = sslParams?.sSLSocketFactory
                httpsURLConnection?.setHostnameVerifier { _, _ -> true }
                val respCode: Int = httpsURLConnection?.responseCode!!
                if (respCode == 301 || respCode == 302) {
                    httpsURLConnection.disconnect()
                    return null
                }
                if (respCode != 200) {
                    httpsURLConnection.disconnect()
                    return null
                }
            }
            val `is`: InputStream = urlConnection.getInputStream()
            val contentType: String = urlConnection.contentType
            val encoding = urlConnection.contentEncoding
            if (contentType != null) {
                var mimeType = contentType
                if (contentType.contains(";")) {
                    mimeType = contentType.split(";".toRegex()).toTypedArray()[0].trim { it <= ' ' }
                }
                return WebResourceResponse(mimeType, encoding, `is`)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (urlConnection != null) {
            if (urlConnection is HttpsURLConnection) {
                urlConnection.disconnect()
            } else if (urlConnection is HttpURLConnection) {
                urlConnection.disconnect()
            }
        }
        return WebResourceResponse(null, null, null)
    }

    interface WebViewUrlChangeListener {
        fun changedUrl(url: String)

        fun onPageFinsh()

        fun loadProgress(progress: Int)

        fun onPageError()

    }

    interface WebViewScrollChanged {
        fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int)
    }

    companion object {
        val toLoadJs = "WebViewJavascriptBridge.js"
        private var webViewUrlChangeListener: WebViewUrlChangeListener? = null
    }
}
