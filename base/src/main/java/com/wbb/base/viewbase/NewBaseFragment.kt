package com.chouyou.base.base


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.wbb.base.bean.NetWorkRequestData
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.dialog.IOSLoadingDialog
import com.wbb.base.mvvm.base.NetWorkRequestLinsener
import com.wbb.base.mvvm.base.OnNetWorkCallLinsener
import com.wbb.base.network.NetWorkMonitorManager
import com.wbb.base.network.NetWorkState
import com.wbb.base.network.ViewNetWorkStateManager
import com.wbb.base.network.OnNetWorkViewShowLinsener
import com.wbb.base.viewbase.onGetClassTypeNam
import org.greenrobot.eventbus.EventBus
import java.lang.Exception

abstract class NewBaseFragment<VM : BaseViewModel> : Fragment(), onGetClassTypeNam,
    OnNetWorkViewShowLinsener, NetWorkRequestLinsener, OnNetWorkCallLinsener {
    protected lateinit var mViewModel: VM
    protected var mRoot: View? = null
    protected var mInflater: LayoutInflater? = null
    protected var mFragment: Fragment? = null
    protected lateinit var mProgressDialog: IOSLoadingDialog
    private var mNewWorkStateManager: ViewNetWorkStateManager? = null

    //用来存储需要监听的网络错误
    private var networkErrorTagList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loadBuilder =
            IOSLoadingDialog.Builder(activity).setMessage("加载中...").setShowMessage(false)
                .setCancelable(true).setCancelOutside(false)
        mProgressDialog = loadBuilder.create()
        mNewWorkStateManager = ViewNetWorkStateManager(this, false)
        getLifecycle().addObserver(mNewWorkStateManager!!)
    }

    open fun refreshPage() {
    }

    open fun isRegEventBus(): Boolean {
        return false
    }

    /**
     * 如果需在要当前界面知道是否有网络，就可以实现这个类
     */
    open fun currentNewWorkState(isNetWork: Boolean) {

    }

    override fun onCurrentNewWorkState(isNetWork: Boolean) {
        currentNewWorkState(isNetWork)
    }


    /**
     * 需要响应调用方法出现网络错误时候，需要添加一个
     */
    fun addNetWorkErrorTag(tag: String) {
        networkErrorTagList.add(tag)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initVM()
        initView()
        initData()
        startObserve()
        if (!EventBus.getDefault().isRegistered(this) && isRegEventBus()) {
            EventBus.getDefault().register(this)
        }
        val currClassName = onGetClassTypeNam()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutResId(), null)
    }

    open fun startObserve() {}
    abstract fun getLayoutResId(): Int
    abstract fun initView()
    abstract fun initData()
    fun onRestartInstance(bundle: Bundle) {

    }

    private fun initVM() {
        providerVMClass()?.let {
            mViewModel = ViewModelProvider(this).get(it)
            mViewModel.let(lifecycle::addObserver)
            mViewModel.regNetWorkRequestLinsener(this)
        }
    }


    open fun providerVMClass(): Class<VM>? = null

    protected fun startActivity(z: Class<*>) {
        startActivity(Intent(activity, z))
    }

    protected fun showProgressDialog(content: String) {
        mProgressDialog.show()
    }

    protected fun showProgressDialog() {
        showProgressDialog("")
    }

    protected fun dismissProgressDialog() {
        mProgressDialog.dismiss()
    }

    override fun onDestroy() {
        if (::mViewModel.isInitialized) {
            mViewModel.let {
                lifecycle.removeObserver(it)
            }
        }
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    /**
     * 添加
     *
     * @param frameLayoutId
     * @param fragment
     */
    protected open fun addFragment(frameLayoutId: Int, fragment: Fragment?) {
        if (fragment != null) {
            val transaction = activity!!.supportFragmentManager.beginTransaction()
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


    override fun onNewWorkErrorCall(tag: String, ext: Exception?) {
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