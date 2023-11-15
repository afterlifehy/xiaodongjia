package com.wbb.xiaodongjia.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.text.TextUtils
import android.view.View
import android.webkit.*
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.amap.api.mapcore.util.it
import com.aries.ui.util.StatusBarUtil
import com.chouyou.base.base.ShareItemData
import com.chouyou.base.ext.gone
import com.chouyou.base.ext.show
import com.github.dfqin.grantor.PermissionListener
import com.github.dfqin.grantor.PermissionsUtil
import com.lianlianpay.cashier.PayService
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.wbb.base.BaseApplication
import com.wbb.base.bean.HomeAttUpData
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.dialog.ShareDialog
import com.wbb.base.emuns.SHARE_EMUN
import com.wbb.base.ext.i18N
import com.wbb.base.help.WxSharManager
import com.wbb.base.observer.ObserverKey
import com.wbb.base.observer.ObserverManger
import com.wbb.base.observer.OnObserver
import com.wbb.base.util.*
import com.wbb.base.view.dsbridge.DWebView
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.base.VbBaseActivity
import com.wbb.xiaodongjia.bean.PayChannelParamsBean
import com.wbb.xiaodongjia.bean.PayResult
import com.wbb.xiaodongjia.databinding.ActivityWebviewBinding
import com.wbb.xiaodongjia.dialog.ChooseMapDialog
import com.wbb.xiaodongjia.dialog.ChoosePayMethodDialog
import com.wbb.xiaodongjia.dialog.SelectPicDialog
import com.wbb.xiaodongjia.mvvm.viewmodel.WebviewViewModel
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import wendu.dsbridge.CompletionHandler
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by hy on 2021/2/27.
 */
@Route(path = ARouterMap.WEBVIEW)
class WebViewActivity : VbBaseActivity<WebviewViewModel, ActivityWebviewBinding>(),
    View.OnClickListener,
    ShareDialog.OnShareClickLisener, OnObserver {
    var url = ""
    var mUploadMessageForAndroid5: ValueCallback<Array<Uri>>? = null
    var shareDialog: ShareDialog? = null
    var selectPicDialog: SelectPicDialog? = null
    var handler: CompletionHandler<Any>? = null
    var tempFile: File? = null
    var shareObject: JSONObject? = null
    var chooseMapDialog: ChooseMapDialog? = null
    var choosePayMethodDialog: ChoosePayMethodDialog? = null
    var payType = ""
    var money = "0.0"

    override fun onReloadData() {

    }

    override fun providerVMClass(): Class<WebviewViewModel>? {
        return WebviewViewModel::class.java
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false


    /**
     * 分享按钮显示
     */
    @JavascriptInterface
    fun shareUrl(args: Any?, handler: CompletionHandler<Any>) {
        shareObject = JSON.parseObject(args.toString())
        runOnUiThread {
            binding.titleTop.tvRight.text = i18N(R.string.分享)
            binding.titleTop.tvRight.show()
        }
    }

    /**
     * 打电话
     */
    @JavascriptInterface
    fun telPhone(args: Any?, handler: CompletionHandler<Any>) {
        val jsonObject = JSONObject.parseObject(args.toString())
        AppUtil.telPhone(jsonObject.getString("phone"))
    }

    /**
     * 沉浸式
     */
    @JavascriptInterface
    fun changeNavigationBarStyle(args: Any?, handler: CompletionHandler<Any>) {
        val jsonObject = JSONObject.parseObject(args.toString())
        var type = jsonObject.getIntValue("type")
        if (type == 3) {
            runOnUiThread {

                binding.titleTop.ablToolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white_bg
                    )
                )
                binding.titleTop.toolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white_bg
                    )
                )
                binding.viewMask.show()
            }
        } else {
            runOnUiThread {
                if (type == 1) {

                    GlideUtil.loadImage(R.mipmap.ic_arrow_back_black_base, binding.titleTop.ivBack)

                    binding.titleTop.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            this@WebViewActivity,
                            R.color.color_333333
                        )
                    )
                } else if (type == 2) {
                    GlideUtil.loadImage(R.mipmap.ic_arrow_back_white_base, binding.titleTop.ivBack)
                    binding.titleTop.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            this@WebViewActivity,
                            R.color.white
                        )
                    )
                }
                binding.titleTop.ablToolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.transparent
                    )
                )
                binding.titleTop.toolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.transparent
                    )
                )
                binding.viewMask.gone()
            }
        }
    }

    /**
     * 调用相册相机
     */
    @JavascriptInterface
    fun getPic(args: Any?, handler: CompletionHandler<Any>) {
        this.handler = handler
        PermissionsUtil.requestPermission(
            BaseApplication.instance(), object : PermissionListener {
                override fun permissionGranted(permission: Array<out String>) {
                    selectPicDialog =
                        SelectPicDialog(this@WebViewActivity, object : SelectPicDialog.Callback {
                            override fun onTakePhoto() {
                                CameraUtil.takePhoto()
                            }

                            override fun onPickPhoto() {
                                CameraUtil.selectPhoto()
                            }

                        })
                    selectPicDialog?.show()
                }

                override fun permissionDenied(permission: Array<out String>) {
                    ToastUtils.showToash("没有读写权限，不能保存图片")
                }
            },
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }

    /**
     * 查看路线
     */
    @JavascriptInterface
    fun gotoMap(args: Any?, handler: CompletionHandler<Any>) {
        val latLng = JSONObject.parseObject(args.toString())
        if (chooseMapDialog == null) {
            chooseMapDialog = ChooseMapDialog(
                this@WebViewActivity,
                R.style.CommonBottomDialogStyle,
                latLng.getString("jing"),
                latLng.getString("wei"),
                latLng.getString("name")
            )
        }
        chooseMapDialog?.show()
    }

    /**
     * 买单
     */
    @JavascriptInterface
    fun payMoney(args: Any?, handler: CompletionHandler<Any>) {
        val jsonObject = JSONObject.parseObject(args.toString())
        ARouter.getInstance().build(ARouterMap.PAYBILL)
            .withString(ARouterMap.MERCHANTID, jsonObject.getString("id"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
        OnBuriedPointManager.get().getOnBuriedPointManager()?.ButtonClick("首页","商户详情页","买单")
    }

    /**
     * plus充值
     */
    @JavascriptInterface
    fun plusRecharge(args: Any?, handler: CompletionHandler<Any>) {
        val jsonObject = JSONObject.parseObject(args.toString())
        choosePayMethodDialog = null
        choosePayMethodDialog = ChoosePayMethodDialog(
            this,
            R.style.CommonBottomDialogStyle,
            jsonObject.getString("money"),
            object : ChoosePayMethodDialog.choosePayMethodCallback {
                override fun makeSure(paymentType: String) {
                    var par = HashMap<String, String>()
                    money = jsonObject.getString("money")
                    par["amount"] = money
                    par["payType"] = paymentType
                    par["rechargeType"] = "0"
                    payType = paymentType
                    mViewModel.initRecharge(par)
                    OnBuriedPointManager.get().getOnBuriedPointManager()?.PlusRecharge(money, 1, "")
                }
            })
        choosePayMethodDialog?.show()
    }

    /**
     * 东家充值
     */
    @JavascriptInterface
    fun dongjiaRecharge(args: Any?, handler: CompletionHandler<Any>) {
        val jsonObject = JSONObject.parseObject(args.toString())
        choosePayMethodDialog = null
        choosePayMethodDialog = ChoosePayMethodDialog(
            this,
            R.style.CommonBottomDialogStyle,
            jsonObject.getString("money"),
            object : ChoosePayMethodDialog.choosePayMethodCallback {
                override fun makeSure(paymentType: String) {
                    var par = HashMap<String, String>()
                    money = jsonObject.getString("money")
                    par["amount"] = money
                    par["merchantId"] = jsonObject.getString("id")
                    par["payType"] = paymentType
                    par["rechargeType"] = "1"
                    payType = paymentType
                    mViewModel.initRecharge(par)
                    OnBuriedPointManager.get().getOnBuriedPointManager()?.DjRecharge(
                        jsonObject.getString("id"),
                        jsonObject.getString("money"),
                        1,
                        ""
                    )
                }
            })
        choosePayMethodDialog?.show()
    }

    @JavascriptInterface
    fun getFocus(args: Any?, handler: CompletionHandler<Any>) {
        val homeAttUpData = GsonUtils.fromJson(args.toString(), HomeAttUpData::class.java)
        ObserverManger.getInstance(ObserverKey.HOME_ATT_UP).notifyObserver(homeAttUpData)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mInitRechargeLiveData.observe(this@WebViewActivity, Observer {
                when (payType) {
                    "llaccp_wxpay" -> {
                        callWxApplet(it.orderId)
                    }
                    "llaccp_alipay" -> {
//                        callAlipay(orderInfo)
                    }
                }
            })
            errMsg.safeObserve(this@WebViewActivity, Observer {
                ToastUtils.showErrorToast(it.msg)
            })
            mException.observe(this@WebViewActivity, Observer {
                LogUtil.v(it.toString())
            })
        }
    }

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val payResult = PayResult(msg.obj as Map<String?, String?>)
            when (payResult.resultStatus) {
                "9000" -> {
                    ARouter.getInstance().build(ARouterMap.PAY_RESULT)
                        .withInt(ARouterMap.PAYMENT_STATUS, 2).withInt(ARouterMap.PAY_FROM, 2)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                }
                else -> {
                    ARouter.getInstance().build(ARouterMap.PAY_RESULT)
                        .withInt(ARouterMap.PAYMENT_STATUS, 3).withInt(ARouterMap.PAY_FROM, 2)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                }
            }
        }
    }

    fun callAlipay(orderInfo: String) {
        PayService.pay(this@WebViewActivity, mHandler, "", "")
    }

    fun callWxPay(payChannelParams: String) {
        val payChannelParamsBean =
            GsonUtils.fromJson(payChannelParams, PayChannelParamsBean::class.java)
        val api: IWXAPI = WXAPIFactory.createWXAPI(BaseApplication.instance(), null)
        val request = PayReq()
        request.appId = payChannelParamsBean.appid//子商户appid
        request.partnerId = payChannelParamsBean.partnerid//子商户号
        request.prepayId = payChannelParamsBean.prepayid
        request.packageValue = payChannelParamsBean.`package`
        request.nonceStr = payChannelParamsBean.noncestr
        request.timeStamp = payChannelParamsBean.timestamp
        request.sign = payChannelParamsBean.sign
        api.sendReq(request)
    }

    private fun callWxApplet(orderId: String) {
        val appId: String = Constant.WX_APP_ID // 填应用AppId
        val api = WXAPIFactory.createWXAPI(this, appId)

        val req = WXLaunchMiniProgram.Req()
        req.userName = Constant.WX_APPLET_ID // 填小程序原始id
        req.path = "/pages/logs/logs?orderid=${orderId}&amount=${money}" //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE // 可选打开 开发版，体验版和正式版
        api.sendReq(req)
    }

    override fun initView() {
        StatusBarUtils.setTranslucentStatus(this)
        val lp = binding.titleTop.ablToolbar.layoutParams as RelativeLayout.LayoutParams
        lp.topMargin = StatusBarUtil.getStatusBarHeight()
        binding.titleTop.ablToolbar.layoutParams = lp

        url = intent.getStringExtra(ARouterMap.URL).toString()
        initWebSetting()
        startListener()

        binding.dwWeb.addJavascriptObject(this@WebViewActivity, null)
        binding.dwWeb.loadUrl(url)
    }

    private fun startListener() {

        binding.titleTop.ivBack.setOnClickListener(this)
        binding.titleTop.tvRight.setOnClickListener(this)
    }

    private fun initWebSetting() {
        val jsonObject = JSONObject()
        val userInfo = UserInfoManager.instance().getUserInfo()
        jsonObject["token"] = userInfo?.token
        binding.dwWeb.settings.userAgentString += jsonObject.toJSONString()

        // 设置可以支持缩放
        binding.dwWeb.settings.setSupportZoom(true)
        // 设置出现缩放工具
        binding.dwWeb.settings.builtInZoomControls = true
        //扩大比例的缩放
        binding.dwWeb.settings.useWideViewPort = true
        //自适应屏幕
        binding.dwWeb.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        binding.dwWeb.settings.loadWithOverviewMode = true
        binding.dwWeb.webChromeClient = webChromeClient
        binding.dwWeb.setWebViewUrlChangeListener(webViewUrlChangeListener)

        try {
            if (Build.VERSION.SDK_INT >= 16) {
                val clazz: Class<*> = binding.dwWeb.settings.javaClass
                val method = clazz.getMethod(
                    "setAllowUniversalAccessFromFileURLs",
                    Boolean::class.javaPrimitiveType
                )
                method.invoke(binding.dwWeb.settings, true)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

    var webChromeClient: WebChromeClient = object : WebChromeClient() {

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            if (!TextUtils.isEmpty(title) && !view?.url?.contains(title.toString())!!) {
                binding.titleTop.tvTitle.text = title
            }
        }

        override fun onProgressChanged(p0: WebView?, progress: Int) {
            if (progress == 0 || progress == 100) {

                binding.pbWeb.gone()
            } else {
                binding.pbWeb.show()
                binding.pbWeb.progress = progress
            }
            super.onProgressChanged(p0, progress)

        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            mUploadMessageForAndroid5 = filePathCallback
            return true
        }

    }

    var webViewUrlChangeListener: DWebView.WebViewUrlChangeListener =
        object : DWebView.WebViewUrlChangeListener {

            override fun changedUrl(url: String) {

            }

            override fun onPageFinsh() {

            }

            override fun loadProgress(progress: Int) {

            }

            override fun onPageError() {
            }
        }

    override fun initData() {
        ObserverManger.getInstance(ObserverKey.WEXIN_PAY_RESULT).registerObserver(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressedSupport()
            }
            R.id.tv_right -> {
                when (binding.titleTop.tvRight.text) {
                    i18N(R.string.分享) -> {
                        if (shareObject == null) {
                            return
                        }
                        WxSharManager.instance().shareToGraphis(
                            this,
                            shareObject?.getString("title").toString(),
                            shareObject?.getString("url").toString(),
                            shareObject?.getString("desc").toString(),
                            object : WxSharManager.OnShareResultLinsener {
                                override fun onShareCancer(mType: SHARE_EMUN) {
                                }

                                override fun onShareError(mType: SHARE_EMUN) {
                                }

                                override fun onShareSucess(mType: SHARE_EMUN) {
                                }
                            })
                    }
                }
            }
        }
    }

    override fun onShareClickLinsener(mShareItemData: ShareItemData) {
        mShareItemData
    }


    override fun onGetClassTypeNam(): Any {
        return "网页"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CameraUtil.CameraRequestCode -> {
                    startCrop(CameraUtil.saveUri, 600f, 600f)
                }
                CameraUtil.AlbumRequestCode -> {
                    startCrop(data?.data!!, 600f, 600f)
                }
                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data!!)
                    tempFile = CameraUtil.choosePhoto(resultUri)
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(tempFile?.path, options)
                    options.inSampleSize = FileAccessor.calculateInSampleSize(
                        options,
                        800,
                        800
                    )  //800,800,转换后的宽和高，具体值会有些出入
                    options.inJustDecodeBounds = false
                    var bitmap = BitmapFactory.decodeFile(tempFile?.path, options)
                    bitmap = FileAccessor.compressImage(bitmap)
//
                    tempFile = FileAccessor.saveFile(bitmap, UUID.randomUUID().toString() + ".jpg")
                    handler?.complete("data:image/png;base64," + FileAccessor.fileToBase64(tempFile))
                    ToastUtils.showToash(i18N(R.string.正在上传))
                }
            }
        }
    }


    private fun startCrop(uri: Uri, w: Float, h: Float) {
        val fileDir = File(
            Environment.getExternalStorageDirectory().toString() + FileAccessor.XDJ_PATH,
            "/image"
        )
        if (!fileDir.exists()) {
            fileDir.mkdir()
        }
        //裁剪后保存到文件中
        val destinationUri =
            Uri.fromFile(File(FileAccessor.IMAGE_DIR, UUID.randomUUID().toString() + ".jpg"))
        val uCrop = UCrop.of(uri, destinationUri)
        val options = UCrop.Options()
        //设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL)
//        //设置toolbar颜色
        options.setToolbarColor(
            ContextCompat.getColor(
                BaseApplication.instance(),
                R.color.white_bg
            )
        )
        //设置状态栏颜色
        options.setStatusBarColor(
            ContextCompat.getColor(
                BaseApplication.instance(),
                R.color.white_bg
            )
        )
        //是否能调整裁剪框
        options.setFreeStyleCropEnabled(false)
        //设置是否显示裁剪网格
        options.setShowCropGrid(false)
//        //设置竖线的数量
//        options.setCropGridColumnCount(0)
//        //设置横线的数量
//        options.setCropGridRowCount(0)
        //裁剪比例
        options.withAspectRatio(w, h)
        //是否隐藏底部容器，默认显示
        options.setHideBottomControls(true)
        //圆形裁剪框
        options.setCircleDimmedLayer(false)
//        //设置是否显示裁剪边框(true为方形边框)
        options.setShowCropFrame(false)
        //设置最大缩放比例
//        options.setMaxScaleMultiplier(3f)
        uCrop.withOptions(options)
        uCrop.start(this@WebViewActivity)
    }

    override fun onBackPressedSupport() {
        if (binding.dwWeb.canGoBack()) {
            binding.dwWeb.goBack()
            runOnUiThread {
                binding.titleTop.ablToolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white_bg
                    )
                )
                binding.titleTop.toolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white_bg
                    )
                )
                binding.viewMask.show()
            }
        } else {
            finish()
        }
    }

    override fun update(obj: Any?) {
        if (obj is BaseResp) {
            var result = ""
            when (obj.errCode) {
                BaseResp.ErrCode.ERR_OK -> {
                    result = "成功"
                    ARouter.getInstance().build(ARouterMap.PAY_RESULT)
                        .withInt(ARouterMap.PAYMENT_STATUS, 2).withInt(ARouterMap.PAY_FROM, 1)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                    onBackPressedSupport()
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ObserverManger.getInstance(ObserverKey.WEXIN_PAY_RESULT).removeObserver(this)
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityWebviewBinding.inflate(layoutInflater)
    }
}