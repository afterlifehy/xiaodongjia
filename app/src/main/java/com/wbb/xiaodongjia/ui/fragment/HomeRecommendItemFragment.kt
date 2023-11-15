package com.wbb.xiaodongjia.ui.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.chouyou.base.ext.show
import com.wbb.base.bean.HomeAttUpData
import com.wbb.base.bean.MerchantInfo
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.ext.i18N
import com.wbb.base.help.AppToH5Manager
import com.wbb.base.observer.ObserverKey
import com.wbb.base.observer.ObserverManger
import com.wbb.base.observer.OnObserver
import com.wbb.base.util.ToastUtils
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.HomeCommodityAdapter
import com.wbb.xiaodongjia.adapter.NotDataAdapter
import com.wbb.xiaodongjia.adapter.help.StaggeredDividerItemDecoration
import com.wbb.xiaodongjia.base.VbBaseFragment
import com.wbb.xiaodongjia.databinding.FramgnetHomeItemLayoutBinding
import com.wbb.xiaodongjia.emuns.ATTENTION_CLASS
import com.wbb.xiaodongjia.listener.OnUpLocalAddressLinsener
import com.wbb.xiaodongjia.mvvm.viewmodel.AttentionViewModel
import com.wbb.xiaodongjia.mvvm.viewmodel.HomeViewModel
import com.wbb.xiaodongjia.utils.LocalAddressManager
import kotlinx.coroutines.*

/**
 * Created by zj on 2021/1/25.
 */
class HomeRecommendItemFragment : VbBaseFragment<HomeViewModel, FramgnetHomeItemLayoutBinding>(),
    HomeCommodityAdapter.OnRecommendClickLinsener, OnUpLocalAddressLinsener, OnObserver {
    private val mAllList = ArrayList<MerchantInfo>()
    var mHomeCommodityAdapter: HomeCommodityAdapter? = null
    var mAttentionViewModel: AttentionViewModel? = null
    private var page: Int = 1
    private var categoryId: String = ""
    private var categoryName: String = ""

    companion object {
        const val CATE_ID = "categoryId"
        const val CATE_NAME = "categoryName"
        fun getFragment(categoryId: String, categoryName: String): HomeRecommendItemFragment {
            val mHomeRecommendItemFragment = HomeRecommendItemFragment()
            val mBundle = Bundle()
            mBundle.putString(CATE_ID, categoryId)
            mBundle.putString(CATE_NAME, categoryName)
            mHomeRecommendItemFragment.arguments = mBundle
            return mHomeRecommendItemFragment
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mMaecht.observe(this@HomeRecommendItemFragment, Observer {
                binding.homeRecRef.finishLoadMore()
                bindAdapter(it.list)
                if (page == 1 && categoryId.equals("RECOMMEND")) {//只有推荐有
                    getRecommend()
                }

            })
            errMsg.observe(this@HomeRecommendItemFragment, {
                ToastUtils.showErrorToast(it.msg)
                showNotDataViews()
            })
            mSourceMaterialData.observe(this@HomeRecommendItemFragment, Observer {
                val mList = ArrayList<MerchantInfo>()
                it?.forEach {
                    mList.add(MerchantInfo(type = 1, recommend = it))
                }
                if (mList.size > 0) {
                    mAllList.addAll(0, mList)
                    mHomeCommodityAdapter?.setList(mAllList)
                }
            })
        }
    }

    override fun providerVMClass(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    /**
     * 获取推荐
     */
    fun getRecommend() {
        val parM = HashMap<String, String>()
        parM["type"] = "1"
        parM["cityId"] = LocalAddressManager.instance().getCityId()
        mViewModel.getSourceMaterial(parM)
    }

    override fun getData() {
        super.getData()
        val par = HashMap<String, String>()
        par["page"] = page.toString()
        par["pageSize"] = "10"
        par["searchType"] = ""
        if (categoryId.equals("RECOMMEND")) {//推荐
            par["searchKeyWord"] = "RECOMMEND"
        } else if (categoryId.equals("D_JIA_RECOMMEND")) {//东家招募
            par["searchKeyWord"] = "D_JIA_RECOMMEND"
        } else {
            par["firstTypeId"] = categoryId
        }
        par["searchKey"] = ""
        par["cityId"] = LocalAddressManager.instance().getCityId()
        mViewModel.getMerchantSearchList(par)


    }

    override fun onReloadData() {
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false


    override fun initView() {
        binding.homeRecRef.setOnLoadMoreListener {
            page++
            getData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ObserverManger.getInstance(ObserverKey.HOME_ATT_UP).registerObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ObserverManger.getInstance(ObserverKey.HOME_ATT_UP).removeObserver(this)

    }

    fun showNotDataViews() {
        binding.rlListNot.show()
        val list = ArrayList<String>()
        list.add("")
        val mNotDataAdapter = NotDataAdapter(list)
        binding.rlListNot.layoutManager = LinearLayoutManager(requireContext())
        binding.rlListNot.adapter = mNotDataAdapter
    }

    fun bindAdapter(list: List<MerchantInfo>) {
        if (page == 1 && list.size <= 0) {
            showNotDataViews()
            return
        }
        if (list.size <= 0) {
            binding.homeRecRef.finishLoadMoreWithNoMoreData()
            return
        }
        mAllList.addAll(list)
        if (mHomeCommodityAdapter == null) {
            mHomeCommodityAdapter = HomeCommodityAdapter(mAllList)
            mHomeCommodityAdapter?.setOnOnRecommendClickLinsener(this)
            val mGridLayoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            binding.rlList.layoutManager = mGridLayoutManager
            binding.rlList.addItemDecoration(StaggeredDividerItemDecoration(context, 15))
            binding.rlList.adapter = mHomeCommodityAdapter
            binding.rlList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    val first = intArrayOf(0, 0)
                    mGridLayoutManager.findFirstCompletelyVisibleItemPositions(first)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && (first[0] == 1 || first[1] == 1)) {
                        mGridLayoutManager.invalidateSpanAssignments()
                    }
                }
            })
        } else {
            mHomeCommodityAdapter?.setList(mAllList)
        }

    }

    override fun initData() {
        mAttentionViewModel = ViewModelProvider(this).get(AttentionViewModel::class.java)
        categoryId = arguments!!.getString(CATE_ID).toString()
        categoryName = arguments!!.getString(CATE_NAME).toString()
        getData()
    }

    override fun onGetClassTypeNam(): Any {
        return "首页商品列表"
    }

    override fun onItemClickLinsener(postion: Int, mMerchantInfo: MerchantInfo) {
        if (mMerchantInfo.type == 1) {
            // ToastUtils.showToash("暂无跳转")
        } else {


            AppToH5Manager.toMerchantDetails(mMerchantInfo.merchantStoreId)
            OnBuriedPointManager.get().getOnBuriedPointManager()
                ?.onArticleView(
                    mMerchantInfo.merchantStoreId,
                    mMerchantInfo.name,
                    categoryName,
                    if (mMerchantInfo.follow) "1" else "0",
                    "${postion}",
                    "",
                    "",
                    "首页"
                )
        }

    }

    override fun onFavoritesClickLinsener(
        postion: Int,
        mMerchantInfo: MerchantInfo,
        isFavorites: Boolean
    ) {
        mAttentionViewModel?.addFollow(
            mMerchantInfo.merchantStoreId,
            ATTENTION_CLASS.MERCHANT.mType.toString()
        )
            ?.observe(this, {
                if (!isFavorites) {
                    ToastUtils.showSucessToast(i18N(R.string.关注成功))
                } else {
                    //ToastUtils.showSucessToast("取消关注")

                }
                mHomeCommodityAdapter?.data?.get(postion)?.follow = !isFavorites
                mHomeCommodityAdapter?.notifyItemChanged(postion)

            })
    }

    override fun onUpLocalAddress() {
        val currentAMapLocation = LocalAddressManager.instance().getCurrentAMapLocation()
        mHomeCommodityAdapter?.setAMapLocation(currentAMapLocation)
    }

    override fun update(obj: Any?) {
        obj?.apply {
            if (this is HomeAttUpData) {
                mHomeCommodityAdapter?.data?.forEach {
                    if (this.id.equals(it.merchantStoreId)) {
                        it.follow = this.focus
                        mHomeCommodityAdapter?.notifyDataSetChanged()
                        return
                    }
                }
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return FramgnetHomeItemLayoutBinding.inflate(layoutInflater)
    }
}