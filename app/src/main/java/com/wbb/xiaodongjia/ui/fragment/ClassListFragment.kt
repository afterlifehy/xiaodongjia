package com.wbb.xiaodongjia.ui.fragment

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.chouyou.waterbridge.base.BaseDataFragmentKt
import com.wbb.base.BaseApplication
import com.wbb.base.bean.DongClassBean
import com.wbb.base.ext.i18N
import com.wbb.base.util.ARouterMap
import com.wbb.base.util.LogUtil
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.DongClassAdapter
import com.wbb.xiaodongjia.mvvm.viewmodel.DongClassViewModel
import com.wbb.xiaodongjia.ui.activity.video.VideoPlayActivity
import kotlinx.android.synthetic.main.fragment_dong_class_list.*

/**
 * Created by hy on 2021/1/28.
 */
class ClassListFragment : BaseDataFragmentKt<DongClassViewModel>(), View.OnClickListener {
    var dongClassAdapter: DongClassAdapter? = null
    var classList: MutableList<DongClassBean> = ArrayList()
    var courseType = "DEFAULT"
    var page = 1
    var pageSize = 10

    override fun onReloadData() {

    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun providerVMClass(): Class<DongClassViewModel>? {
        return DongClassViewModel::class.java
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_dong_class_list
    }

    override fun initView() {
        courseType = arguments?.getString("type").toString()

        rv_class.isNestedScrollingEnabled = false
        rv_class.setHasFixedSize(true)
        rv_class.layoutManager = LinearLayoutManager(BaseApplication.instance())
        dongClassAdapter = DongClassAdapter(classList, this)
        rv_class.adapter = dongClassAdapter
        dongClassAdapter?.setEmptyView(
            R.layout.view_base_empty,
            R.mipmap.ic_no_search,
            i18N(R.string.暂无课程)
        )

        srl_class.setOnRefreshListener {
            page = 1
            getData()
            srl_class.finishRefresh(1000)
        }
        srl_class.setOnLoadMoreListener {
            page++
            getData()
            srl_class.finishLoadMore(1000)
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mDongClassLiveData.observe(this@ClassListFragment, Observer {
                if (page == 1) {
                    classList.clear()
                    classList.addAll(it.list)
                    dongClassAdapter?.setList(classList)
                } else {
                    if (it.list != null && it.list.size > 0) {
                        classList.addAll(it.list)
                        dongClassAdapter?.setList(classList)
                        srl_class.finishLoadMore(300)
                    } else {
                        page--
                        srl_class.finishLoadMoreWithNoMoreData()
                    }
                }
            })
            mException.observe(this@ClassListFragment, Observer {
                LogUtil.v(it.toString())
            })
        }
    }

    override fun initData() {
        getData()
    }

    override fun getData() {
        super.getData()
        courseTypeSearch()
    }

    private fun courseTypeSearch() {
        var par = HashMap<String, Any>()
        par["courseType"] = courseType
        par["page"] = page
        par["pageSize"] = pageSize
        mViewModel.courseTypeSearch(par)
    }

    override fun onGetClassTypeNam(): Any {
        return ""
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ll_class -> {
                val dongClassBean = v.tag as DongClassBean
                ARouter.getInstance().build(ARouterMap.VIDEO_PLAY)
                    .withString(ARouterMap.COURSE_ID, dongClassBean.courseTypeId).withString(
                        ARouterMap.ViDEO_COURSE_DETAIL,
                        VideoPlayActivity.COURSE_LIST
                    )
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

}