package com.wbb.xiaodongjia.ui.fragment

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.aries.ui.util.StatusBarUtil
import com.chouyou.base.ext.gone
import com.chouyou.base.ext.show
import com.tmall.ultraviewpager.UltraViewPager
import com.wbb.base.BaseApplication
import com.wbb.base.bean.CardPackageBean
import com.wbb.base.util.*
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.CardPackageAdapter
import com.wbb.xiaodongjia.adapter.CommonToolsAdapter
import com.wbb.base.bean.CommenTools
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.pf.PreferencesKeys
import com.wbb.base.pf.StoreFactory
import com.wbb.xiaodongjia.base.VbBaseFragment
import com.wbb.xiaodongjia.databinding.FragmentMineLayoutBinding
import com.wbb.xiaodongjia.mvvm.viewmodel.UserViewModel
import com.zrq.spanbuilder.TextStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by zj on 2021/1/25.
 * 我的
 */
class MineFragment : VbBaseFragment<UserViewModel, FragmentMineLayoutBinding>(),
    View.OnClickListener {
    var cardPackageAdapter: CardPackageAdapter? = null
    var staggeredGridLayoutManager: StaggeredGridLayoutManager? = null
    var commonToolsAdapter: CommonToolsAdapter? = null
    val colors = intArrayOf(R.color.color_ff31251e, R.color.color_ff31251e)
    val sizes = intArrayOf(14, 24)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD)
    var commonToolsList: MutableList<CommenTools> = ArrayList()
    var cardPackageList: MutableList<CardPackageBean> = ArrayList()
    var icon = ""
    var posterList: MutableList<String> = ArrayList()

    override fun onReloadData() {
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.fragment_mine_layout
    }

    override fun providerVMClass(): Class<UserViewModel>? {
        return UserViewModel::class.java
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    override fun initView() {
        val lp = binding.ablToolbar.layoutParams as RelativeLayout.LayoutParams
        lp.topMargin = StatusBarUtil.getStatusBarHeight()
        binding.ablToolbar.layoutParams = lp

        binding.uvpCardPackage.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        binding.uvpCardPackage.setMultiScreen(0.78f)
        binding.uvpCardPackage.setItemRatio(3.0)
        binding.uvpCardPackage.setAutoMeasureHeight(true)
        binding.uvpCardPackage?.setInfiniteLoop(false)
        binding.uvpCardPackage?.setOffscreenPageLimit(3)

        binding.rvTools.setHasFixedSize(true)
        binding.rvTools.isNestedScrollingEnabled = false
        staggeredGridLayoutManager =
            StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        binding.rvTools.layoutManager = staggeredGridLayoutManager
        commonToolsAdapter = CommonToolsAdapter(commonToolsList, this)
        binding.rvTools.adapter = commonToolsAdapter

        startListener()
    }

    private fun startListener() {

        binding.srlMine.setOnRefreshListener {
            getData()
            binding.srlMine.finishRefresh(500)
        }
        binding.srlMine.setEnableLoadMore(false)
        binding.ivRight.setOnClickListener(this)
        binding.rivHead.setOnClickListener(this)
        binding.rtvCopy.setOnClickListener(this)
        binding.llDirect.setOnClickListener(this)
        binding.llIndirect.setOnClickListener(this)
        binding.rtvRecharge.setOnClickListener(this)
        binding.rlPlus.setOnClickListener(this)
        binding.tvAll.setOnClickListener(this)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mMineLiveData.observe(this@MineFragment, Observer {
                icon = it.icon
                posterList.clear()
                if (it.urls != null && it.urls.size > 0) {
                    posterList.addAll(it.urls as MutableList<String>)
                }
                GlideUtils.getInstance()
                    .loadImage(binding.rivHead, icon, R.mipmap.ic_header_placeholder)
                binding.tvName.text = it.nickName
                binding.tvId.text = it.recommend
                GlobalScope.launch(Dispatchers.Main) {
                    StoreFactory.providePreferencesDataStore(BaseApplication.baseApplication)
                        .putString(
                            PreferencesKeys.INVITE_CODE, it.recommend
                        )
                }

                binding.tvDirect.text = it.recNum
                binding.tvIndirect.text = it.commNum
                val strings = arrayOf("¥ ", it.plusBalance)
                binding.tvBalance.text = StringUtil.getSpan(strings, sizes, colors, styles)

                commonToolsList.clear()
                commonToolsList.addAll(it.utils)
                commonToolsAdapter?.setList(commonToolsList)
            })
            mCardPackageLiveData.observe(this@MineFragment, Observer {
                cardPackageList.clear()
                cardPackageList.addAll(it)
                if (cardPackageList.size > 0) {
                    binding.tvNoCard.gone()
                    cardPackageAdapter = CardPackageAdapter(cardPackageList, this@MineFragment)
                    binding.uvpCardPackage?.adapter = cardPackageAdapter
                    binding.uvpCardPackage?.setCurrentItem(0, true)
                } else {
                    binding.tvNoCard.show()
                }
            })
            mException.observe(this@MineFragment, Observer {
                LogUtil.v(it.toString())
            })
        }
    }

    override fun initData() {

    }

    override fun getData() {
        super.getData()
        mViewModel.userInfoView()
        mViewModel.getMemberCard()
    }

    override fun onGetClassTypeNam(): Any {
        return "我的"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_right -> {
                ARouter.getInstance().build(ARouterMap.SETTING)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
            R.id.riv_head -> {
                ARouter.getInstance().build(ARouterMap.WEBVIEW)
                    .withString(ARouterMap.URL, Constant.PERSONAL_SETTING)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
            R.id.rtv_copy -> {
                StringUtil.copyString(binding.tvId.text.toString())
            }
            R.id.ll_direct -> {
                ARouter.getInstance().build(ARouterMap.RECOMMEND)
                    .withInt(ARouterMap.RECOMMEND_TYPE, 1).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .navigation()
                OnBuriedPointManager.get().getOnBuriedPointManager()
                    ?.ButtonClick("我的", "我的首页", "直接推荐")
            }
            R.id.ll_indirect -> {
                ARouter.getInstance().build(ARouterMap.RECOMMEND)
                    .withInt(ARouterMap.RECOMMEND_TYPE, 2).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .navigation()
                OnBuriedPointManager.get().getOnBuriedPointManager()
                    ?.ButtonClick("我的", "我的首页", "间接推荐")
            }
            R.id.rtv_recharge -> {
                ARouter.getInstance().build(ARouterMap.WEBVIEW)
                    .withString(ARouterMap.URL, Constant.PLUS_RECHARGE)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                OnBuriedPointManager.get().getOnBuriedPointManager()
                    ?.ButtonClick("我的", "我的首页", "充值")
            }
            R.id.rl_plus -> {
                ARouter.getInstance().build(ARouterMap.WEBVIEW)
                    .withString(ARouterMap.URL, Constant.DONG_PLUS + "?pic=${icon}")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
            R.id.tv_all -> {
                ARouter.getInstance().build(ARouterMap.CARD_PACKAGE)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
            R.id.ll_tools -> {
                val commenTools = v.tag as CommenTools
                if (TextUtils.isEmpty(commenTools.appUrl)) {
                    ARouter.getInstance().build(ARouterMap.WEBVIEW)
                        .withString(ARouterMap.URL, Constant.getH5ServerPath() + commenTools.h5Url)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                } else {
                    val jsonObject = JSONObject.parseObject(commenTools.appUrl)
                    if (jsonObject != null && jsonObject["url"] != null) {
                        if (jsonObject["url"].toString().startsWith("xdj:/")) {
                            val url = jsonObject["url"].toString()
                                .substring(5, jsonObject["url"].toString().length)
                            var path = url.split("?")[0]
                            if (TextUtils.equals(path, ARouterMap.POSTER)) {
                                ARouter.getInstance().build(path).withStringArrayList(
                                    ARouterMap.POSTER_LIST,
                                    posterList as java.util.ArrayList<String>
                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .navigation()
                            } else {
                                val par = StringUtil.getUrlParameter(url)
                                val build = ARouter.getInstance().build(path)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                if (par != null) {
                                    for (key in par.keys) {
                                        build.withString(key, par.getValue(key))
                                    }
                                }
                                build.navigation()
                            }
                        } else {
                            ARouter.getInstance().build(ARouterMap.TXT_SHOW)
                                .withString(ARouterMap.MESSAGE, commenTools.appUrl)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                        }
                    } else {
                        ARouter.getInstance().build(ARouterMap.TXT_SHOW)
                            .withString(ARouterMap.MESSAGE, commenTools.appUrl)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                    }
                }
                OnBuriedPointManager.get().getOnBuriedPointManager()
                    ?.ButtonClick("我的", "我的首页", commenTools.name)
            }
            R.id.rrl_cardPackage -> {
                val cardPackageBean = v.tag as CardPackageBean
                ARouter.getInstance().build(ARouterMap.WEBVIEW).withString(
                    ARouterMap.URL,
                    Constant.OTHER_MEMBER + "?id=${cardPackageBean.id}&merchantId=${cardPackageBean.merchantId}"
                )
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return FragmentMineLayoutBinding.inflate(layoutInflater)
    }
}