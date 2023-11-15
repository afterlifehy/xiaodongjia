package com.wbb.xiaodongjia.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import cn.jiguang.verifysdk.api.PreLoginListener
import com.snail.antifake.jni.EmulatorDetectUtil
import com.wbb.base.BaseApplication
import com.wbb.base.dialog.DialogHelp
import com.wbb.xiaodongjia.dialog.PrivacyAgreementDialog
import com.wbb.base.ext.i18N
import com.wbb.base.help.AppExitManager
import com.wbb.base.pf.PreferencesKeys
import com.wbb.base.pf.StoreFactory
import com.wbb.base.util.*
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.bean.LocalAddressInfo
import com.wbb.xiaodongjia.mvvm.viewmodel.UserViewModel
import com.wbb.xiaodongjia.ui.activity.attention.MyAttentionActivity
import com.wbb.xiaodongjia.ui.activity.login.LoginActivity
import com.wbb.xiaodongjia.ui.activity.login.OneClickLoginActivity
import com.wbb.xiaodongjia.ui.activity.video.VideoPlayActivity
import com.wbb.xiaodongjia.utils.LocalAddressManager
import com.wbb.xiaodongjia.utils.OneClickLoginManager
import kotlinx.android.synthetic.main.activity_splash_layout.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

/**
 * Created by hy on 2021/1/19.
 */
class SplashActivity : BaseDataActivityKt<UserViewModel>(), View.OnClickListener,
    PreLoginListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarUtils.setTranslucentStatus(this)
        StatusBarUtils.setNavigationBarColor(this, R.color.color_FFBA00)



        checkReadProtocol()
        val channel = ChannelUtils.getChannelName(this)
    }

    /**
     * 检查是否阅读协议
     */
    fun checkReadProtocol() {
        runBlocking {
            val isRead = StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                .getBoolean(
                    PreferencesKeys.IS_READ
                )
            if (isRead) {
                startInit()
            } else {
                val mPrivacyAgreementDialog = PrivacyAgreementDialog(this@SplashActivity, object :
                    PrivacyAgreementDialog.OnButtonClickLinsener {
                    override fun onLeftClickLinsener(msg: String) {
                        AppExitManager.instance().exitApp()
                    }

                    override fun onRightClickLinsener(msg: String) {
                        runBlocking {
                            StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                                .putBoolean(PreferencesKeys.IS_READ, true)
                        }
                        startInit()
                    }
                })
                mPrivacyAgreementDialog.show()
            }
        }

    }

    private fun startInit() {
        val saveInfo = LocalAddressManager.instance().getDefaultCityInfo()
        if (saveInfo != null) {
            initLogin()
        } else {//没有保存默认配置
            getInitConfig()
        }
    }

    override fun providerVMClass(): Class<UserViewModel>? {
        return UserViewModel::class.java
    }

    fun initLogin() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (!checkEmulator()) {
                goLogin()
            }
        } else {
            goLogin()
        }
    }

    fun goLogin() {
        if (UserInfoManager.instance().isLogin()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            ///去检查是否也一键登录
            OneClickLoginManager.preLogin(this, this)

        }
    }

    fun checkEmulator(): Boolean {
        if (EmulatorDetectUtil.isEmulator()) {
            DialogHelp.Builder.setTitle(i18N(R.string.风险提示))
                .setContentMsg(i18N(R.string.检测到你可能在模拟器上运行应用即将关闭)).setLeftMsg(i18N(R.string.取消))
                .setRightMsg(i18N(R.string.确定))
                .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                    override fun onLeftClickLinsener(msg: String) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            AppExitManager.instance().exitApp()
                        }, 2000)
                    }

                    override fun onRightClickLinsener(msg: String) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            AppExitManager.instance().exitApp()
                        }, 1000)
                    }

                }).setCancelable(false).build(this@SplashActivity).showDailog()
            return true
        }
        return false
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mConfigData.observe(this@SplashActivity, {
                it?.let {
                    LocalAddressManager.instance()
                        .onSaveDefaultCityInfo(LocalAddressInfo(it.cityName, it.cityId))
                    initLogin()
                }
            })
            mException.safeObserve(this@SplashActivity, {
                // ToastUtils.showErrorToast(it.message)
            })
            errMsg.safeObserve(this@SplashActivity, {
                ToastUtils.showErrorToast(it.msg)
            })
        }
    }

    /**
     * 获取需要加载配置
     */
    fun getInitConfig() {
        mViewModel.getConfigSet()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_home -> {
                startActivity(Intent(this, MainActivity::class.java))

            }
            R.id.tv_login -> {
                startActivity(Intent(this, LoginActivity::class.java))

            }
            R.id.tv_toast -> {
                startActivity(Intent(this, VideoPlayActivity::class.java))
            }
            R.id.tv_zm -> {
//
//                GlobalScope.launch(Dispatchers.Main) {
//                    count2().flowOn(Dispatchers.Unconfined).map {
//                        Log.d("Coroutine", "map on ${Thread.currentThread().name}")
//                        if (it > 15) {
//                            throw NumberFormatException()
//                        }
//                        "I am2 $it"
//
//                    }.flowOn(Dispatchers.IO).catch {
//                        Log.d("Coroutine", "catch on ${Thread.currentThread().name}")
//                        emit("error")
//                    }.collect {
//                        Log.d("Coroutine", "collect on ${Thread.currentThread().name}")
//                        Log.i("keey", "it:${it}")
//                        tv_zm.text = it
//                    }
//                }
                startActivity(Intent(this, MyAttentionActivity::class.java))
            }


        }
    }

    private fun count2(): Flow<Int> = flow {
        var x = 0
        while (true) {
            if (x > 20) {
                break
            }
            delay(500)
            emit(x)
            x = x.plus(1)
        }
    }

    override fun onReloadData() {
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.activity_splash_layout
    }

    override fun initView() {
        tv_home.setOnClickListener(this)
        tv_login.setOnClickListener(this)
        tv_toast.setOnClickListener(this)
        tv_zm.setOnClickListener(this)

    }

    override fun initData() {
    }

    override fun onGetClassTypeNam(): Any {
        return "欢迎页面"
    }

    override fun onResult(p0: Int, p1: String?) {
        if (p0 == 7000) {//支持一键登录
            OneClickLoginActivity.startIntent(this@SplashActivity)
            finish()

        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}