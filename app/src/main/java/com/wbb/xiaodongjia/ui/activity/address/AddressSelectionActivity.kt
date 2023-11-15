package com.wbb.xiaodongjia.ui.activity.address

import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocation
import com.chouyou.base.ext.gone
import com.chouyou.base.ext.show
import com.wbb.base.bean.CityChildItemInfo
import com.wbb.base.bean.CityItemInfo
import com.wbb.base.ext.i18N
import com.wbb.base.map.DxlNewLocationManager
import com.wbb.base.observer.ObserverKey
import com.wbb.base.observer.ObserverManger
import com.wbb.base.util.ARouterMap
import com.wbb.base.util.StringUtil
import com.wbb.base.wiget.SearchInputView
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.CityChildAdapter
import com.wbb.xiaodongjia.adapter.CityListAdapter
import com.wbb.xiaodongjia.adapter.LetterAdapter
import com.wbb.xiaodongjia.base.HomeUpDataInfo
import com.wbb.xiaodongjia.base.VbBaseActivity
import com.wbb.xiaodongjia.bean.CityInfo
import com.wbb.xiaodongjia.bean.LocalAddressInfo
import com.wbb.xiaodongjia.databinding.ActivityAddresLoaclLayoutBinding
import com.wbb.xiaodongjia.mvvm.viewmodel.AddressSelectViewModel
import com.wbb.xiaodongjia.utils.LocalAddressManager

/**
 * Created by zj on 2021/1/29.
 * 地区选择
 */
@Route(path = ARouterMap.ADDRESS_LOCATION)
class AddressSelectionActivity :
    VbBaseActivity<AddressSelectViewModel, ActivityAddresLoaclLayoutBinding>(),
    CityListAdapter.OnSelectLinsener {
    var mDxlNewLocationManager: DxlNewLocationManager? = null
    private var mAMapLocation: AMapLocation? = null
    val mAllCityList = ArrayList<CityInfo>()
    override fun onReloadData() {
    }

    override fun providerVMClass(): Class<AddressSelectViewModel>? {
        return AddressSelectViewModel::class.java
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mOpenCity.observe(this@AddressSelectionActivity, Observer {
                bindCityList(it)
            })
            mAllCity.observe(this@AddressSelectionActivity, Observer {

            })
        }
    }

    /**
     * 获取当前定位
     */
    fun getCurrentLocation() {
        if (mDxlNewLocationManager == null) {
            mDxlNewLocationManager = DxlNewLocationManager(this)

        }
        mDxlNewLocationManager?.startPositioning(this, object :
            DxlNewLocationManager.OnCallback {
            override fun onLocation(aMapLocation: AMapLocation?) {
                aMapLocation?.let {
                    mAMapLocation = it
                    binding.tvCurrAddress.setText("${i18N(R.string.curre_city) + it.city}")
                }
            }

            override fun onTimeout() {

            }

            override fun onNotGetAuthority() {
            }
        })

    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false


    override fun initView() {

        binding.sflSearch.initSearch(
            "城市、拼音 ",
            true,
            object : SearchInputView.OnSearchInputLinsener {
                override fun onCancer(string: String) {
                    finish()
                }

                override fun onInputText(string: String) {
                    inquireCity(string)
                }

                override fun onUserClickSearch(string: String) {
                }
            })

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvCurrAddress.setOnClickListener {
            mAMapLocation?.let {
                saveLocalAddress(
                    LocalAddressInfo(
                        it.city,
                        LocalAddressManager.instance().userLocalAddress(it.adCode)
                    )
                )
            }
        }
    }

    /**
     * 搜索城市
     */
    fun inquireCity(str: String) {
        if (TextUtils.isEmpty(str)) {
            showGoneList(true)
        } else {
            showGoneList(false)
            val mList = queryCity(str)
            if (mList.size > 0) {

                binding.notDataView.gone()

                binding.rlQueryCityList.show()
                val mCityChildAdapter = CityChildAdapter(mList)
                mCityChildAdapter.setOnSelectLinsener(this)
                binding.rlQueryCityList.layoutManager = LinearLayoutManager(this)
                binding.rlQueryCityList.adapter = mCityChildAdapter
            } else {
                binding.rlQueryCityList.gone()
                binding.notDataView.show()
            }
        }
    }

    /**
     *
     */
    fun queryCity(str: String): MutableList<CityChildItemInfo> {
        val list = ArrayList<CityChildItemInfo>()
        mAllCityList.forEach {
            if (it.mType == 0) {
                it.mCityItemInfo?.items?.forEach {
                    var startCityStr = ""
                    if (StringUtil.isChinese(str)) {//如果是汉字，去取城市名称
                        if (str.length <= it.cityName.length) {//防止输入字符超过城市紫字符串
                            startCityStr = it.cityName.substring(0, str.length)
                        } else {
                            startCityStr = it.cityName
                        }
                    } else {//否则去取城市拼音
                        if (str.length <= it.pinyin.length) {//防止输入字符超过城市紫字符串
                            startCityStr = it.pinyin.substring(0, str.length)
                        } else {
                            startCityStr = it.pinyin
                        }
                    }

                    if (str.toLowerCase().equals(startCityStr)
                        || str.toUpperCase()
                            .equals(startCityStr)
                    ) {//不区分大小写匹配
                        list.add(it)
                    }
                }
            }
        }
        return list
    }

    private fun showGoneList(show: Boolean) {
        if (show) {

            binding.cityList.show()

            binding.rlQeuryView.gone()
        } else {
            binding.cityList.gone()
            binding.rlQeuryView.show()
        }
    }

    override fun getData() {
        super.getData()
        mViewModel.getOpenCity()
        // mViewModel.getAllSort()
    }

    /**
     * 设置城市列表
     */
    fun bindCityList(list: MutableList<CityItemInfo>) {
        val zmList = ArrayList<String>()
        // zmList.add("当前历史热门")
        val cityList = ArrayList<CityInfo>()
        // cityList.add(CityInfo(1))
        list.forEach {
            zmList.add(it.name)
            cityList.add(CityInfo(0, it))
        }
        mAllCityList.clear()
        mAllCityList.addAll(cityList)
        val mCityListAdapter = CityListAdapter(mAllCityList, this)

        binding.cityList.layoutManager = LinearLayoutManager(this)
        binding.cityList.adapter = mCityListAdapter

        val mLetterAdapter = LetterAdapter(zmList)
        mLetterAdapter.setOnItemClickListener { adapter, view, position ->
            val scrolllIndex = position
            val mLinearLayoutManager = binding.cityList.getLayoutManager() as LinearLayoutManager
            mLinearLayoutManager.scrollToPositionWithOffset(scrolllIndex, 0)
        }

        binding.rvNavigationCity.layoutManager = LinearLayoutManager(this)
        binding.rvNavigationCity.adapter = mLetterAdapter

    }

    override fun initData() {
        getData()
        getCurrentLocation()
    }

    override fun onGetClassTypeNam(): Any {
        return "地区选择"
    }

    override fun onSelectCityLinsener(mCityChildItemInfo: CityChildItemInfo) {
        saveLocalAddress(LocalAddressInfo(mCityChildItemInfo.cityName, mCityChildItemInfo.cityId))
    }

    fun saveLocalAddress(mLocalAddressInfo: LocalAddressInfo) {
        LocalAddressManager.instance()
            .onSaveUserSelectCityInfo(mLocalAddressInfo)
        //去通知首页更新
        ObserverManger.getInstance(ObserverKey.HOME_INFO_UP)
            .notifyObserver(
                HomeUpDataInfo(
                    HomeUpDataInfo.UP_ADDRESS,
                    mLocalAddressInfo
                )
            )
        ObserverManger.getInstance(ObserverKey.LIFE_INFO_UP)
            .notifyObserver(
                HomeUpDataInfo(
                    HomeUpDataInfo.UP_ADDRESS,
                    mLocalAddressInfo
                )
            )
        finish()
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityAddresLoaclLayoutBinding.inflate(layoutInflater)
    }

}