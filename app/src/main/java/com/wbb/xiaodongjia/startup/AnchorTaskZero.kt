package com.wbb.xiaodongjia.startup

import android.util.Log
import com.wbb.base.BaseApplication
import com.wbb.base.start.BaseStartUpManager
import com.wbb.base.start.StartUpKey
import com.wbb.xiaodongjia.base.AppApplication
import com.wbb.xiaodongjia.base.AppStartUpManager
import com.xj.anchortask.library.AnchorTask

/**
 * Created by zj on 2021/3/5.
 */
class AnchorTaskZero : AnchorTask(StartUpKey.MUST_BE_NITIALIZED) {
    override fun isRunOnMainThread(): Boolean {
        return false
    }

    override fun run() {
        BaseStartUpManager.instance().applicationInit(AppApplication.instance())
        AppStartUpManager.instance().applicationInit(AppApplication.instance())
    }
}