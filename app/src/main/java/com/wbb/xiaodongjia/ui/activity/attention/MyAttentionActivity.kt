package com.wbb.xiaodongjia.ui.activity.attention

import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.wbb.base.adapter.FragmentAdapter
import com.wbb.base.ext.i18N
import com.wbb.base.util.ARouterMap
import com.wbb.base.util.MagicValue
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.emuns.ATTENTION_CLASS
import com.wbb.xiaodongjia.mvvm.viewmodel.AttentionViewModel
import com.wbb.xiaodongjia.ui.activity.attention.fragment.AttentionFragment
import kotlinx.android.synthetic.main.activit_my_attention_layout.*
import kotlinx.android.synthetic.main.activit_my_attention_layout.id_stickynavlayout_indicator
import kotlinx.android.synthetic.main.activit_my_attention_layout.id_stickynavlayout_viewpager
import kotlinx.android.synthetic.main.fragment_home_layout.*

/**
 * Created by zj on 2021/2/22.
 * 我的关注主界面
 */
@Route(path = ARouterMap.MY_ATTENTION)
class MyAttentionActivity : BaseDataActivityKt<AttentionViewModel>() {
    override fun onReloadData() {
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = true

    override fun providerVMClass(): Class<AttentionViewModel>? {
        return AttentionViewModel::class.java
    }

    override fun getLayoutResId(): Int {
        return R.layout.activit_my_attention_layout
    }

    override fun initView() {
        setTitleName(i18N(R.string.my_attention))
        search_toolbar.setOnClickListener {
            ARouter.getInstance().build(ARouterMap.SEARCH).withString(ARouterMap.SEARCH_TYPE, MagicValue.SEARCH_TYPE_CLASS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
        }
        val tabNameList = arrayOfNulls<String>(size = 3)
        tabNameList[0] = ATTENTION_CLASS.MERCHANT.mName
        tabNameList[1] = ATTENTION_CLASS.CLASSROOM.mName
        tabNameList[2] = ATTENTION_CLASS.ACTIVITY.mName
        val fmList = ArrayList<Fragment>()
        fmList.add(AttentionFragment.getAttentionFragment(ATTENTION_CLASS.MERCHANT.mType))
        fmList.add(AttentionFragment.getAttentionFragment(ATTENTION_CLASS.CLASSROOM.mType))
        fmList.add(AttentionFragment.getAttentionFragment(ATTENTION_CLASS.ACTIVITY.mType))

        val mFragmentAdapter = FragmentAdapter(supportFragmentManager, fmList)
        id_stickynavlayout_viewpager.adapter = mFragmentAdapter
        id_stickynavlayout_viewpager.setOffscreenPageLimit(3)
        id_stickynavlayout_indicator.setViewPager(id_stickynavlayout_viewpager, tabNameList)
    }


    override fun initData() {
    }

    override fun onGetClassTypeNam(): Any {
        return "我的关注界面"
    }
}