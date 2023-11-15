package com.wbb.xiaodongjia.ui.fragment

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.chouyou.waterbridge.base.BaseDataFragmentKt
import com.wbb.base.BaseApplication
import com.wbb.base.bean.DongClassBean
import com.wbb.base.bean.SearchResultBean
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.ext.i18n
import com.wbb.base.util.ARouterMap
import com.wbb.base.util.Constant
import com.wbb.base.util.LogUtil
import com.wbb.base.util.MagicValue
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.SearchResultAdapter
import com.wbb.xiaodongjia.event.RefreshSearchEvent
import com.wbb.xiaodongjia.mvvm.viewmodel.SearchViewModel
import com.wbb.xiaodongjia.ui.activity.video.VideoPlayActivity
import com.wbb.xiaodongjia.utils.LocalAddressManager
import kotlinx.android.synthetic.main.fragment_search_result.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by hy on 2021/2/25.
 */
class SearchResultFragment : BaseDataFragmentKt<SearchViewModel>(), View.OnClickListener {
    var searchResultAdapter: SearchResultAdapter? = null
    var searchResultList: MutableList<SearchResultBean> = ArrayList()
    var searchType = ""
    var pageIndex = 1
    var pageSize = 10
    var searchTxt = ""

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onEvent(refreshSearchEvent: RefreshSearchEvent) {
        pageIndex = 1
        searchTxt = refreshSearchEvent.searchText
        searchResultList.clear()
        searchAll()
    }

    override fun isRegEventBus(): Boolean {
        return true
    }

    override fun onReloadData() {

    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.fragment_search_result
    }

    override fun providerVMClass(): Class<SearchViewModel>? {
        return SearchViewModel::class.java
    }

    override fun initView() {
        searchType = arguments?.getString("searchType").toString()
        searchTxt = arguments?.getString("searchTxt").toString()

        rv_search.setHasFixedSize(true)
        rv_search.layoutManager = LinearLayoutManager(BaseApplication.instance())
        searchResultAdapter = SearchResultAdapter(searchResultList, this)
        rv_search.adapter = searchResultAdapter
        searchResultAdapter?.setEmptyView(
            R.layout.view_base_empty,
            R.mipmap.ic_no_search,
            i18n(R.string.搜索无结果)
        )

        srl_search.setOnRefreshListener {
            pageIndex = 1
            searchAll()
            srl_search.finishRefresh(5000)
        }
        srl_search.setOnLoadMoreListener {
            pageIndex++
            searchAll()
            srl_search.finishLoadMore(5000)
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mSearchResultLiveData.observe(this@SearchResultFragment, Observer {
                if (pageIndex == 1) {
                    searchResultList.clear()
                    searchResultList.addAll(it.list)
                    searchResultAdapter?.setList(searchResultList)
                    srl_search.finishRefresh()
                } else {
                    if (it.list != null && it.list.size > 0) {
                        searchResultList.addAll(it.list)
                        searchResultAdapter?.setList(searchResultList)
                        srl_search.finishLoadMore(300)
                    } else {
                        pageIndex--
                        srl_search.finishLoadMoreWithNoMoreData()
                    }
                }
            })
            mException.observe(this@SearchResultFragment, Observer {
                LogUtil.v(it.toString())
            })
        }
    }

    override fun initData() {
        searchAll()
    }

    private fun searchAll() {
        val par = HashMap<String, Any>()
        par["searchType"] = searchType
        par["input"] = searchTxt
        par["cityId"] = LocalAddressManager.instance().getCityId()
        par["page"] = pageIndex
        par["pageSize"] = pageSize
        mViewModel.searchAll(par)
    }

    override fun onGetClassTypeNam(): Any {
        return "搜索结果"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rl_search -> {
                val searchResultBean = v?.tag as SearchResultBean
                when (searchResultBean.searchType) {
                    MagicValue.SEARCH_TYPE_BUSINESS -> {
                        ARouter.getInstance().build(ARouterMap.WEBVIEW).withString(
                            ARouterMap.URL,
                            Constant.MERCHANT_DETAIL + "?id=${searchResultBean.id}"
                        )
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .navigation()
                        OnBuriedPointManager.get().getOnBuriedPointManager()?.SearchResultView(
                            "商户",
                            searchResultBean.name,
                            Constant.MERCHANT_DETAIL + "?id=${searchResultBean.id}"
                        )
                    }
                    MagicValue.SEARCH_TYPE_ACTIVITY -> {
                        ARouter.getInstance().build(ARouterMap.WEBVIEW).withString(
                            ARouterMap.URL,
                            Constant.ACTIVITY_DETAIL + "?id=${searchResultBean.id}"
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .navigation()
                        OnBuriedPointManager.get().getOnBuriedPointManager()?.SearchResultView(
                            "活动",
                            searchResultBean.name,
                            Constant.ACTIVITY_DETAIL + "?id=${searchResultBean.id}"
                        )
                    }
                    MagicValue.SEARCH_TYPE_CLASS -> {
                        ARouter.getInstance().build(ARouterMap.VIDEO_PLAY)
                            .withString(ARouterMap.COURSE_ID, searchResultBean.id).withString(
                                ARouterMap.ViDEO_COURSE_DETAIL,
                                VideoPlayActivity.COURSE_SING
                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                        OnBuriedPointManager.get().getOnBuriedPointManager()
                            ?.SearchResultView("课程", searchResultBean.name, searchResultBean.id)
                    }
                }
            }
        }
    }
}