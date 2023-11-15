package com.chouyou.waterbridge.base

import android.content.Intent
import androidx.fragment.app.Fragment
import com.wbb.base.viewbase.NewBaseActivity
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.R
import com.wbb.base.bean.NetWorkRequestData

/**
 * Created by zj on 2018/7/5 0005.
 */
abstract class DefaultFragmetnAttachActivity<VM : BaseViewModel> : NewBaseActivity<VM>() {
    var mLoadFragment: Fragment? = null
    override fun getLayoutResId(): Int {
        return R.layout.activity_default_layout
    }

    override fun initView() {
        mLoadFragment = loadFragment
        bindIntent(intent, mLoadFragment!!)
        if (mLoadFragment != null) { //把fragment添加到activity
            addFragment(R.id.rl_content, mLoadFragment)
        }
    }

    open fun bindIntent(intent: Intent?, mLoadFragment: Fragment) {

    }

    protected abstract val loadFragment: Fragment?

    override fun onGetClassTypeNam(): Any {
        return "默认加载fragment"
    }

    override fun onNetWorkRequestError(errror: NetWorkRequestData) {
    }

    override fun onNotNetWorkErrror(errror: NetWorkRequestData) {
    }
}