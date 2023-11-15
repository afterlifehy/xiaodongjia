package com.wbb.xiaodongjia.ui.fragment

import android.content.Intent
import android.util.ArrayMap
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.chouyou.waterbridge.base.BaseDataFragmentKt
import com.wbb.base.BaseApplication
import com.wbb.base.ext.i18N
import com.wbb.base.util.ARouterMap
import com.wbb.base.util.GsonUtils
import com.wbb.base.util.LogUtil
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.BillListAdapter
import com.wbb.base.bean.BillListBean
import com.wbb.xiaodongjia.mvvm.viewmodel.BillViewModel
import kotlinx.android.synthetic.main.fragment_bill_list.*

/**
 * Created by hy on 2021/2/2.
 */
class BillListFragment : BaseDataFragmentKt<BillViewModel>(), View.OnClickListener {
    var billListAdapter: BillListAdapter? = null
    var billList: MutableList<BillListBean> = ArrayList()
    var type = 0
    var pageIndex = 1
    var pageSize = 10
    var statusStringMap: MutableMap<String, String> = ArrayMap()

    init {
//        statusStringMap[0] = i18N(R.string.)
    }

    override fun onReloadData() {

    }

    override fun onGetClassTypeNam(): Any {
        return "账单列表"
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun providerVMClass(): Class<BillViewModel>? {
        return BillViewModel::class.java
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_bill_list
    }

    override fun initView() {
        type = arguments?.getInt("type", 0)!!

        rv_bill.setHasFixedSize(true)
        rv_bill.layoutManager = LinearLayoutManager(BaseApplication.instance())
        billListAdapter = BillListAdapter(billList, this)
        rv_bill.adapter = billListAdapter
        billListAdapter?.setEmptyView(R.layout.view_base_empty, R.mipmap.ic_no_trade, i18N(R.string.暂无交易记录))

        srl_bill.setOnRefreshListener {
            pageIndex = 1
            getOrder()
            srl_bill.finishRefresh(5000)
        }
        srl_bill.setOnLoadMoreListener {
            pageIndex++
            getOrder()
            srl_bill.finishLoadMore(5000)
        }

    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mBillListLiveData.observe(this@BillListFragment, Observer {
                var tempList = it.list
                if (pageIndex == 1) {
                    billList.clear()
                    billList.addAll(tempList)
                    billListAdapter?.setList(billList)
                } else {
                    if (tempList != null && tempList.size > 0) {
                        billList.addAll(tempList)
                        billListAdapter?.setList(billList)
                        srl_bill.finishLoadMore(300)
                    } else {
                        pageIndex--
                        srl_bill.finishLoadMoreWithNoMoreData()
                    }
                }
            })
            mException.observe(this@BillListFragment, Observer {
                LogUtil.v(it.toString())
            })
        }
    }

    override fun initData() {
        getOrder()
    }

    private fun getOrder() {
        var par = HashMap<String, Any>()
        par["queryType"] = 1
        par["page"] = pageIndex
        par["pageSize"] = pageSize
        par["status"] = 2
        mViewModel.getOrder(par)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rll_bill -> {
                val billListBean = v.tag as BillListBean
                ARouter.getInstance().build(ARouterMap.BILL_DETAIL).withString(ARouterMap.BILL_ID, billListBean.orderId).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

}