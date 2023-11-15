package com.wbb.xiaodongjia.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.event.ChangeApiEvent
import com.wbb.base.help.ActivityCacheManager
import com.wbb.base.help.AppExitManager
import com.wbb.base.util.ARouterMap
import com.wbb.base.util.StatusBarUtils
import com.wbb.base.util.UserInfoManager
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.MainTabAdapter
import com.wbb.xiaodongjia.bean.MainTabInfo
import com.wbb.xiaodongjia.dialog.UserInfoInputDialog
import com.wbb.xiaodongjia.mvvm.viewmodel.MainViewModel
import com.wbb.xiaodongjia.mvvm.viewmodel.UserViewModel
import com.wbb.xiaodongjia.ui.fragment.EastClassroomFragment
import com.wbb.xiaodongjia.ui.fragment.HomeFragment
import com.wbb.xiaodongjia.ui.fragment.LifeCircleFragment
import com.wbb.xiaodongjia.ui.fragment.MineFragment
import com.wbb.xiaodongjia.utils.CheckVersionUtil
import kotlinx.android.synthetic.main.activity_main_layout.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *
 * 主页
 * Created by zj on 2021/1/25.
 */
class MainActivity : BaseDataActivityKt<MainViewModel>(), MainTabAdapter.OnTabSelectLinsener {
    private var mUserViewModel: UserViewModel? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onEvent(changeApiEvent: ChangeApiEvent) {
        changeApi()
    }

    override fun isRegEventBus(): Boolean {
        return true
    }

    override fun onReloadData() {
    }

    override fun providerVMClass(): Class<MainViewModel>? {
        return MainViewModel::class.java
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            isBing.observe(this@MainActivity, Observer {
                if (!it) {
                    showBind()
                }
            })
            mAppVersionLiveData.observe(this@MainActivity, Observer {
                CheckVersionUtil.instance?.init(
                    ActivityCacheManager.instance().getLastActivity() as MainActivity?, it
                )
            })
        }
    }

    /**
     * 弹窗输入用户名称
     */
    private fun showBind() {
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        val mUserInfoInputDialog = UserInfoInputDialog(this, mUserViewModel)
        mUserInfoInputDialog.show()
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.activity_main_layout
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setTranslucentStatus(this)
        StatusBarUtils.setNavigationBarColor(this, R.color.white)
        //StatusBarUtils.setCommonUI(this, true)
        val mUserInfo = UserInfoManager.instance().getUserInfo()
    }

    override fun initView() {
        val tabList = ArrayList<MainTabInfo>()
        val homeMainTabInfo = MainTabInfo("首页", HomeFragment(), "", "home/home.json")
        tabList.add(homeMainTabInfo)
        tabList.add(MainTabInfo("生活圈", LifeCircleFragment(), "", "life/life.json"))
        tabList.add(MainTabInfo("东家课堂", EastClassroomFragment(), "", "class/class.json"))
        tabList.add(MainTabInfo("我的", MineFragment(), "", "mine/mine.json"))

        val mMainTabAdapter = MainTabAdapter(tabList, this)
        rl_tab_list.layoutManager = GridLayoutManager(this@MainActivity, 4)
        rl_tab_list.adapter = mMainTabAdapter
        onTabSelectLinsener(0, homeMainTabInfo)

    }


    override fun onSaveInstanceState(outState: Bundle) {
        // super.onSaveInstanceState(outState)
    }

    override fun initData() {
        mViewModel.getLevelCount()
        mViewModel.appVersion()
    }

    override fun onGetClassTypeNam(): Any {
        return "首页"
    }

    override fun onTabSelectLinsener(
        tabIndex: Int,
        item: MainTabInfo?
    ) {
        item?.let {
            OnBuriedPointManager.get().getOnBuriedPointManager()?.OnTabClickTabClick(it.tabName)
            when (it.tabName) {
                "首页",
                "生活圈",
                "东家课堂" -> {
                    StatusBarUtils.setCommonUI(this, true)
                }
                "我的" -> {
                    StatusBarUtils.setCommonUI(this, false)
                }
            }
            showFragment(
                supportFragmentManager,
                supportFragmentManager.beginTransaction(),
                it.mFragment,
                R.id.fl_container,
                it.tabName
            )
        }

    }

    private fun changeApi() {
        ARouter.getInstance().build(ARouterMap.LOGIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .navigation()
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (AppExitManager.instance().checkIsExit(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}