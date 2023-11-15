package com.wbb.xiaodongjia.ui.activity

import android.content.Intent
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.aries.ui.util.StatusBarUtil
import com.wbb.base.bean.CardPackageBean
import com.wbb.base.ext.i18N
import com.wbb.base.util.*
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.CardPackageListAdapter
import com.wbb.xiaodongjia.mvvm.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_card_package.*
import kotlinx.android.synthetic.main.layout_comment_toolbar.*

/**
 * Created by hy on 2021/2/22.
 */
@Route(path = ARouterMap.CARD_PACKAGE)
class CardPackageActivity : BaseDataActivityKt<UserViewModel>(), View.OnClickListener {
    var cardPackageListAdapter: CardPackageListAdapter? = null
    var cardList: MutableList<CardPackageBean> = ArrayList()

    override fun onReloadData() {
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.activity_card_package
    }

    override fun providerVMClass(): Class<UserViewModel>? {
        return UserViewModel::class.java
    }

    override fun initView() {
        StatusBarUtils.setTranslucentStatus(this)
        abl_toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        val lp = abl_toolbar.layoutParams as RelativeLayout.LayoutParams
        lp.topMargin = StatusBarUtil.getStatusBarHeight()
        abl_toolbar.layoutParams = lp

        GlideUtil.loadImage(R.mipmap.ic_arrow_back_white_base, iv_back)
        tv_title.text = i18N(R.string.东家会员卡)
        tv_title.setTextColor(ContextCompat.getColor(this, R.color.white_txt))

        rv_card.setHasFixedSize(true)
        rv_card.layoutManager = LinearLayoutManager(this)
        cardPackageListAdapter = CardPackageListAdapter(cardList, this)
        rv_card.adapter = cardPackageListAdapter
        cardPackageListAdapter?.setEmptyView(R.layout.view_base_empty, R.mipmap.ic_no_collection, i18N(R.string.暂无会员卡))

        srl_card.setOnRefreshListener {
            cardList.clear()
            getData()
            srl_card.finishRefresh(2000)
        }
        srl_card.setEnableLoadMore(false)
        startListener()
    }

    private fun startListener() {
        iv_back.setOnClickListener(this)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mCardPackageLiveData.observe(this@CardPackageActivity, Observer {
                cardList.addAll(it)
                cardPackageListAdapter?.setList(cardList)
            })
            mException.observe(this@CardPackageActivity, Observer {
                LogUtil.v(it.toString())
            })
        }
    }

    override fun initData() {
        getData()
    }

    override fun getData() {
        super.getData()
        mViewModel.getMemberCard()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressedSupport()
            }
            R.id.rrl_cardPackage -> {
                val cardPackageBean = v.tag as CardPackageBean
                ARouter.getInstance().build(ARouterMap.WEBVIEW).withString(ARouterMap.URL, Constant.OTHER_MEMBER + "?id=${cardPackageBean.id}&merchantId=${cardPackageBean.merchantId}")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

    override fun onGetClassTypeNam(): Any {
        return "卡包列表"
    }

}