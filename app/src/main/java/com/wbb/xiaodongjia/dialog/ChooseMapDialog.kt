package com.wbb.xiaodongjia.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.wbb.base.dialog.BaseLibDialog
import com.wbb.base.util.AppUtil
import com.wbb.xiaodongjia.R
import kotlinx.android.synthetic.main.dialog_choose_map.*

/**
 * Created by hy on 2021/3/5.
 */
class ChooseMapDialog(context: Context, themeResId: Int, val lat: String, val lng: String, val name: String) : BaseLibDialog(context, themeResId), View.OnClickListener {
    val BAIDU_MAP = "com.baidu.BaiduMap"
    val GAODE_MAP = "com.autonavi.minimap"
    val TENCENT_MAP = "com.tencent.map"

    init {
        initView()
    }

    private fun initView() {
        rtv_baidu.setOnClickListener(this)
        rtv_gaode.setOnClickListener(this)
        rtv_tencent.setOnClickListener(this)
        tv_cancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rtv_baidu -> {
                if (AppUtil.checkAppInstalled(BAIDU_MAP)) {
                    val uri = Uri.parse("baidumap://map/direction?destination=latlng:${lat},${lng}|name:${name}&mode=driving")
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                } else {
                    AppUtil.goToMarket(BAIDU_MAP)
                }
                dismiss()
            }
            R.id.rtv_gaode -> {
                if (AppUtil.checkAppInstalled(GAODE_MAP)) {
                    val uri = Uri.parse("amapuri://route/plan/?dlat=${lat}&dlon=${lng}&dname=${name}&dev=0&t=0")
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                } else {
                    AppUtil.goToMarket(GAODE_MAP)
                }
                dismiss()
            }
            R.id.rtv_tencent -> {
                if (AppUtil.checkAppInstalled(TENCENT_MAP)) {
                    val uri = Uri.parse("qqmap://map/routeplan?type=drive&to=${name}&tocoord=${lat},${lng}&referer=小东家")
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                } else {
                    AppUtil.goToMarket(TENCENT_MAP)
                }
                dismiss()
            }
            R.id.tv_cancel -> {
                dismiss()
            }
        }
    }

    override fun getHeight(): Float {
        return WindowManager.LayoutParams.WRAP_CONTENT.toFloat()
    }

    override fun getLayoutResID(): Int {
        return R.layout.dialog_choose_map
    }

    override fun getCanceledOnTouchOutside(): Boolean {
        return true
    }

    override fun getHideInput(): Boolean {
        return true
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }

    override fun getWidth(): Float {
        return WindowManager.LayoutParams.MATCH_PARENT.toFloat()
    }

}
