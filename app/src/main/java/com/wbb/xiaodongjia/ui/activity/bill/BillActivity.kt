package com.wbb.xiaodongjia.ui.activity.bill

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.base.ext.i18N
import com.wbb.base.util.ARouterMap
import com.wbb.base.view.flycotablayout.listener.OnTabSelectListener
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.BillViewpagerAdapter
import com.wbb.xiaodongjia.mvvm.viewmodel.BillViewModel
import kotlinx.android.synthetic.main.activity_bill.*
import kotlinx.android.synthetic.main.layout_comment_toolbar.*

/**
 * Created by hy on 2021/2/2.
 */
@Route(path = ARouterMap.BILL)
class BillActivity : BaseDataActivityKt<BillViewModel>(), View.OnClickListener {
    var tabTitle: Array<String>? = null
    var tabTitleList: ArrayList<String> = ArrayList()
    var billViewpagerAdapter: BillViewpagerAdapter? = null

    override fun onReloadData() {

    }

    override fun onGetClassTypeNam(): Any {
        return "账单"
    }

    override fun providerVMClass(): Class<BillViewModel>? {
        return BillViewModel::class.java
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.activity_bill
    }

    override fun initView() {
        tv_title.text = i18N(R.string.东家账单)
        tabTitleList.add(i18N(R.string.已支付))
        tabTitle = arrayOf(i18N(R.string.已支付))
        billViewpagerAdapter = BillViewpagerAdapter(supportFragmentManager, tabTitleList)
        vp_bill.adapter = billViewpagerAdapter
        vp_bill.offscreenPageLimit = 1
        stl_bill.setViewPager(vp_bill, tabTitle)
        stl_bill.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                vp_bill.currentItem = position
            }

            override fun onTabReselect(position: Int) {

            }

        })
        iv_back.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressedSupport()
            }
        }
    }

    override fun onBackPressedSupport() {
        super.onBackPressedSupport()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}