package com.wbb.xiaodongjia.ui.activity.attention.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.chouyou.waterbridge.base.BaseDataFragmentKt
import com.wbb.base.bean.MerchantFolownfo
import com.wbb.base.bean.MerchantInfo
import com.wbb.base.help.AppToH5Manager
import com.wbb.base.help.OnItemClickLinsener
import com.wbb.base.util.ARouterMap
import com.wbb.base.util.MagicValue
import com.wbb.base.util.ToastUtils
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.MyAttentionAdapter
import com.wbb.xiaodongjia.emuns.ATTENTION_CLASS
import com.wbb.xiaodongjia.mvvm.viewmodel.AttentionViewModel
import com.wbb.xiaodongjia.ui.activity.video.VideoPlayActivity
import kotlinx.android.synthetic.main.fragemnt_attention_layout.*

/**
 * Created by zj on 2021/2/22.
 */
class AttentionFragment : BaseDataFragmentKt<AttentionViewModel>(),
    OnItemClickLinsener<MerchantFolownfo> {
    private var page = 1
    private var mType: Int = ATTENTION_CLASS.MERCHANT.mType
    private var mMyAttentionAdapter: MyAttentionAdapter? = null
    private var mAllList = ArrayList<MerchantFolownfo>()

    companion object {
        const val ATTENTION_TYPE = "attention_type"
        fun getAttentionFragment(mType: Int = 1): AttentionFragment {
            val mAttentionFragment = AttentionFragment()
            val mBuild = Bundle()
            mBuild.putInt(ATTENTION_TYPE, mType)
            mAttentionFragment.arguments = mBuild
            return mAttentionFragment
        }
    }

    override fun providerVMClass(): Class<AttentionViewModel>? {
        return AttentionViewModel::class.java
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mFollowList.observe(this@AttentionFragment, {
                bindAdapter(it.list, it.total)
            })
            errMsg.observe(this@AttentionFragment, {
                ToastUtils.showErrorToast(it.msg)
                showNotDataView()
            })
        }
    }

    /**
     * 绑定数据
     */
    fun bindAdapter(list: MutableList<MerchantFolownfo>?, total: Int = 0) {
        sl_ref_list.finishLoadMore()
        sl_ref_list.finishRefresh()
        if ((list == null || list.size <= 0) && page == 1) {
            showNotDataView()
        }
        if (total < 10) {
            sl_ref_list.finishLoadMoreWithNoMoreData()
        }
        if (list != null) {
            if (page == 1) {
                mAllList.clear()
            }
            mAllList.addAll(list)
            if (mMyAttentionAdapter == null) {
                mMyAttentionAdapter = MyAttentionAdapter(mAllList, mType)
                mMyAttentionAdapter?.setOnItemClickLinsener(this)
                rl_att_list.layoutManager = LinearLayoutManager(requireContext())
                rl_att_list.adapter = mMyAttentionAdapter
            } else {
                mMyAttentionAdapter?.setList(mAllList)
            }
        }

    }

    override fun onReloadData() {
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.fragemnt_attention_layout
    }

    override fun getData() {
        super.getData()
        val par = HashMap<String, String>()
        par["page"] = page.toString()
        par["mid"] = ""
        par["pageSize"] = "10"
        par["type"] = mType.toString()
        mViewModel.getFollowList(par)
    }

    override fun initView() {
        sl_ref_list.setOnRefreshListener {
            page = 1
            sl_ref_list
            getData()
        }
        sl_ref_list.setOnLoadMoreListener {
            page++
            getData()
        }
    }

    override fun initData() {
        mType = arguments!!.getInt(ATTENTION_TYPE)

    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    override fun onGetClassTypeNam(): Any {
        return "关注子fragemnt"
    }

    override fun onItemClick(postion: Int, mInfo: MerchantFolownfo?) {
        when (mType) {
            ATTENTION_CLASS.MERCHANT.mType -> {//商家
                mInfo?.let {
                    AppToH5Manager.toMerchantDetails(it.merchantSearchVO!!.merchantStoreId)
                }

            }
            ATTENTION_CLASS.ACTIVITY.mType -> {//活动
                mInfo.let {
                    AppToH5Manager.toActivityDetails(it!!.activity!!.activityId)
                }

            }
            ATTENTION_CLASS.CLASSROOM.mType -> {//课程

                ARouter.getInstance().build(ARouterMap.VIDEO_PLAY)
                    .withString(ARouterMap.COURSE_ID, mInfo!!.course!!.courseId).withString(
                        ARouterMap.ViDEO_COURSE_DETAIL,
                        VideoPlayActivity.COURSE_SING
                    ).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    ).navigation()
            }
        }
    }
}