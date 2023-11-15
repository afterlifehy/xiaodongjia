package com.wbb.xiaodongjia.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.wbb.xiaodongjia.startup.ApplicationAnchorTaskCreator
import com.wbb.base.start.BaseStartUpManager
import com.wbb.base.start.StartUpKey
import com.wbb.base.util.RxTimerUtil
import com.wbb.base.util.StatusBarUtils
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.base.AppApplication
import com.wbb.xiaodongjia.base.AppStartUpManager
import com.xj.anchortask.library.AnchorProject
import com.xj.anchortask.library.OnProjectExecuteListener
import com.xj.anchortask.library.log.LogUtils

/**
 * Created by zj on 2021/2/27.
 */
class WelcomeActivity : Activity(), OnProjectExecuteListener {
    var project: AnchorProject? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setTranslucentStatus(this)
        StatusBarUtils.setNavigationBarColor(this, R.color.color_FFBA00)
        setContentView(R.layout.actiivty_welcome_layout)


        project = AnchorProject.Builder().setContext(this).setLogLevel(LogUtils.LogLevel.DEBUG)
            .setAnchorTaskCreator(ApplicationAnchorTaskCreator())
            .addTask(StartUpKey.MUST_BE_NITIALIZED).addTask(StartUpKey.MUST_BE_NOE)
            .afterTask(StartUpKey.MUST_BE_NITIALIZED).build()
        project?.addListener(this)
    }

    override fun onResume() {
        super.onResume()
        project?.start()
    }

    override fun onProjectFinish() {
        startActivity(Intent(this@WelcomeActivity, SplashActivity::class.java))
        finish()
    }

    override fun onProjectStart() {
    }

    override fun onTaskFinish(taskName: String) {

    }
}