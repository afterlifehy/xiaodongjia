package com.wbb.xiaodongjia.ui.activity

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.wbb.base.BaseApplication
import com.wbb.base.ext.i18N
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.RecommendListAdapter
import com.wbb.base.bean.RecommendListBean
import com.wbb.base.util.*
import com.wbb.xiaodongjia.mvvm.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_recommend.*
import kotlinx.android.synthetic.main.layout_comment_toolbar.*

/**
 * Created by hy on 2021/2/23.
 */
@Route(path = ARouterMap.RECOMMEND)
class RecommendListActivity : BaseDataActivityKt<UserViewModel>(), View.OnClickListener {
    var recommendListAdapter: RecommendListAdapter? = null
    var type = 1
    var pageCount = 10
    var pageIndex = 1
    var recommendList: MutableList<RecommendListBean> = ArrayList()
    var dateSort = 2
    var incomeSort = 2

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
        return R.layout.activity_recommend
    }

    override fun initView() {
        type = intent.getIntExtra(ARouterMap.RECOMMEND_TYPE, 1)
        when (type) {
            1 -> {
                tv_title.text = i18N(R.string.直接推荐)
            }
            2 -> {
                tv_title.text = i18N(R.string.间接推荐)
            }
        }
        rv_recommend.setHasFixedSize(true)
        rv_recommend.layoutManager = LinearLayoutManager(BaseApplication.instance())
        recommendListAdapter = RecommendListAdapter(recommendList)
        rv_recommend.adapter = recommendListAdapter
        recommendListAdapter?.setEmptyView(R.layout.view_base_empty, R.mipmap.ic_no_recommendation, i18N(R.string.暂无推荐关系))

        srl_recommend.setOnRefreshListener {
            pageIndex = 1
            getRecommendList()
            srl_recommend.finishRefresh(2000)
        }
        srl_recommend.setOnLoadMoreListener {
            pageIndex++
            getRecommendList()
            srl_recommend.finishLoadMore(2000)
        }
        startListener()
    }

    private fun startListener() {
        iv_back.setOnClickListener(this)
        rll_search.setOnClickListener(this)
        ll_dateSort.setOnClickListener(this)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mRecommendListLiveData.observe(this@RecommendListActivity, Observer {
                if (pageIndex == 1) {
                    recommendList.clear()
                    recommendList.addAll(it)
                    recommendListAdapter?.setList(recommendList)
                } else {
                    if (it != null && it.size > 0) {
                        recommendList.addAll(it)
                        recommendListAdapter?.setList(recommendList)
                        srl_recommend.finishLoadMore(300)
                    } else {
                        pageIndex--
                        srl_recommend.finishLoadMoreWithNoMoreData()
                    }
                }
            })
            mException.observe(this@RecommendListActivity, Observer {
                LogUtil.v(it.toString())
            })
        }
    }

    override fun initData() {
        getRecommendList()
    }

    private fun getRecommendList() {
        var par = HashMap<String, Any?>()
        par["dateSort"] = dateSort
        par["incomeSort"] = incomeSort
        par["level"] = type
        par["pageCount"] = pageCount
        par["pageIndex"] = pageIndex
        par["tenantCode"] = ""
        mViewModel.getRecommendList(par)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressedSupport()
            }
            R.id.rll_search -> {
                ARouter.getInstance().build(ARouterMap.RECOMMEND_SEARCH).withInt(ARouterMap.RECOMMEND_SEARCH_LEVEL, type).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
            R.id.ll_dateSort -> {
                pageIndex = 1
                when (dateSort) {
                    1 -> {
                        dateSort = 2
                        GlideUtil.loadImagePreview(R.mipmap.ic_sort_arrow_down, iv_dateArrow)
                    }
                    2 -> {
                        dateSort = 1
                        GlideUtil.loadImagePreview(R.mipmap.ic_sort_arrow_up, iv_dateArrow)
                    }
                }
                getRecommendList()
            }
        }
    }

    override fun onGetClassTypeNam(): Any {
        return "推荐列表"
    }

}