package com.wbb.xiaodongjia.startup

import com.wbb.base.start.BaseStartUpManager
import com.wbb.base.start.StartUpKey
import com.wbb.xiaodongjia.base.AppApplication
import com.wbb.xiaodongjia.base.AppStartUpManager
import com.xj.anchortask.library.AnchorTask

/**
 * Created by zj on 2021/3/5.
 */
class AnchorTaskOne : AnchorTask(StartUpKey.MUST_BE_NOE) {
    override fun isRunOnMainThread(): Boolean {
        return false
    }

    override fun run() {
        BaseStartUpManager.instance().delayInit(AppApplication.instance())
        AppStartUpManager.instance().delayInit(AppApplication.instance())
    }
}