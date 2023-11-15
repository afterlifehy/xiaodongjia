package com.wbb.base.view.bridgewebview

interface WebViewJavascriptBridge {

    fun send(data: String)
    fun send(data: String, responseCallback: CallBackFunction)


}
