package com.wbb.base.help

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.text.TextUtils
import androidx.annotation.Nullable
import com.wbb.base.observer.ObserverKey
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.chouyou.base.base.ShareItemData
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.wbb.base.BaseApplication
import com.wbb.base.R
import com.wbb.base.dialog.ShareDialog
import com.wbb.base.emuns.SHARE_EMUN
import com.wbb.base.emuns.WEIXIN_SHAR_TYPE
import com.wbb.base.observer.ObserverManger
import com.wbb.base.observer.OnObserver
import com.wbb.base.util.CopyUtils
import com.wbb.base.util.ToastUtils
import java.io.ByteArrayOutputStream


/**
 * Created by zj on 2021/3/3.
 */
class WxSharManager private constructor() : BroadcastReceiver(), ShareDialog.OnShareClickLisener,
    OnObserver {
    private val THUMB_SIZE = 150

    // IWXAPI 是第三方app和微信通信的openApi接口
    private var api: IWXAPI? = null
    private var mOnShareResultLinsener: OnShareResultLinsener? = null
    private var mShaerInfo: ShareInfo? = null
    var isBitmap = false
    var bitmapString = ""
    var bp: Bitmap? = null

    //当前分享类型
    var mCurrentSharType = SHARE_EMUN.WEIXIN

    companion object {
        const val APP_ID = "wx02dcd66ba6f2b82d"
        var mWxSharManager: WxSharManager? = null
        fun instance(): WxSharManager {
            if (mWxSharManager == null) {
                mWxSharManager = WxSharManager()

            }
            return mWxSharManager!!
        }
    }

    /**
     * 获取微信api
     */
    fun getWxApi(): IWXAPI? {
        return api
    }

    fun showShareDialog(
        context: Context?,
        mShaerInfo: ShareInfo? = null,
        mOnShareResultLinsener: OnShareResultLinsener?
    ) {
        if (context == null || mShaerInfo == null) {
            return
        }
        this.mShaerInfo = mShaerInfo
        this.mOnShareResultLinsener = mOnShareResultLinsener
        val mShareDialog = ShareDialog(context)
        mShareDialog.setOnShareClickLinener(this)
        mShareDialog.show()
    }

    /**
     * 分享图文到微信
     */
    fun shareToGraphis(
        context: Context?,
        title: String,
        shareUrl: String,
        description: String,
        mOnShareResultLinsener: OnShareResultLinsener?
    ) {
        val mShareInfo =
            ShareInfo(
                WEIXIN_SHAR_TYPE.GRAPHIC,
                title = title,
                graphis = ShareGraphic(
                    shareUrl,
                    "",
                    description,
                )
            )
        showShareDialog(context, mShareInfo, object :
            OnShareResultLinsener {
            override fun onShareCancer(mType: SHARE_EMUN) {
                mOnShareResultLinsener?.onShareCancer(mType)
            }

            override fun onShareError(mType: SHARE_EMUN) {
                mOnShareResultLinsener?.onShareError(mType)
            }

            override fun onShareSucess(mType: SHARE_EMUN) {
                mOnShareResultLinsener?.onShareSucess(mType)
            }
        })

    }

    /**
     * 分享bitmap到微信
     */
    fun shareBitmapToWx(
        context: Context?,
        bp: Bitmap,
        bitmapString: String,
        mOnShareResultLinsener: OnShareResultLinsener?
    ) {
        isBitmap = true
        showBitmapShareDialog(context, bp, bitmapString, object : OnShareResultLinsener {
            override fun onShareCancer(mType: SHARE_EMUN) {
                mOnShareResultLinsener?.onShareCancer(mType)
            }

            override fun onShareError(mType: SHARE_EMUN) {
                mOnShareResultLinsener?.onShareError(mType)
            }

            override fun onShareSucess(mType: SHARE_EMUN) {
                mOnShareResultLinsener?.onShareSucess(mType)
            }
        })
    }

    fun showBitmapShareDialog(
        context: Context?,
        bp: Bitmap,
        bitmapString: String,
        mOnShareResultLinsener: OnShareResultLinsener?
    ) {
        if (context == null || bp == null) {
            return
        }
        this.bitmapString = bitmapString
        this.bp = bp
        this.mOnShareResultLinsener = mOnShareResultLinsener
        val mShareDialog = ShareDialog(context)
        mShareDialog.setOnShareClickLinener(this)
        mShareDialog.show()
    }

    /**
     * 初始化
     */
    fun initWx(application: Application) {
        ObserverManger.getInstance(ObserverKey.WEXIN_SHARE_RESULT).registerObserver(this)
        api = WXAPIFactory.createWXAPI(application, APP_ID, true)
        // 将应用的appId注册到微信
        api?.registerApp(APP_ID)
        application.registerReceiver(this, IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP))
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        // 将该app注册到微信
        api?.registerApp(APP_ID)
    }

    /**
     * 选择分享内容
     */
    private fun chooseSharingType(mType: Int) {
        val isInstalled: Boolean = api!!.isWXAppInstalled()
        if (!isInstalled) {
            ToastUtils.showErrorToast("您还未安装微信请先安装")
        }
        if (!api!!.isWXAppInstalled()) {
            ToastUtils.showErrorToast("分享失败")
            return
        }
        if (mShaerInfo == null) {
            return
        }
        val mInfo = mShaerInfo!!
        when (mInfo.mType) {
            WEIXIN_SHAR_TYPE.TEXT -> {//文本
                shareTextToWeixin(mType)
            }
            WEIXIN_SHAR_TYPE.GRAPHIC -> {
                checkBigmap(mType, mInfo.graphis?.imgPaht, mInfo.graphis?.bmp)
            }
            WEIXIN_SHAR_TYPE.IMAGE -> {//图片
                checkBigmap(mType, mInfo.img?.imgPaht, mInfo.img?.bmp)
            }

        }
    }

    /**
     *
     */
    private fun dealWith(mType: Int, bitmap: Bitmap) {
        val thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true)
        bitmap.recycle()
        mShaerInfo?.let {
            if (it.mType == WEIXIN_SHAR_TYPE.IMAGE) {
                shareImgToWeixin(mType, thumbBmp)
            } else if (it.mType == WEIXIN_SHAR_TYPE.GRAPHIC) {
                shareGrarohicToWeixin(mType, thumbBmp)
            }
        }

    }

    /**
     * 分享图片到微信 这个有问题，不能使用，需要调整
     */
    fun shareImgToWeixin(mType: Int, bitmap: Bitmap) {
        mShaerInfo?.let {
            val imgObj = WXImageObject(bitmap)
            val msg = WXMediaMessage()
            msg.mediaObject = imgObj
            msg.description = it.img?.description
            val thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true)
            bitmap.recycle()
            msg.thumbData = bmpToByteArray(bitmap, true)

            val req = SendMessageToWX.Req()
            req.transaction = System.currentTimeMillis().toString()
            req.message = msg
            req.scene = mType
            api?.sendReq(req)
        }
    }

    /**
     * 分享图片到微信
     */
    fun shareBitmapToWeixin(mType: Int, bitmap: Bitmap) {
        val isInstalled: Boolean = api!!.isWXAppInstalled()
        if (!isInstalled) {
            ToastUtils.showErrorToast("您还未安装微信请先安装")
        }
        if (!api!!.isWXAppInstalled()) {
            ToastUtils.showErrorToast("分享失败")
            return
        }
        val imgObj = WXImageObject(bitmap)
        val msg = WXMediaMessage()
        msg.mediaObject = imgObj
        val thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true)
        bitmap.recycle()
        msg.thumbData = bmpToByteArray(thumbBmp, true)

        val req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg
        req.scene = mType
        api?.sendReq(req)
    }

    /**
     * 分享图文
     */
    private fun shareGrarohicToWeixin(mType: Int, bitmap: Bitmap) {
        mShaerInfo?.let {
            val webpageObject = WXWebpageObject(it.graphis?.description)
            webpageObject.webpageUrl = it.graphis?.url

            // 用WXTextObject对象初始化一个WXMediaMessage对象
            val msg = WXMediaMessage()
            msg.mediaObject = webpageObject
            msg.thumbData = bmpToByteArray(bitmap, true)
            msg.description = it.graphis?.description
            msg.title = it.title
            // 构造一个Req
            val req = SendMessageToWX.Req()
            req.transaction = System.currentTimeMillis().toString()
            req.message = msg
            req.scene = mType
            api?.sendReq(req)
        }
    }


    private fun checkBigmap(mType: Int, imgPaht: String?, bitmap: Bitmap?) {
        if (bitmap != null || !TextUtils.isEmpty(imgPaht)) {
            if (bitmap != null) {
                dealWith(mType, bitmap = bitmap)
            } else {
                Glide.with(BaseApplication.instance()).asBitmap().load(imgPaht)
                    .listener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(
                            @Nullable e: GlideException?,
                            model: Any,
                            target: Target<Bitmap?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            //图片没有下载成,也去分享,用默认图片
                            getDefalutImg(mType)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any,
                            target: Target<Bitmap?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    }).into(object : SimpleTarget<Bitmap?>() {

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            dealWith(mType, bitmap = resource)

                        }
                    })

            }
        } else {
            getDefalutImg(mType)
        }
    }

    private fun getDefalutImg(mType: Int) {
        val bitmap = BitmapFactory.decodeResource(
            BaseApplication.instance().getResources(),
            R.mipmap.ic_app_logo
        )
        dealWith(mType, bitmap = bitmap)
    }

    /**
     * 分享文本到微信或朋友圈
     */
    private fun shareTextToWeixin(mType: Int) {
        //初始化一个 WXTextObject 对象，填写分享的文本内容
        val textObj = WXTextObject()
        textObj.text = mShaerInfo!!.text?.context
        //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
        val msg = WXMediaMessage()
        msg.mediaObject = textObj
        msg.description = mShaerInfo!!.title
        // msg.thumbData=
        val req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString() //transaction字段用与唯一标示一个请求
        req.message = msg
        req.scene = mType
        //调用api接口，发送数据到微信
        api!!.sendReq(req)
    }

    override fun onShareClickLinsener(mShareItemData: ShareItemData) {

        when (mShareItemData.type) {
            SHARE_EMUN.WEIXIN.id -> {//微信好友
                mCurrentSharType = SHARE_EMUN.WEIXIN
                if (isBitmap) {
                    bp?.let { shareBitmapToWeixin(0, it) }
                } else {
                    chooseSharingType(SendMessageToWX.Req.WXSceneSession)
                }
            }
            SHARE_EMUN.CIRCLE_OF_FRIENDS.id -> {//朋友圈
                mCurrentSharType = SHARE_EMUN.CIRCLE_OF_FRIENDS
                if (isBitmap) {
                    bp?.let { shareBitmapToWeixin(1, it) }
                } else {
                    chooseSharingType(SendMessageToWX.Req.WXSceneTimeline)
                }
            }
            SHARE_EMUN.COPY_URL.id -> {
                mCurrentSharType = SHARE_EMUN.COPY_URL
                if (isBitmap) {
                    CopyUtils.strToCopy(BaseApplication.instance(), bitmapString)
                    ToastUtils.showSucessToast("已复制到粘贴板")
                    mOnShareResultLinsener?.onShareSucess(mCurrentSharType)
                } else {
                    mShaerInfo?.let {
                        CopyUtils.strToCopy(BaseApplication.instance(), it.graphis!!.url!!)
                        ToastUtils.showSucessToast("已复制到粘贴板")
                        mOnShareResultLinsener?.onShareSucess(mCurrentSharType)
                    }
                }
            }
        }

    }

    override fun update(obj: Any?) {
        if (obj is BaseResp) {
            var result = ""
            when (obj.errCode) {
                BaseResp.ErrCode.ERR_OK -> {
                    result = "成功"
                    ToastUtils.showSucessToast(result)
                    mOnShareResultLinsener?.onShareSucess(mCurrentSharType)
                }
                BaseResp.ErrCode.ERR_COMM -> {
                    result = "错误"
                    ToastUtils.showErrorToast(result)
                    mOnShareResultLinsener?.onShareError(mCurrentSharType)
                }
                BaseResp.ErrCode.ERR_USER_CANCEL -> {
                    result = "取消"
                    ToastUtils.showErrorToast(result)
                    mOnShareResultLinsener?.onShareCancer(mCurrentSharType)
                }
                BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                    result = "被拒"
                    ToastUtils.showErrorToast(result)
                    mOnShareResultLinsener?.onShareError(mCurrentSharType)
                }
                else -> {
                    result = "未知"
                    ToastUtils.showErrorToast(result)
                    mOnShareResultLinsener?.onShareError(mCurrentSharType)
                }
            }

        }

    }

    private fun bmpToByteArray(bmp: Bitmap, needRecycle: Boolean): ByteArray? {
        val output = ByteArrayOutputStream()
        bmp.compress(CompressFormat.PNG, 75, output)
        if (needRecycle) {
            bmp.recycle()
        }
        val result = output.toByteArray()
        try {
            output.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    interface OnShareResultLinsener {
        fun onShareSucess(mType: SHARE_EMUN)
        fun onShareError(mType: SHARE_EMUN)
        fun onShareCancer(mType: SHARE_EMUN)
    }

    data class ShareInfo(
        var mType: WEIXIN_SHAR_TYPE,
        var title: String = "",
        var text: ShareText? = null,
        var img: ShareImg? = null,
        var graphis: ShareGraphic? = null
    )


    data class ShareText(var context: String = "")
    data class ShareImg(
        var shareImgPaht: String = "",
        var imgPaht: String = "",
        var description: String = "",
        var bmp: Bitmap? = null
    )

    data class ShareGraphic(
        var url: String? = "",
        var imgPaht: String = "",
        var description: String = "",
        var bmp: Bitmap? = null

    )
}