package com.wbb.xiaodongjia.ui.activity

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.start.StartUpKey
import com.wbb.base.util.StatusBarUtils
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.base.VbBaseActivity
import com.wbb.xiaodongjia.databinding.ActiivtyNewWelcomeLayoutBinding
import com.wbb.xiaodongjia.startup.ApplicationAnchorTaskCreator
import com.xj.anchortask.library.AnchorProject
import com.xj.anchortask.library.OnProjectExecuteListener
import com.xj.anchortask.library.log.LogUtils

/**
 * Created by zj on 2021/2/27.
 */
class NewWelcomeActivity : VbBaseActivity<BaseViewModel, ActiivtyNewWelcomeLayoutBinding>(),
    OnProjectExecuteListener {
    var project: AnchorProject? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setTranslucentStatus(this)
        StatusBarUtils.setNavigationBarColor(this, R.color.color_FFBA00)
        // setContentView(R.layout.actiivty_new_welcome_layout)
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

    override fun onGetClassTypeNam(): Any {
        return "测试"
    }

    override fun onProjectFinish() {
//        startActivity(Intent(this@NewWelcomeActivity, SplashActivity::class.java))
//        finish()
    }

    override fun onProjectStart() {
    }

    override fun onTaskFinish(taskName: String) {

    }

    override fun onReloadData() {
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false


    override fun initView() {
    }


    override fun initData() {
    }

    override fun getVbBindingView(): ViewBinding {
        return ActiivtyNewWelcomeLayoutBinding.inflate(layoutInflater)
    }
}