package com.wbb.xiaodongjia.ui.activity.bill

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.wbb.base.bean.PayDetail
import com.wbb.base.ext.i18N
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.base.util.ARouterMap
import com.wbb.base.util.GlideUtil
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.BillDetailAdapter
import com.wbb.base.util.LogUtil
import com.wbb.base.util.StringUtil
import com.wbb.xiaodongjia.mvvm.viewmodel.BillViewModel
import kotlinx.android.synthetic.main.activity_bill_detail.*
import kotlinx.android.synthetic.main.layout_comment_toolbar.*

/**
 * Created by hy on 2021/2/3.
 */
@Route(path = ARouterMap.BILL_DETAIL)
class BillDetailActivity : BaseDataActivityKt<BillViewModel>(), View.OnClickListener {
    var billDetailAdapter: BillDetailAdapter? = null
    var billDetailList: MutableList<PayDetail> = ArrayList()
    var orderId = ""
    var sizes = intArrayOf(18, 28)
    var colors = intArrayOf(R.color.color_ff2d2e32, R.color.color_ff2d2e32)

    override fun onGetClassTypeNam(): Any {
        return "订单详情"
    }

    override fun onReloadData() {

    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun providerVMClass(): Class<BillViewModel>? {
        return BillViewModel::class.java
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_bill_detail
    }

    override fun initView() {
        orderId = intent.getStringExtra(ARouterMap.BILL_ID).toString()
        tv_title.text = i18N(R.string.订单详情)

        rv_billDetail.setHasFixedSize(true)
        rv_billDetail.layoutManager = LinearLayoutManager(this)
        billDetailAdapter = BillDetailAdapter(billDetailList)
        rv_billDetail.adapter = billDetailAdapter

        iv_back.setOnClickListener(this)
        rtv_goPay.setOnClickListener(this)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mBillDetailLiveData.observe(this@BillDetailActivity, Observer {
                tv_billType.text = it.tips
                val strings = arrayOf("¥", it.amount)
                tv_billAmount.text = StringUtil.getSpan(strings, sizes, colors)
                when (it.status) {
                    2 -> {
                        GlideUtil.loadImagePreview(R.mipmap.ic_bill_paid, iv_billStatus)
                    }
                }
                billDetailList.addAll(it.payDetails)
                billDetailAdapter?.setList(billDetailList)
            })
            mException.observe(this@BillDetailActivity, Observer {
                LogUtil.v(it.toString())
            })
        }
    }

    override fun initData() {
        var par = HashMap<String, String>()
        par["orderId"] = orderId
        mViewModel.getOrderDetail(par)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressedSupport()
            }
            R.id.rtv_goPay -> {

            }
        }
    }

}