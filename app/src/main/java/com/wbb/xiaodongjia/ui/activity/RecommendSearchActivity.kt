package com.wbb.xiaodongjia.ui.activity

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.wbb.base.ext.i18N
import com.wbb.base.ext.isEmpty
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.RecommendListAdapter
import com.wbb.base.bean.RecommendListBean
import com.wbb.base.util.*
import com.wbb.base.wiget.SearchInputView
import com.wbb.xiaodongjia.mvvm.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_recommend_search.*

/**
 * Created by hy on 2021/2/23.
 */
@Route(path = ARouterMap.RECOMMEND_SEARCH)
class RecommendSearchActivity : BaseDataActivityKt<UserViewModel>(), View.OnClickListener {
    var recommendListAdapter: RecommendListAdapter? = null
    var recommendSearchList: MutableList<RecommendListBean> = ArrayList()
    val pageCount = 10
    var pageIndex = 1
    var level = 1

    override fun onReloadData() {

    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun providerVMClass(): Class<UserViewModel>? {
        return UserViewModel::class.java
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_recommend_search
    }

    override fun initView() {
        level = intent.getIntExtra(ARouterMap.RECOMMEND_SEARCH_LEVEL, 1)
        rv_recommend.setHasFixedSize(true)
        rv_recommend.layoutManager = LinearLayoutManager(this)
        recommendListAdapter = RecommendListAdapter(recommendSearchList)
        rv_recommend.adapter = recommendListAdapter
        recommendListAdapter?.setEmptyView(R.layout.view_base_empty, R.mipmap.ic_no_recommendation, i18N(R.string.暂无推荐关系))

        startListener()
    }

    private fun startListener() {
        srl_search.setOnRefreshListener {
            pageIndex = 1
            getRecommendList()
            srl_search.finishRefresh(5000)
        }
        srl_search.setOnLoadMoreListener {
            pageIndex++
            getRecommendList()
            srl_search.finishLoadMore(5000)
        }
        siv_recommend.initSearch(i18N(R.string.输入搜索内容), true,
            object : SearchInputView.OnSearchInputLinsener {
                override fun onCancer(string: String) {
                    if (isEmpty(siv_recommend.getSearchText())) {
                        onBackPressedSupport()
                    } else {
                        siv_recommend.setSearchText("")
                    }
                }

                override fun onInputText(string: String) {
                    if (isEmpty(string)) {

                    }
                }

                override fun onUserClickSearch(string: String) {
                    if (TextUtils.isEmpty(string)) {
                        ToastUtils.showToash(i18N(R.string.请输入搜索内容))
                        return
                    }
                    pageIndex = 1
                    getRecommendList()
                }
            })
    }

    override fun initData() {

    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mRecommendListLiveData.observe(this@RecommendSearchActivity, Observer {
                if (pageIndex == 1) {
                    recommendSearchList.clear()
                    recommendSearchList.addAll(it)
                    recommendListAdapter?.setList(recommendSearchList)
                } else {
                    if (it != null && it.size > 0) {
                        recommendSearchList.addAll(it)
                        recommendListAdapter?.setList(recommendSearchList)
                        srl_search.finishLoadMore(300)
                    } else {
                        pageIndex--
                        srl_search.finishLoadMoreWithNoMoreData()
                    }
                }
            })
            mException.observe(this@RecommendSearchActivity, Observer {
                LogUtil.v(it.toString())
            })
        }
    }

    private fun getRecommendList() {
        var par = HashMap<String, Any?>()
        par["dateSort"] = 2
        par["incomeSort"] = 2
        par["level"] = level
        par["pageCount"] = pageCount
        par["pageIndex"] = pageIndex
        par["recommend"] = siv_recommend.getSearchText()
        mViewModel.getRecommendList(par)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }


    override fun onGetClassTypeNam(): Any {
        return "推荐搜索"
    }

}