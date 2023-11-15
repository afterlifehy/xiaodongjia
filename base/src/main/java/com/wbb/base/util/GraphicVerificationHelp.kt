package com.wbb.base.util

import android.content.Context
import com.geetest.sdk.GT3ConfigBean
import com.geetest.sdk.GT3ErrorBean
import com.geetest.sdk.GT3GeetestUtils
import org.json.JSONObject
import com.geetest.sdk.GT3Listener as GT3Listener1

/**
 * Created by zj on 2020/12/23.
 * 图形验证码帮助类
 */
class GraphicVerificationHelp(context: Context?) : GT3Listener1() {
    var mOnGraphicVerificationLinsener: OnGraphicVerificationLinsener? = null
    var gt3GeetestUtils: GT3GeetestUtils? = null
    var context: Context? = null
    var gt3ConfigBean: GT3ConfigBean? = null

    init {
        this.context = context
        initGT3()
    }

    fun initGT3() {
        gt3GeetestUtils = GT3GeetestUtils(context)
        // 配置bean文件，也可在oncreate初始化
        gt3ConfigBean = GT3ConfigBean()
        // 设置验证模式，1：bind，2：unbind
        gt3ConfigBean?.pattern = 1
        // 设置点击灰色区域是否消失，默认不消息
        gt3ConfigBean?.isCanceledOnTouchOutside = false
        // 设置语言，如果为null则使用系统默认语言
        gt3ConfigBean?.lang = null
        // 设置加载webview超时时间，单位毫秒，默认10000，仅且webview加载静态文件超时，不包括之前的http请求
        gt3ConfigBean?.timeout = 10000
        // 设置webview请求超时(用户点选或滑动完成，前端请求后端接口)，单位毫秒，默认10000
        gt3ConfigBean?.webviewTimeout = 10000
        // 设置回调监听
        gt3ConfigBean?.listener = this
        gt3GeetestUtils?.init(gt3ConfigBean)
    }

    /**
     * 启动图形验证码验证
     */
    fun startCheckGraphic(mOnGraphicVerificationLinsener: OnGraphicVerificationLinsener?) {
        this.mOnGraphicVerificationLinsener = mOnGraphicVerificationLinsener
        gt3GeetestUtils?.startCustomFlow()
    }

    override fun onDialogReady(p0: String?) {
        super.onDialogReady(p0)
    }

    override fun onDialogResult(p0: String?) {
        super.onDialogResult(p0)
        mOnGraphicVerificationLinsener?.onGraphicCheckSucess(p0, this)
    }

    override fun onReceiveCaptchaCode(p0: Int) {
    }

    override fun onStatistics(p0: String?) {

    }

    override fun onClosed(p0: Int) {
    }

    override fun onSuccess(p0: String?) {
    }

    override fun onFailed(p0: GT3ErrorBean?) {
        mOnGraphicVerificationLinsener?.onFailed(p0)
    }

    /**
     * 继续加重效果
     */
    fun getGeetest(var1: JSONObject) {
        gt3ConfigBean?.api1Json = var1
        gt3GeetestUtils?.getGeetest()
    }

    /**
     * 显示错误提示
     */
    fun showFailedDialog() {
        gt3GeetestUtils?.showFailedDialog()
    }

    fun showSuccessDialog() {
        gt3GeetestUtils?.showSuccessDialog()
    }

    /**
     * 关闭
     */
    fun dismissGeetestDialog() {
        gt3GeetestUtils?.dismissGeetestDialog()
    }

    override fun onButtonClick() {
        mOnGraphicVerificationLinsener?.onGraphicStartSucess(this)
    }

    interface OnGraphicVerificationLinsener {
        fun onGraphicStartSucess(mGraphicVerificationHelp: GraphicVerificationHelp)
        fun onFailed(p0: GT3ErrorBean?)
        fun onGraphicCheckSucess(p0: String?, mGraphicVerificationHelp: GraphicVerificationHelp)
    }

}