package com.wbb.base.start

import android.app.Application

/**
 * Created by zj on 2021/2/27.
 */
abstract class AppInitManager {
    //需要在application里面初始化的
    abstract fun applicationInit(application: Application)

    /**
     * 可以延时加载的
     */
    abstract fun delayInit(application: Application)
}