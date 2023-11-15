package com.wbb.xiaodongjia.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocation
import com.github.dfqin.grantor.PermissionListener
import com.github.dfqin.grantor.PermissionsUtil
import com.google.android.material.appbar.AppBarLayout
import com.wbb.base.BaseApplication
import com.wbb.base.bean.HomeMenuInfo
import com.wbb.base.bean.HomeRecommendInfo
import com.wbb.base.bean.MsgData
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.help.OnItemClickLinsener
import com.wbb.base.map.DxlNewLocationManager
import com.wbb.base.observer.ObserverKey
import com.wbb.base.observer.ObserverManger
import com.wbb.base.observer.OnObserver
import com.wbb.base.pf.PreferencesKeys
import com.wbb.base.pf.StoreFactory
import com.wbb.base.util.*
import com.wbb.base.view.flycotablayout.listener.OnTabSelectListener
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.HomeMenuAdapter
import com.wbb.xiaodongjia.base.HomeUpDataInfo
import com.wbb.xiaodongjia.base.VbBaseFragment
import com.wbb.xiaodongjia.bean.LocalAddressInfo
import com.wbb.xiaodongjia.databinding.FragmentHomeLayoutBinding
import com.wbb.xiaodongjia.listener.OnUpLocalAddressLinsener
import com.wbb.xiaodongjia.mvvm.viewmodel.HomeViewModel
import com.wbb.xiaodongjia.mvvm.viewmodel.HomeViewModel.Companion.NET_WORK_MAT_TA
import com.wbb.xiaodongjia.ui.fragment.help.FixBounceV26Behavior
import com.wbb.xiaodongjia.ui.fragment.help.HomeRecommendListFragmentStatePagerAdapter
import com.wbb.xiaodongjia.ui.fragment.help.HomeToolsHelp
import com.wbb.xiaodongjia.utils.LocalAddressManager
import kotlinx.coroutines.*

/**
 * Created by zj on 2021/1/25.
 */
class HomeFragment : VbBaseFragment<HomeViewModel, FragmentHomeLayoutBinding>(),
    View.OnClickListener, OnObserver {
    //推荐tab
    private var mTjLis: List<HomeRecommendInfo>? = null
    private var pagerAdapter: HomeRecommendListFragmentStatePagerAdapter? = null
    var mHomeMenuAdapter: HomeMenuAdapter? = null
    private var mMsgData: MsgData? = null

    //当前tab页选中的界面
    private var mMerchantSelectIndex = 0
    override fun onReloadData() {
    }

    override fun providerVMClass(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override val isLoadNotData: Boolean
        get() = true
    override val isShowTitle: Boolean
        get() = false

    @SuppressLint("SetTextI18n")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mCategoryData.observe(this@HomeFragment, Observer {
                binding.homeSmar.finishRefresh()
                bindMenuList(it)
            })
            errMsg.safeObserve(this@HomeFragment, Observer {
                ToastUtils.showErrorToast(it.msg)
                binding.homeSmar.finishRefresh()
            })
            mProfitData.observe(this@HomeFragment, Observer {
                binding.homeTopView.tvAddWbb.text = it.dayProfit
                binding.homeTopView.tvCurrDayAdd.text = "今日新增（${it.dayProfitUnit})"
                binding.homeTopView.tvTotalWbb.text = it.sumProfit
                binding.homeTopView.tvTotalGet.text = "累计获得（${it.sumProfitUnit})"
                binding.homeTopView.tvGetAl.text = "获得奖励（${it.rewardUnit})"
                binding.homeTopView.tvGetRewards.text = it.reward
            })
            mNewMsgData.observe(this@HomeFragment, Observer {
                it.list.let {
                    if (it.size > 0) {
                        GlobalScope.launch(Dispatchers.Main) {
                            val locMsg =
                                StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                                    .getString(PreferencesKeys.NEW_MSG_ID)
                            if (locMsg.equals(it.get(0).siteMessageId)) {

                                binding.homeTopHead.ivMsgShow.visibility = View.GONE
                            } else {
                                mMsgData = it.get(0)
                                binding.homeTopHead.ivMsgShow.visibility = View.VISIBLE
                            }
                        }

                    } else {
                        binding.homeTopHead.ivMsgShow.visibility = View.GONE
                    }
                }
            })
            mException.safeObserve(this@HomeFragment, Observer {
                binding.homeSmar.finishRefresh()
            })
        }
    }

    override fun getData() {
        super.getData()
        mViewModel.getCategory()
        mViewModel.getProfit()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    override fun onDestroy() {
        super.onDestroy()
        ObserverManger.getInstance(ObserverKey.HOME_INFO_UP).removeObserver(this)
    }

    fun bindMenuList(list: MutableList<HomeMenuInfo>) {
        if (mHomeMenuAdapter == null) {
            mHomeMenuAdapter = HomeMenuAdapter(list)

            binding.homeTopView.rvMuneButton.layoutManager = LinearLayoutManager(
                context!!,
                RecyclerView.HORIZONTAL,
                false
            )
            mHomeMenuAdapter?.setOnItemClickLinsener(object : OnItemClickLinsener<HomeMenuInfo> {
                override fun onItemClick(postion: Int, mInfo: HomeMenuInfo?) {
                    if (TextUtils.isEmpty(mInfo?.categoryId)) {
                        return
                    }
                    OnBuriedPointManager.get().getOnBuriedPointManager()
                        ?.OnIconClick(postion + 1, mInfo!!.name, "首页-首页", "首页")
                    val mMapLoac = LocalAddressManager.instance().getCurrentAMapLocation()
                    var longitude = ""
                    var latitude = ""
                    //\h5定位太慢，如果APP已经获取到定位，直接把经纬度传过去
                    mMapLoac?.let {
                        longitude = it.longitude.toString()
                        latitude = it.latitude.toString()
                    }
                    ARouter.getInstance().build(ARouterMap.WEBVIEW).withString(
                        ARouterMap.URL,
                        Constant.MERCHANT_TYPE_LIST + "?id=${mInfo?.categoryId}&cityId=${
                            LocalAddressManager.instance().getCityId()
                        }&longitude=${longitude}&latitude=${latitude}"
                    )
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                }

            })
            binding.homeTopView.rvMuneButton.adapter = mHomeMenuAdapter
        } else {
            mHomeMenuAdapter?.setList(list)
        }
        setMerchantList(list)

    }

    /**
     * 开始启动定位
     */
    fun startLocalAddress(isLoadData: Boolean) {
        DxlNewLocationManager(requireContext()).startPositioning(requireContext(), object :
            DxlNewLocationManager.OnCallback {
            override fun onLocation(aMapLocation: AMapLocation?) {
                aMapLocation?.let {

                    LocalAddressManager.instance().saveCurrentAMapLocation(it)
                }
                if (isLoadData) {
                    setCityName()
                    getData()
                } else {
                    upMerchantAddressInfo()
                }

            }

            override fun onNotGetAuthority() {
                if (isLoadData) {
                    setCityName()
                    getData()
                }
            }

            override fun onTimeout() {
                if (isLoadData) {
                    setCityName()
                    getData()
                }
            }
        })
    }

    /**
     * 获取消息列表，用来判断是否有新消息
     */
    fun getNewMsgInfo() {
        val par = HashMap<String, String>()
        par["page"] = "1"
        par["pageSize"] = "1"
        mViewModel.getMineMsgPage(par)
    }

    override fun onResume() {
        super.onResume()
        getNewMsgInfo()
    }

    /**
     * 获取定位到后，要去刷新商户列表
     */
    fun upMerchantAddressInfo() {
        pagerAdapter?.fragments?.forEach {
            if (it is OnUpLocalAddressLinsener) {
                (it as OnUpLocalAddressLinsener).onUpLocalAddress()

            }
        }

    }

    /**
     * 设置当前城市名称
     */
    fun setCityName() {
        val mLocalAddress = LocalAddressManager.instance().getNetworkRequestDataCityInfo()
        if (mLocalAddress != null) {
            binding.homeTopHead.tvCityName.setText(mLocalAddress.cityName)
        }
    }

    override fun initView() {
        setListener()
        HomeToolsHelp(context!!, binding)


        /**
         * 调整apl的layoutParams，这是为了解决CoordinaatorLayout内部子view联动优化给这个业务带来的一个bug,这样设置就行了
         */

        val layoutParams: CoordinatorLayout.LayoutParams =
            binding.aplBt.getLayoutParams() as CoordinatorLayout.LayoutParams
        val behavior: AppBarLayout.Behavior = FixBounceV26Behavior(context, null)
        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return true
            }
        })
        layoutParams.setBehavior(behavior)
    }

    /**
     * 设置商家列表A
     */
    fun setMerchantList(list: MutableList<HomeMenuInfo>) {
        //给tab指示器设置宽度
        val listFragment = ArrayList<HomeRecommendInfo>()
        listFragment.add(HomeRecommendInfo("推荐", "RECOMMEND"))
        listFragment.add(HomeRecommendInfo("东家招募", "D_JIA_RECOMMEND"))
        list.forEach {
            listFragment.add(HomeRecommendInfo(it.name, it.categoryId, it))
        }
        initViewPagerData(listFragment)
    }

    /**
     * 切换城市后进行viewpager的数据刷新
     */
    private fun initViewPagerData(mList: List<HomeRecommendInfo>?) {
        if (mList == null || mList.size <= 0) {
            return
        }
        this.mTjLis = mList
        if (pagerAdapter != null) { //要把原有的frament清除

            binding.idStickynavlayoutViewpager.removeAllViews()
            val fragments: List<Fragment> = pagerAdapter!!.getFragments()
            if (fragments.size > 0) {
                val fragmentTransaction: FragmentTransaction =
                    childFragmentManager.beginTransaction()
                for (mFragment in fragments) {
                    fragmentTransaction.remove(mFragment)
                }
            }
        }
        val tabNameList = arrayOfNulls<String>(size = mList.size)
        for (indxe in 0..mList.size - 1) {
            tabNameList[indxe] = mList.get(indxe).name
        }
        var currentItem = 0
        if (mMerchantSelectIndex < mList.size) {//如果不是城市选择，在刷新的时候要选中上一次正在查看的tab
            currentItem = mMerchantSelectIndex
        }
        //推荐
        pagerAdapter = HomeRecommendListFragmentStatePagerAdapter(childFragmentManager, mList)

        binding.idStickynavlayoutViewpager.adapter = pagerAdapter
        binding.idStickynavlayoutViewpager.setScrollAble(true)
        binding.idStickynavlayoutViewpager.offscreenPageLimit = 1
        binding.idStickynavlayoutViewpager.currentItem = currentItem
        binding.idStickynavlayoutIndicator.setViewPager(
            binding.idStickynavlayoutViewpager,
            tabNameList
        )

        binding.idStickynavlayoutIndicator.currentTab = currentItem
        binding.idStickynavlayoutIndicator.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                mMerchantSelectIndex = position
                //  clsssTab(mList.get(position).name)
            }

            override fun onTabReselect(position: Int) {
            }
        })
        binding.idStickynavlayoutViewpager.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                if (mMerchantSelectIndex != position) {
                    mTjLis?.get(position)?.let {
                        clsssTab(it.name)
                    }


                }
                mMerchantSelectIndex = position

            }

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

        })
    }

    /**
     * 生成站位菜单项,免得加载慢的时候，界面看起来很怪
     *
     * */
    fun createDefaultMenuTab() {
        val empList = ArrayList<HomeMenuInfo>()
        empList.add(HomeMenuInfo())
        empList.add(HomeMenuInfo())
        empList.add(HomeMenuInfo())
        empList.add(HomeMenuInfo())
        bindMenuList(empList)
    }

    override fun initData() {
        createDefaultMenuTab()
        addNetWorkErrorTag(NET_WORK_MAT_TA)
        ObserverManger.getInstance(ObserverKey.HOME_INFO_UP).registerObserver(this)
        val mAddressInfo = LocalAddressManager.instance().getUserSelecetCityInfo()

        binding.homeSmar.autoRefresh()
        if (mAddressInfo == null) {
            //去获取定位
            startLocalAddress(true)
        } else {
            getData()
            setCityName()
            startLocalAddress(false)
        }

    }

    override fun onGetClassTypeNam(): Any {
        return "home"
    }

    fun setListener() {
        binding.homeSmar.setOnRefreshListener {
            getNewMsgInfo()
            getData()
        }

        binding.homeTopHead.llSec.setOnClickListener(this)
        binding.homeTopHead.rlMsg.setOnClickListener(this)
        binding.homeTopView.llLeftLayout.setOnClickListener(this)
        binding.homeTopHead.llAddress.setOnClickListener(this)
    }

    fun clsssTab(clickName: String?) {
        clickName?.let {
            OnBuriedPointManager.get().getOnBuriedPointManager()
                ?.OnClassifyingLists("首页", "首页-首页", it)
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ll_sec -> {
                ARouter.getInstance().build(ARouterMap.SEARCH)
                    .withString(ARouterMap.SEARCH_TYPE, MagicValue.SEARCH_TYPE_BUSINESS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
            R.id.rl_msg -> {


//                val mActivityDialog = ActivityDialog(requireContext(), object :
//                        OnActiivtyClickLinsener {
//                    override fun onActivityClickLinsener(mWaterDropletsInfo: WaterDropletsInfo) {
//                        val mReceivedSuccessfullyDialog =
//                                ReceivedSuccessfullyDialog(requireContext())
//                        mReceivedSuccessfullyDialog.show()
//                    }
//                })
//                mActivityDialog.show()
                //clsssTab("消息中心")
                mMsgData?.let {
                    GlobalScope.launch(Dispatchers.Main) {
                        StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                            .putString(PreferencesKeys.NEW_MSG_ID, it.siteMessageId)
                    }
                }
                ARouter.getInstance().build(ARouterMap.WEBVIEW)
                    .withString(ARouterMap.URL, Constant.NOTICATION)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                OnBuriedPointManager.get().getOnBuriedPointManager()
                    ?.ButtonClick("首页", "首页-首页", "消息中心")
            }
            R.id.ll_address -> {//选择地址
                ARouter.getInstance().build(ARouterMap.ADDRESS_LOCATION)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
            R.id.ll_left_layout -> {
                PermissionsUtil.requestPermission(
                    BaseApplication.instance(),
                    object : PermissionListener {
                        override fun permissionGranted(permission: Array<out String>) {
                            ARouter.getInstance().build(ARouterMap.MYCAPTURE)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                        }

                        override fun permissionDenied(permission: Array<out String>) {
                            ToastUtils.showToash("没有读写权限，不能保存图片")
                        }
                    },
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    override fun update(obj: Any?) {
        if (obj is HomeUpDataInfo) {
            when (obj.mTypeCode) {
                HomeUpDataInfo.UP_ADDRESS -> {//更新地址
                    val data = obj.data
                    if (data is LocalAddressInfo) {
                        binding.homeTopHead.tvCityName.setText(data.cityName)
                        getData()
                        OnBuriedPointManager.get().getOnBuriedPointManager()
                            ?.OnCitiesSwitch(data.cityName, "首页", "1")
                    }

                }
            }

        }
    }

    override fun getVbBindingView(): ViewBinding {
        return FragmentHomeLayoutBinding.inflate(layoutInflater)
    }
}