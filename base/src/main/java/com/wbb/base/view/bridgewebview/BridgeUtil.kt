package com.wbb.base.view.bridgewebview

import android.content.Context
import android.webkit.WebView

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object BridgeUtil {
    internal val WVJB_OVERRIDE_SCHEMA = "wvjbscheme://"
    val YY_OVERRIDE_SCHEMA = "yy://"
    val YY_RETURN_DATA = YY_OVERRIDE_SCHEMA + "return/"//格式为   yy://return/{function}/returncontent
    val YY_FETCHQUEUE = YY_RETURN_DATA + "_fetchQueue/"
    internal val WVJB_BRIDGE_LOADED = WVJB_OVERRIDE_SCHEMA + "__BRIDGE_LOADED__" // load bridge
    internal val WVJB_QUEUE_HAS_MESSAGE = WVJB_OVERRIDE_SCHEMA + "__WVJB_QUEUE_MESSAGE__" // queue has message
    internal val WVJB_RETURN_DATA = WVJB_OVERRIDE_SCHEMA + "return/"//格式为   yy://return/{function}/returncontent
    internal val WVJB_FETCH_QUEUE = WVJB_RETURN_DATA + "_fetchQueue/"
    internal val INTENT = "intent"
    internal val EMPTY_STR = ""
    internal val UNDERLINE_STR = "_"
    internal val SPLIT_MARK = "/"
    internal val PPT="https://phk1-powerpoint.officeapps.live.com"

    internal val CALLBACK_ID_FORMAT = "JAVA_CB_%s"
    internal val JS_HANDLE_MESSAGE_FROM_JAVA = "javascript:WebViewJavascriptBridge._handleMessageFromObjC('%s');"
    internal val JS_FETCH_QUEUE_FROM_JAVA = "javascript:WebViewJavascriptBridge._fetchQueue();"

    fun parseFunctionName(jsUrl: String): String {
        return jsUrl.replace("javascript:WebViewJavascriptBridge.", "").replace("\\(.*\\);".toRegex(), "")
    }

    fun getDataFromReturnUrl(url: String): String? {
        if (url.startsWith(WVJB_FETCH_QUEUE)) {
            return url.replace(WVJB_FETCH_QUEUE, EMPTY_STR)
        }

        val temp = url.replace(WVJB_RETURN_DATA, EMPTY_STR)
        val functionAndData = temp.split(SPLIT_MARK.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (functionAndData.size >= 2) {
            val sb = StringBuilder()
            for (i in 1 until functionAndData.size) {
                sb.append(functionAndData[i])
            }
            return sb.toString()
        }
        return null
    }

    fun getFunctionFromReturnUrl(url: String): String? {
        val temp = url.replace(WVJB_RETURN_DATA, EMPTY_STR)
        val functionAndData = temp.split(SPLIT_MARK.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (functionAndData.size >= 1) {
            functionAndData[0]
        } else null
    }

    fun webViewLoadLocalJs(view: WebView, path: String) {
        val jsContent = assetFile2Str(view.context, path)
        view.loadUrl("javascript:$jsContent")
    }

    fun assetFile2Str(c: Context, urlStr: String): String? {
        var `in`: InputStream? = null
        try {
            `in` = c.assets.open(urlStr)
            val bufferedReader = BufferedReader(InputStreamReader(`in`))
            var line: String? = null
            val sb = StringBuilder()
            do {
                line = bufferedReader.readLine()
                if (line != null && !line.matches("^\\s*\\/\\/.*".toRegex())) {
                    sb.append(line)
                }
            } while (line != null)

            bufferedReader.close()
            `in`!!.close()

            return sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                }

            }
        }
        return null
    }
}
