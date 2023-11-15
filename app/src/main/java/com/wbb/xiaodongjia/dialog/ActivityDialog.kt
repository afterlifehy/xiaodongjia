package com.wbb.xiaodongjia.dialog

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import com.wbb.base.util.StatusBarUtils
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.bean.WaterDropletsInfo
import com.wbb.xiaodongjia.wiget.OnActiivtyClickLinsener
import com.wbb.xiaodongjia.wiget.RandomLayout

/**
 * Created by zj on 2021/1/26.
 */
class ActivityDialog(context: Context, mOnActiivtyClickLinsener: OnActiivtyClickLinsener? = null) :
    Dialog(context, R.style.DaoxilaDialog_Alert), OnActiivtyClickLinsener {
    private var rl_layout: RandomLayout? = null

    private var mOnActiivtyClickLinsener: OnActiivtyClickLinsener? = null

    init {
        this.mOnActiivtyClickLinsener = mOnActiivtyClickLinsener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_lactivity_layout)
        init()
        findBView()
    }

    fun findBView() {
        rl_layout = findViewById(R.id.rl_layout)
        val list = ArrayList<WaterDropletsInfo>()
        list.add(WaterDropletsInfo(""))
        list.add(WaterDropletsInfo(""))
        list.add(WaterDropletsInfo(""))
        list.add(WaterDropletsInfo(""))
        rl_layout?.addRandomView(list, this)
    }

    fun init() {
        //        //宽度全屏显示
        val layoutParams = window?.getAttributes()
        layoutParams?.width = ActionBar.LayoutParams.MATCH_PARENT
        layoutParams?.height = ActionBar.LayoutParams.MATCH_PARENT
        layoutParams?.gravity = Gravity.CENTER
        window?.setAttributes(layoutParams)
        StatusBarUtils.setImmersiveStatus(window, false)
    }

    override fun onActivityClickLinsener(mWaterDropletsInfo: WaterDropletsInfo) {
        mOnActiivtyClickLinsener?.onActivityClickLinsener(mWaterDropletsInfo)
        dismiss()
    }


}