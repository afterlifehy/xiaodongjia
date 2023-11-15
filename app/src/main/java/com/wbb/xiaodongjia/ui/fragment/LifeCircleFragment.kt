package com.wbb.xiaodongjia.ui.fragment

import android.content.Intent
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.ViewPager
import com.wbb.base.observer.ObserverKey
import com.alibaba.android.arouter.launcher.ARouter
import com.aries.ui.util.StatusBarUtil
import com.chouyou.base.ext.gone
import com.chouyou.base.ext.show
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.BaseApplication
import com.wbb.base.bean.LifeCircleActivityBean
import com.wbb.base.ext.i18N
import com.wbb.base.help.AppToH5Manager
import com.wbb.base.observer.ObserverManger
import com.wbb.base.observer.OnObserver
import com.wbb.base.util.*
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.LifeCircleViewpagerAdapter
import com.wbb.xiaodongjia.base.HomeUpDataInfo
import com.wbb.xiaodongjia.base.VbBaseFragment
import com.wbb.xiaodongjia.bean.LocalAddressInfo
import com.wbb.xiaodongjia.databinding.FragmentLifeCircleLayoutBinding
import com.wbb.xiaodongjia.mvvm.viewmodel.LifeCircleViewModel
import com.wbb.xiaodongjia.utils.LocalAddressManager

/**
 * Created by hy on 2021/1/25.
 * 生活圈
 */
class LifeCircleFragment : VbBaseFragment<LifeCircleViewModel, FragmentLifeCircleLayoutBinding>(),
    View.OnClickListener,
    OnObserver {
    var lifeCircleViewpagerAdapter: LifeCircleViewpagerAdapter? = null
    var lifeCircleActivityList: MutableList<LifeCircleActivityBean> = ArrayList()
    var countDownUtil: CountDownUtil? = null
    var selectedActivity: LifeCircleActivityBean? = null
    var currentPosition = 0
    var pageIndex = 0
    var pageSize = 5
    var isRunning = true

    override fun onReloadData() {
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false


    override fun providerVMClass(): Class<LifeCircleViewModel>? {
        return LifeCircleViewModel::class.java
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    override fun initView() {
        showLoadData()

        val lp = binding.tbToolbar.layoutParams as RelativeLayout.LayoutParams
        lp.topMargin = StatusBarUtil.getStatusBarHeight()
        binding.tbToolbar.layoutParams = lp
        binding.tbToolbar.setBackgroundColor(
            ContextCompat.getColor(
                BaseApplication.instance(),
                R.color.color_fffafbff
            )
        )
        StatusBarUtils.setStatusBarColor(activity, R.color.transparent)

        binding.vpLife.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {

                binding.pivLifeCircle.setSelected(position)
                countDownUtil?.cancel()
                countDownUtil = null
                currentPosition = position % lifeCircleActivityList.size
                selectedActivity = lifeCircleActivityList[position]

                binding.tvApply.setTextColor(
                    ContextCompat.getColor(
                        BaseApplication.instance(),
                        R.color.color_ff221503
                    )
                )
                if (selectedActivity?.attendStartTime!! > System.currentTimeMillis()) {
                    binding.tvApply.text = i18N(R.string.未开始)
                    binding.tvApply.setTextColor(
                        ContextCompat.getColor(
                            BaseApplication.instance(),
                            R.color.color_ffd2a140
                        )
                    )
                    startCountDown(selectedActivity?.attendStartTime!! - System.currentTimeMillis())
                } else if (selectedActivity?.attendStartTime!! < System.currentTimeMillis() && selectedActivity?.attendEndTime!! > System.currentTimeMillis()) {
                    if (selectedActivity?.attend!!) {
                        binding.tvApply.text = i18N(R.string.已报名)
                    } else {
                        if (selectedActivity?.remainNum == 0) {
                            binding.tvApply.text = i18N(R.string.已结束)
                            binding.tvApply.setTextColor(
                                ContextCompat.getColor(
                                    BaseApplication.instance(),
                                    R.color.color_ffd2a140
                                )
                            )
                        } else {
                            binding.tvApply.text = i18N(R.string.立即报名)
                        }
                    }
                    startCountDown(selectedActivity?.attendEndTime!! - System.currentTimeMillis())
                } else if (selectedActivity?.attendEndTime!! < System.currentTimeMillis()) {
                    binding.tvApply.text = i18N(R.string.已结束)
                    binding.tvApply.setTextColor(
                        ContextCompat.getColor(
                            BaseApplication.instance(),
                            R.color.color_ffd2a140
                        )
                    )

                    binding.tvDay.text = "0"
                    binding.tvHour.text = "0"
                    binding.tvMinute.text = "0"
                }
            }
        })

        binding.srlLifeCircle.setOnRefreshListener {
            getData()
            binding.srlLifeCircle.finishRefresh()
        }
        binding.srlLifeCircle.setEnableLoadMore(false)

        binding.tvLocation.setText(LocalAddressManager.instance().getCity())

        startListener()
    }

    private fun startListener() {

        binding.llCountDown.setOnClickListener(this)
        binding.tvLocation.setOnClickListener(this)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mActivityListLiveData.observe(this@LifeCircleFragment, Observer {
                hideLoadData()
                lifeCircleActivityList.clear()
                lifeCircleActivityList.addAll(it)
                if (lifeCircleActivityList.size > 0) {

                    binding.vpLife.show()
                    binding.pivLifeCircle.show()
                    binding.llCountDown.show()
                    binding.llEmptyView.gone()
                    binding.vpLife.removeAllViews()
                    lifeCircleViewpagerAdapter =
                        LifeCircleViewpagerAdapter(lifeCircleActivityList, this@LifeCircleFragment)
                    binding.vpLife.offscreenPageLimit = 0
                    binding.vpLife.currentItem = 0
                    binding.vpLife.adapter = lifeCircleViewpagerAdapter
                    selectedActivity = lifeCircleActivityList[currentPosition]

                    binding.tvApply.setTextColor(
                        ContextCompat.getColor(
                            BaseApplication.instance(),
                            R.color.color_ff221503
                        )
                    )
                    if (selectedActivity?.attendStartTime!! > System.currentTimeMillis()) {
                        binding.tvApply.text = i18N(R.string.未开始)
                        binding.tvApply.setTextColor(
                            ContextCompat.getColor(
                                BaseApplication.instance(),
                                R.color.color_ffd2a140
                            )
                        )
                        startCountDown(selectedActivity?.attendStartTime!! - System.currentTimeMillis())
                    } else if (selectedActivity?.attendStartTime!! < System.currentTimeMillis() && selectedActivity?.attendEndTime!! > System.currentTimeMillis()) {
                        if (selectedActivity?.attend!!) {
                            binding.tvApply.text = i18N(R.string.已报名)
                        } else {
                            if (selectedActivity?.remainNum == 0) {
                                binding.tvApply.text = i18N(R.string.已结束)
                                binding.tvApply.setTextColor(
                                    ContextCompat.getColor(
                                        BaseApplication.instance(),
                                        R.color.color_ffd2a140
                                    )
                                )
                            } else {
                                binding.tvApply.text = i18N(R.string.立即报名)
                            }
                        }
                        startCountDown(selectedActivity?.attendEndTime!! - System.currentTimeMillis())
                    } else if (selectedActivity?.attendEndTime!! < System.currentTimeMillis()) {
                        binding.tvApply.text = i18N(R.string.已结束)
                        binding.tvApply.setTextColor(
                            ContextCompat.getColor(
                                BaseApplication.instance(),
                                R.color.color_ffd2a140
                            )
                        )

                        binding.tvDay.text = "0"
                        binding.tvHour.text = "0"
                        binding.tvMinute.text = "0"
                    }
                } else {
                    binding.vpLife.gone()
                    binding.llCountDown.gone()
                    binding.llEmptyView.show()
                    binding.pivLifeCircle.gone()
                }
            })
            mException.observe(this@LifeCircleFragment, Observer {
                LogUtil.v(it.toString())
            })
        }
    }

    override fun initData() {
        addNetWorkErrorTag(LifeCircleViewModel.NET_WORK_MAT_TA)
        ObserverManger.getInstance(ObserverKey.LIFE_INFO_UP)
            .registerObserver(this@LifeCircleFragment)
    }

    override fun getData() {
        super.getData()
        getActivityPage()
    }

    private fun getActivityPage() {
        var par = HashMap<String, Any>()
        par["cityId"] = LocalAddressManager.instance().getCityId()
        mViewModel.activityList(par)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cv_lifeCircle -> {
                selectedActivity?.let {
                    AppToH5Manager.toActivityDetails(it.activityId)
                }
            }
            R.id.ll_countDown -> {
                if (selectedActivity?.attendEndTime!! < System.currentTimeMillis()) {
                    ToastUtils.showToash(i18N(R.string.活动已结束))
                    return
                }
                if (selectedActivity?.attendStartTime!! < System.currentTimeMillis() && selectedActivity?.attendEndTime!! > System.currentTimeMillis() && selectedActivity?.remainNum == 0) {
                    ToastUtils.showToash(i18N(R.string.活动已结束))
                    return
                }
                selectedActivity?.let {
                    AppToH5Manager.toActivityDetails(it.activityId)
                }
            }
            R.id.tv_location -> {
                ARouter.getInstance().build(ARouterMap.ADDRESS_LOCATION)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

    override fun onGetClassTypeNam(): Any {
        return "生活圈"
    }

    fun startCountDown(time: Long) {
        countDownUtil = CountDownUtil(time, 1000 * 60, object : CountDownUtil.TimeCallBack {
            override fun onTimeOut() {

                binding.tvApply.setTextColor(
                    ContextCompat.getColor(
                        BaseApplication.instance(),
                        R.color.color_ff221503
                    )
                )
                if (selectedActivity?.attendStartTime!! > System.currentTimeMillis()) {
                    binding.tvApply.text = i18N(R.string.未开始)
                    binding.tvApply.setTextColor(
                        ContextCompat.getColor(
                            BaseApplication.instance(),
                            R.color.color_ffd2a140
                        )
                    )
                } else if (selectedActivity?.attendStartTime!! < System.currentTimeMillis() && selectedActivity?.attendEndTime!! > System.currentTimeMillis()) {
                    if (selectedActivity?.attend!!) {
                        binding.tvApply.text = i18N(R.string.已报名)
                    } else {
                        if (selectedActivity?.remainNum == 0) {
                            binding.tvApply.text = i18N(R.string.已结束)
                            binding.tvApply.setTextColor(
                                ContextCompat.getColor(
                                    BaseApplication.instance(),
                                    R.color.color_ffd2a140
                                )
                            )
                        } else {
                            binding.tvApply.text = i18N(R.string.立即报名)
                        }
                    }
                    startCountDown(selectedActivity?.attendEndTime!! - System.currentTimeMillis())
                } else if (selectedActivity?.attendEndTime!! < System.currentTimeMillis()) {
                    binding.tvApply.text = i18N(R.string.已结束)
                    binding.tvApply.setTextColor(
                        ContextCompat.getColor(
                            BaseApplication.instance(),
                            R.color.color_ffd2a140
                        )
                    )

                    binding.tvDay.text = "0"
                    binding.tvHour.text = "0"
                    binding.tvMinute.text = "0"
                }
            }

            override fun onTimeTick(millisUntilFinished: Long) {
                binding.tvDay.text = DateUtil.time2Date(millisUntilFinished, 1)
                binding.tvHour.text = DateUtil.time2Date(millisUntilFinished, 2)
                binding.tvMinute.text = DateUtil.time2Date(millisUntilFinished, 3)
            }

        })
        countDownUtil?.start()
    }

    override fun update(obj: Any?) {
        if (obj is HomeUpDataInfo) {
            when (obj.mTypeCode) {
                HomeUpDataInfo.UP_ADDRESS -> {//更新地址
                    val data = obj.data
                    if (data is LocalAddressInfo) {
                        binding.tvLocation.text = data.cityName
                        OnBuriedPointManager.get().getOnBuriedPointManager()
                            ?.OnCitiesSwitch(data.cityName, "生活圈", "1")
                    }
                    getData()
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownUtil?.onFinish()
        countDownUtil = null
        ObserverManger.getInstance(ObserverKey.LIFE_INFO_UP).removeObserver(this)
    }

    override fun getVbBindingView(): ViewBinding {
        return FragmentLifeCircleLayoutBinding.inflate(layoutInflater)
    }
}