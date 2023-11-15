package com.wbb.xiaodongjia.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.aries.ui.util.StatusBarUtil
import com.wbb.base.viewbase.BaseDataActivityKt
import com.uuzuche.lib_zxing.activity.CaptureFragment
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.ext.i18N
import com.wbb.base.util.*
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.mvvm.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_mycapture.*

/**
 * Created by hy on 2021/2/1.
 */
@Route(path = ARouterMap.MYCAPTURE)
class MyCaptureActivity : BaseDataActivityKt<HomeViewModel>(), View.OnClickListener, CodeUtils.AnalyzeCallback {
    var captureFragment: CaptureFragment? = null
    var fragmentManager: FragmentManager? = null
    var fragmentTransaction: FragmentTransaction? = null
    var isOpen = false
    var url = ""
    var scanResult = ""

    override fun onReloadData() {

    }

    override fun providerVMClass(): Class<HomeViewModel>? {
        return HomeViewModel::class.java
    }

    override fun onGetClassTypeNam(): Any {
        return "扫码"
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.activity_mycapture
    }

    override fun initView() {
        search_toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        StatusBarUtils.setTranslucentStatus(this)
        val lp = search_toolbar.layoutParams as RelativeLayout.LayoutParams
        lp.topMargin = StatusBarUtil.getStatusBarHeight()
        search_toolbar.layoutParams = lp

        fragmentManager = supportFragmentManager
        if (captureFragment == null) {
            captureFragment = CaptureFragment()
        }
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.layout_mycapture)
        captureFragment?.analyzeCallback = this
        showFragment(fragmentManager!!, fragmentTransaction, captureFragment, R.id.fl_captureContent, "captureFragment")

        initListener()
    }

    private fun initListener() {
        rfl_close.setOnClickListener(this)
        iv_flashLamp.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rfl_close -> {
                onBackPressedSupport()
            }
            R.id.iv_flashLamp -> {
                if (isOpen) {
                    iv_flashLamp.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_flashlamp_off))
                    CodeUtils.isLightEnable(false)
                    isOpen = false
                } else {
                    iv_flashLamp.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_flashlamp_on))
                    CodeUtils.isLightEnable(true)
                    isOpen = true
                }
            }
        }
    }

    override fun onAnalyzeSuccess(mBitmap: Bitmap?, result: String?) {
        try {
            if (result?.startsWith(Constant.getH5ServerPath())!! && result.contains("/pages/user/others/buy")) {
                url = result.substring(7, result.length)
                scanResult = result
                merchantStoreDetail(url)
            } else {
                ARouter.getInstance().build(ARouterMap.TXT_SHOW).withString(ARouterMap.MESSAGE, result).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        } catch (e: Exception) {
            ARouter.getInstance().build(ARouterMap.TXT_SHOW).withString(ARouterMap.MESSAGE, result).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
        }
        result?.let { OnBuriedPointManager.get().getOnBuriedPointManager()?.ScanCode(it, 1, "") }
        finish()
    }


    override fun onAnalyzeFailed() {
        ToastUtils.showErrorToast(i18N(R.string.扫码失败))
        OnBuriedPointManager.get().getOnBuriedPointManager()?.ScanCode("", 0, "识别失败")
    }

    fun merchantStoreDetail(url: String) {
        var path = url.split("?")[0]
        val par = StringUtil.getUrlParameter(url)
        if (par != null) {
            for (key in par.keys) {
                if (TextUtils.equals(ARouterMap.MERCHANTID, key)) {
                    val param = HashMap<String, String>()
                    param["id"] = par.getValue(key)
                    mViewModel.merchantStoreDetail(param)
                }
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mMerchantStoreDetailLiveData.observe(this@MyCaptureActivity, Observer {
                when (it.merchantStatus) {
                    "UP" -> {
                        var path = scanResult.split("?")[0]
                        val par = StringUtil.getUrlParameter(url)
                        var build: Postcard? = null
                        when (path) {
                            Constant.getH5ServerPath() + "pages/user/others/buy" -> {
                                build = ARouter.getInstance().build(ARouterMap.PAYBILL).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        }
                        if (par != null) {
                            for (key in par.keys) {
                                build?.withString(key, par.getValue(key))
                            }
                        }
                        build?.navigation()
                    }
                    "DOWN" -> {
                        ToastUtils.showToash(i18N(R.string.店铺已下架))
                        finish()
                    }
                    "TO_STAY_ON" -> {
                        ToastUtils.showToash(i18N(R.string.店铺待上架))
                        finish()
                    }
                }
            })
            mException.observe(this@MyCaptureActivity, Observer {
                it
            })
        }
    }
}