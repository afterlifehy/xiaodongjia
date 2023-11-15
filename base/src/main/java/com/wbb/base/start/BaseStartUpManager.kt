package com.wbb.base.start

import android.app.Application
import android.os.Environment
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.wbb.base.BaseApplication
import com.wbb.base.BuildConfig
import com.wbb.base.dokit.EnvSwitchKit
import com.wbb.base.help.WxSharManager
import com.wbb.base.util.FileAccessor
import java.io.File
import java.util.*

/**
 * Created by zj on 2021/2/27.
 */
class BaseStartUpManager private constructor() : AppInitManager() {
    var api: IWXAPI? = null
    var req: PayReq? = null

    companion object {
        var mStartUpManager: BaseStartUpManager? = null
        fun instance(): BaseStartUpManager {
            if (mStartUpManager == null) {
                mStartUpManager = BaseStartUpManager()
            }
            return mStartUpManager!!
        }

    }

    /**
     * 初始化路由
     */
    private fun initARounter(context: Application) {
        if (BuildConfig.ISDEBUGAROUTER) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(context)
    }

    private fun initDoKit(context: Application) {
        val kits: MutableList<AbstractKit> = ArrayList()
        kits.add(EnvSwitchKit())
        DoraemonKit.install(context, kits, "7df497771dee4aabc3468440c967ce07")
    }

    private fun regToWx(context: Application) {
        api = WXAPIFactory.createWXAPI(context, null)
        api?.registerApp("wx02dcd66ba6f2b82d")

//        req = PayReq()
//        req.appId = wXAdvanceChargeBean.getResult().getAppid();
//        req.partnerId = wXAdvanceChargeBean.getResult().getMch_id();
//        req.prepayId = wXAdvanceChargeBean.getResult().getPrepay_id();
//        req.packageValue = "Sign=WXPay";
//        req.nonceStr = wXAdvanceChargeBean.getResult().getNonce_str();
//        req.timeStamp = wXAdvanceChargeBean.getResult().getTimeStamp();
//        req.sign = wXAdvanceChargeBean.getResult().getSign();
//        msgApi.sendReq(req);
    }

    override fun applicationInit(application: Application) {
        initARounter(application)

    }

    override fun delayInit(application: Application) {
        WxSharManager.instance().initWx(application)
        //只有测试才开启
        BaseApplication.baseApplication.getOnAppBaseProxyLinsener()?.let {
            if (it.onIsDebug()) {
                initDoKit(application)
            }
        }

        regToWx(application)
        val appDir = File(Environment.getExternalStorageDirectory(), FileAccessor.XDJ_PATH)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
    }


}