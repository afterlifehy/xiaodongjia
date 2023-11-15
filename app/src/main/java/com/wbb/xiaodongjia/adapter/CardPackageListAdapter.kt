package com.wbb.xiaodongjia.adapter

import android.view.View
import android.widget.TextView
import com.amap.api.location.AMapLocation
import com.amap.api.mapcore.util.it
import com.aries.ui.view.radius.RadiusRelativeLayout
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.base.adapter.CustomBaseQuickAdapter
import com.wbb.base.bean.CardPackageBean
import com.wbb.base.map.AMapUtils
import com.wbb.base.map.LngLat
import com.wbb.base.util.GlideUtils
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.utils.LocalAddressManager

/**
 * Created by hy on 2021/2/22.
 */
class CardPackageListAdapter(data: MutableList<CardPackageBean>?, val onClickListener: View.OnClickListener) :
    CustomBaseQuickAdapter<CardPackageBean, BaseViewHolder>(R.layout.item_card_package, data) {
    var mAMapLocation: AMapLocation? = null

    init {
        mAMapLocation = LocalAddressManager.instance().getCurrentAMapLocation()
    }

    override fun convert(holder: BaseViewHolder, item: CardPackageBean) {
        GlideUtils.getInstance().loadImage(holder.getView(R.id.riv_cardBg), item.backImage, R.mipmap.ic_card_package_placeholder)
        GlideUtils.getInstance().loadImage(holder.getView(R.id.riv_cardLogo), item.logo, R.mipmap.ic_tools_placeholder)
        holder.setText(R.id.tv_cardName, item.merchantName)
        val latitude = item.latitude
        val longitude = item.longitude
        if (latitude != null && longitude != null) {//去计算经纬度
            mAMapLocation?.let {
                val start = LngLat(longitude, latitude, false)
                val end = LngLat(it.longitude, it.latitude, false)
                val distance = AMapUtils.calculateLineDistance(start, end)
                if (distance < 1000) {
                    holder.setText(R.id.tv_distance, "<1km")
                } else {
                    holder.getView<TextView>(R.id.tv_distance).visibility = View.GONE
                }
            }
        }
        holder.setText(R.id.tv_balance, "¥${item.balance}")
        holder.getView<RadiusRelativeLayout>(R.id.rrl_cardPackage).tag = item
        holder.getView<RadiusRelativeLayout>(R.id.rrl_cardPackage).setOnClickListener(onClickListener)
    }
}