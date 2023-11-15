package com.wbb.xiaodongjia.ui.activity

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.aries.ui.view.radius.RadiusTextView
import com.chouyou.base.ext.gone
import com.chouyou.base.ext.show
import com.google.android.exoplayer2.util.Log
import com.wbb.base.BaseApplication
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.dialog.ClearSearchHistoryDialog
import com.wbb.base.ext.i18N
import com.wbb.base.ext.isEmpty
import com.wbb.base.pf.PreferencesKeys
import com.wbb.base.pf.StoreFactory
import com.wbb.base.util.*
import com.wbb.base.view.flycotablayout.listener.OnTabSelectListener
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.base.wiget.SearchInputView
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.SearchViewPagerAdapter
import com.wbb.xiaodongjia.event.RefreshSearchEvent
import com.wbb.xiaodongjia.mvvm.viewmodel.SearchViewModel
import com.wbb.xiaodongjia.utils.LocalAddressManager
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
 * Created by hy on 2021/2/1.
 */
@Route(path = ARouterMap.SEARCH)
class SearchActivity : BaseDataActivityKt<SearchViewModel>(), View.OnClickListener {
    var type = ""
    var hotSearchTagAdapter: TagAdapter<String>? = null
    var historySearchTagAdapter: TagAdapter<String>? = null
    var hotSearchTagList: MutableList<String> = ArrayList()
    var historySearchTagList: MutableList<String> = ArrayList()
    var clearSearchHistoryDialog: ClearSearchHistoryDialog? = null
    var tabTitle: Array<String>? = null
    var tabTitleList: ArrayList<String> = ArrayList()
    var searchViewPagerAdapter: SearchViewPagerAdapter? = null
    var searchHistory = ""
    override fun isRegEventBus(): Boolean {
        return true
    }

    override fun onReloadData() {

    }

    override fun onGetClassTypeNam(): Any {
        return "搜索"
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun providerVMClass(): Class<SearchViewModel>? {
        return SearchViewModel::class.java
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_search
    }

    override fun initView() {
        type = intent.getStringExtra(ARouterMap.SEARCH_TYPE).toString()
        search_input.initSearch(
            i18N(R.string.输入搜索内容),
            true,
            object : SearchInputView.OnSearchInputLinsener {
                override fun onCancer(string: String) {
                    if (isEmpty(string)) {
                        onBackPressedSupport()
                    }
                }

                override fun onInputText(string: String) {
                    if (isEmpty(string)) {
                        ll_searchResult.gone()
                        ll_searchHot.show()
                    }
                }

                override fun onUserClickSearch(string: String) {
                    if (!TextUtils.isEmpty(string)) {
                        ll_searchResult.show()
                        rl_historySearch.show()
                        ll_searchHot.gone()
                        count()
                        searchViewPagerAdapter?.setSearchTxt(string)
                        if (!isContainHistory(string)) {
                            if (searchHistory.isEmpty()) {
                                searchHistory = string
                            } else {
                                searchHistory += ",${string}"
                            }
                            GlobalScope.launch(Dispatchers.Main) {
                                StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                                    .putString(
                                        PreferencesKeys.SEARCH_HISTORY, searchHistory
                                    )
                            }
                            historySearchTagList.clear()
                            val tempList = ArrayList<String>()
                            tempList.addAll(searchHistory.split(","))
                            tempList.reverse()
                            if (tempList.size > 6) {
                                historySearchTagList.addAll(tempList.subList(0, 6))
                            } else {
                                historySearchTagList.addAll(tempList)
                            }
                            historySearchTagAdapter?.notifyDataChanged()
                        }
                        EventBus.getDefault().post(RefreshSearchEvent(search_input.getSearchText()))
                        when (type) {
                            MagicValue.SEARCH_TYPE_BUSINESS -> {
                                OnBuriedPointManager.get().getOnBuriedPointManager()
                                    ?.AppSearch(string, 0, 0)
                            }
                            MagicValue.SEARCH_TYPE_CLASS -> {
                                OnBuriedPointManager.get().getOnBuriedPointManager()
                                    ?.CourseSearch(string, 0, 0)
                            }
                        }
                    }
                }
            })

        GlobalScope.launch(Dispatchers.Main) {
            searchHistory =
                StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                    .getString(PreferencesKeys.SEARCH_HISTORY)
            if (searchHistory.isNotEmpty()) {
                rl_historySearch.show()
                historySearchTagList.addAll(searchHistory.split(","))
                historySearchTagList.reverse()
                if (historySearchTagList.size > 6) {
                    historySearchTagList = historySearchTagList.subList(0, 6)
                }
            } else {
                rl_historySearch.gone()
            }
            historySearchTagAdapter = object : TagAdapter<String>(historySearchTagList) {
                override fun getView(parent: FlowLayout?, position: Int, t: String?): View {
                    val tv =
                        layoutInflater.inflate(R.layout.item_hot_search, null) as RadiusTextView
                    tv.text = t
                    return tv
                }
            }
            rfl_historySearch.adapter = historySearchTagAdapter
            rfl_historySearch.setOnTagClickListener { view, position, parent ->
                searchViewPagerAdapter?.setSearchTxt(historySearchTagList[position])
                search_input.setSearchText(historySearchTagList[position])
                ll_searchResult.show()
                ll_searchHot.gone()
                count()
                EventBus.getDefault().post(RefreshSearchEvent(historySearchTagList[position]))
                when (type) {
                    MagicValue.SEARCH_TYPE_BUSINESS -> {
                        OnBuriedPointManager.get().getOnBuriedPointManager()
                            ?.AppSearch(historySearchTagList[position], 0, 1)
                    }
                    MagicValue.SEARCH_TYPE_CLASS -> {
                        OnBuriedPointManager.get().getOnBuriedPointManager()
                            ?.CourseSearch(historySearchTagList[position], 0, 1)
                    }
                }
                true
            }

        }

        tabTitleList.add("全部")
        tabTitleList.add("商户")
        tabTitleList.add("课程")
        tabTitleList.add("活动")
        tabTitle = arrayOf("全部", "商户", "课程", "活动")
        searchViewPagerAdapter = SearchViewPagerAdapter(supportFragmentManager, tabTitleList)
        vp_search.adapter = searchViewPagerAdapter
        vp_search.offscreenPageLimit = 3
        stl_search.setViewPager(vp_search, tabTitle)
        stl_search.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                vp_search.currentItem = position
            }

            override fun onTabReselect(position: Int) {

            }

        })
        iv_clearHistory.setOnClickListener(this)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mCountLiveData.observe(this@SearchActivity, Observer {
                tabTitle = arrayOf(
                    "全部(${it.allCount})",
                    "商户(${it.merchantCount})",
                    "课程(${it.courseCount})",
                    "活动(${it.activityCount})"
                )
                stl_search.setViewPager(vp_search, tabTitle)
                when (type) {
                    MagicValue.SEARCH_TYPE_BUSINESS -> {
                        vp_search.currentItem = 1
                    }
                    MagicValue.SEARCH_TYPE_CLASS -> {
                        vp_search.currentItem = 2
                    }
                }
            })
            mhotSearchLiveData.observe(this@SearchActivity, Observer {
                hotSearchTagList.clear()
                hotSearchTagList.addAll(
                    it.replace("[", "").replace("]", "").replace("\"", "").split(",")
                )
                hotSearchTagAdapter = object : TagAdapter<String>(hotSearchTagList) {
                    override fun getView(parent: FlowLayout?, position: Int, t: String?): View {
                        val tv =
                            layoutInflater.inflate(R.layout.item_hot_search, null) as RadiusTextView
                        tv.text = t
                        return tv
                    }
                }
                tfl_hotSearch.adapter = hotSearchTagAdapter
                tfl_hotSearch.setOnTagClickListener { view, position, parent ->
                    searchViewPagerAdapter?.setSearchTxt(hotSearchTagList[position])
                    search_input.setSearchText(hotSearchTagList[position])
                    if (!isContainHistory(hotSearchTagList[position])) {
                        if (searchHistory.isEmpty()) {
                            searchHistory = hotSearchTagList[position]
                        } else {
                            searchHistory += ",${hotSearchTagList[position]}"
                        }
                        GlobalScope.launch(Dispatchers.Main) {
                            StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                                .putString(PreferencesKeys.SEARCH_HISTORY, searchHistory)
                        }
                        historySearchTagList.clear()
                        val tempList = ArrayList<String>()
                        tempList.addAll(searchHistory.split(","))
                        tempList.reverse()
                        if (tempList.size > 6) {
                            historySearchTagList.addAll(tempList.subList(0, 6))
                        } else {
                            historySearchTagList.addAll(tempList)
                        }
                        historySearchTagAdapter?.notifyDataChanged()
                    }
                    count()
                    EventBus.getDefault().post(RefreshSearchEvent(hotSearchTagList[position]))
                    when (type) {
                        MagicValue.SEARCH_TYPE_BUSINESS -> {
                            OnBuriedPointManager.get().getOnBuriedPointManager()
                                ?.AppSearch(hotSearchTagList[position], 1, 0)
                        }
                        MagicValue.SEARCH_TYPE_CLASS -> {
                            OnBuriedPointManager.get().getOnBuriedPointManager()
                                ?.CourseSearch(hotSearchTagList[position], 1, 0)
                        }
                    }
                    ll_searchResult.show()
                    rl_historySearch.show()
                    ll_searchHot.gone()
                    true
                }
            })
            mException.observe(this@SearchActivity, Observer {
                LogUtil.v(it.toString())
            })
        }
    }

    override fun initData() {
        getData()
    }

    override fun getData() {
        super.getData()
        getKeyList()
    }

    fun getKeyList() {
        val par = HashMap<String, String>()
        mViewModel.getKeyList(par)
    }

    fun count() {
        val par = HashMap<String, String>()
        par["input"] = search_input.getSearchText()
        par["cityId"] = LocalAddressManager.instance().getCityId()
        mViewModel.count(par)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_clearHistory -> {
                clearSearchHistoryDialog = ClearSearchHistoryDialog(
                    this,
                    object : ClearSearchHistoryDialog.ClearSearchHistoryCallback {
                        override fun makeSure() {
                            GlobalScope.launch(Dispatchers.Main) {
                                StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                                    .putString(PreferencesKeys.SEARCH_HISTORY, "")
                            }
                            historySearchTagList.clear()
                            historySearchTagAdapter?.notifyDataChanged()
                            rl_historySearch.gone()
                        }

                    })
                clearSearchHistoryDialog?.show()
            }
        }
    }

    fun isContainHistory(s: String): Boolean {
        for (i in historySearchTagList) {
            if (TextUtils.equals(s, i)) {
                return true
            }
        }
        return false
    }

    override fun onBackPressedSupport() {
        super.onBackPressedSupport()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}