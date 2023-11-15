package com.wbb.xiaodongjia.adapter

import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.base.bean.CityChildItemInfo
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.bean.CityInfo

/**
 * Created by zj on 2021/2/23.
 */
class CityListAdapter(data: MutableList<CityInfo>, mOnSelectLinsener: OnSelectLinsener?) :
    BaseMultiItemQuickAdapter<CityInfo, BaseViewHolder>(data) {
    val list = ArrayList<String>()
    val listH = ArrayList<String>()
    private val mSharedPool = RecyclerView.RecycledViewPool()
    private var mOnSelectLinsener: OnSelectLinsener? = null

    init {
        this.mOnSelectLinsener = mOnSelectLinsener
        addItemType(1, R.layout.view_address_select_layout)
        addItemType(0, R.layout.view_cityt_list_layout)
        list.add("无锡")
        list.add("上海")
        list.add("南京")

        listH.add("无锡")
        listH.add("上海")
        listH.add("苏州")
        listH.add("南京")
        listH.add("南京")
    }

    override fun convert(holder: BaseViewHolder, item: CityInfo) {
        if (item.mType == 1) {
            val rl_hot_city = holder.getView<RecyclerView>(R.id.rl_hot_city)
            val mHotCityAdapter = HotCityAdapter(list)
            rl_hot_city.layoutManager = GridLayoutManager(context, 3)
            rl_hot_city.adapter = mHotCityAdapter


            val rl_cy_city = holder.getView<RecyclerView>(R.id.rl_cy_city)
            val mHotCtCityAdapter = HotCityAdapter(listH)
            rl_cy_city.layoutManager = GridLayoutManager(context, 3)
            rl_cy_city.adapter = mHotCtCityAdapter


        } else if (item.mType == 0) {
            val tv_lett = holder.getView<TextView>(R.id.tv_lett)
            tv_lett.setText(item.mCityItemInfo!!.name)
            val rl_city_item_list = holder.getView<RecyclerView>(R.id.rl_city_item_list)
            val madapter = rl_city_item_list.adapter
            if (madapter == null) {
                val mCityChildAdapter =
                    CityChildAdapter(item.mCityItemInfo!!.items!!)
                mSharedPool.apply {
                    rl_city_item_list.setRecycledViewPool(this)
                }
                mCityChildAdapter.setOnSelectLinsener(mOnSelectLinsener!!)
                rl_city_item_list.layoutManager = LinearLayoutManager(context)
                rl_city_item_list.adapter = mCityChildAdapter
            } else {
                val mCityChildAdapter = (madapter as CityChildAdapter)
                mCityChildAdapter.setList(item.mCityItemInfo!!.items!!)
                mCityChildAdapter.setOnSelectLinsener(mOnSelectLinsener!!)
            }


        }
    }

    interface OnSelectLinsener {
        fun onSelectCityLinsener(mCityChildItemInfo: CityChildItemInfo)
    }
}