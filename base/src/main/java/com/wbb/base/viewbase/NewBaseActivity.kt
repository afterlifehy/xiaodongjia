package com.wbb.base.viewbase


import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.R
import com.wbb.base.bean.NetWorkRequestData
import com.wbb.base.dialog.IOSLoadingDialog
import com.wbb.base.event.BaseEvent
import com.wbb.base.mvvm.base.NetWorkRequestLinsener
import com.wbb.base.mvvm.base.OnNetWorkCallLinsener
import com.wbb.base.network.NetWorkMonitorManager
import com.wbb.base.network.NetWorkState
import com.wbb.base.network.ViewNetWorkStateManager
import com.wbb.base.network.OnNetWorkViewShowLinsener
import com.wbb.base.util.ScreenUtils
import com.wbb.base.util.StatusBarUtils
import me.yokeyword.fragmentation.ISupportActivity
import me.yokeyword.fragmentation.SupportActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class NewBaseActivity<VM : BaseViewModel> : SupportActivity(), ISupportActivity,
    onGetClassTypeNam, OnNetWorkViewShowLinsener, NetWorkRequestLinsener,
    OnNetWorkCallLinsener {
    protected lateinit var mViewModel: VM
    protected var mFragment: Fragment? = null
    private var isLoadContentView = true
    protected lateinit var mProgressDialog: IOSLoadingDialog

    private var mNewWorkStateManager: ViewNetWorkStateManager? = null

    //用来存储需要监听的网络错误
    private var networkErrorTagList = ArrayList<String>()

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onEvent(baseEvent: BaseEvent) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!isHorizontalScreen()) {//设置智能竖屏
            try {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            } catch (e: Exception) {

            }
        }
        super.onCreate(savedInstanceState)
        setNotficeTitleColor()
        val loadBuilder = IOSLoadingDialog.Builder(this)
            .setMessage("加载中...")
            .setShowMessage(false)
            .setCancelable(true)
            .setCancelOutside(false)
        mProgressDialog = loadBuilder.create()

        if (isLoadContentView) {
            setContentView(getLayoutResId())
        }
        StatusBarUtils.setCommonUI(this, true)
        StatusBarUtils.setStatusBarColor(this, R.color.white_bg)
        initVM()
        initView()
        initData()
        startObserve()
        if (isRegEventBus()) {
            EventBus.getDefault().register(this)
        }
        val currClassName = onGetClassTypeNam()
        mNewWorkStateManager = ViewNetWorkStateManager(this, true)
        getLifecycle().addObserver(mNewWorkStateManager!!)
    }


    open fun isRegEventBus(): Boolean {
        return false
    }

    /**
     * 需要响应调用方法出现网络错误时候，需要添加一个
     */
    fun addNetWorkErrorTag(tag: String) {
        networkErrorTagList.add(tag)
    }

    /**
     * 是否可以横屏，默认是竖屏
     */
    open fun isHorizontalScreen(): Boolean {
        return false
    }

    /**
     * 设置这个类里面是否加载setContentView
     */
    protected fun setIsLoadContentView(isLoadContentView: Boolean) {
        this.isLoadContentView = isLoadContentView
    }

    fun initVM() {
        providerVMClass()?.let {
            mViewModel = ViewModelProvider(this).get(it)
            mViewModel.let(lifecycle::addObserver)
            mViewModel.regNetWorkRequestLinsener(this)
        }
    }

    /**
     * 如果需在要当前界面知道是否有网络，就可以实现这个类
     */
    open fun currentNewWorkState(isNetWork: Boolean) {

    }

    override fun onCurrentNewWorkState(isNetWork: Boolean) {
        currentNewWorkState(isNetWork)
    }

    override fun onDestroy() {
        if (::mViewModel.isInitialized) {
            mViewModel.let {
                lifecycle.removeObserver(it)
            }
        }
        if (isRegEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }

    fun getActivity(): Activity {
        return this
    }

    fun setNotficeTitleColor() {
        setNotficeTitleColor("#FFFFFFFF")
    }

    fun setNotficeTitleColor(color: String) {
        //设置状态栏为白底黑字
        ScreenUtils.setStatusBarColor(this, Color.parseColor(color))
        ScreenUtils.setStatusBarDarkFont(this, true)
    }

    /**
     * 添加
     *
     * @param frameLayoutId
     * @param fragment
     */
    protected open fun addFragment(frameLayoutId: Int, fragment: Fragment?) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            if (fragment.isAdded) {
                if (mFragment != null) {
                    transaction.hide(mFragment!!).show(fragment)
                } else {
                    transaction.show(fragment)
                }
            } else {
                if (mFragment != null) {
                    transaction.hide(mFragment!!).add(frameLayoutId, fragment)
                } else {
                    transaction.add(frameLayoutId, fragment)
                }
            }
            mFragment = fragment
            transaction.commit()
        }
    }

    protected fun showProgressDialog(content: String) {
        if (mProgressDialog.isShowing) {
            mProgressDialog.dismiss()
        }
        mProgressDialog.show()
    }

    protected fun showProgressDialog() {
        showProgressDialog("")
    }

    protected fun dismissProgressDialog() {
        mProgressDialog.dismiss()
    }

    open fun providerVMClass(): Class<VM>? = null

    open fun startObserve() {}
    abstract fun getLayoutResId(): Int
    abstract fun initView()
    abstract fun initData()


    override fun onNewWorkErrorCall(tag: String, ext: java.lang.Exception?) {
        if (networkErrorTagList.contains(tag)) {
            val info = NetWorkRequestData(1, ext?.message!!, tag)
            if (NetWorkMonitorManager.getInstance().currNetWorkState == NetWorkState.NONE) {
                onNotNetWorkErrror(info)
            } else {
                onNetWorkRequestError(info)
            }
        }
    }
}