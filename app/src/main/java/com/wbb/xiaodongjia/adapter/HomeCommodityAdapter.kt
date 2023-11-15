package com.wbb.xiaodongjia.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.amap.api.location.AMapLocation
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.exoplayer2.util.Log
import com.makeramen.roundedimageview.RoundedImageView
import com.wbb.base.bean.LabInfo
import com.wbb.base.bean.MerchantInfo
import com.wbb.base.map.AMapUtils
import com.wbb.base.map.LngLat
import com.wbb.base.util.AppUtil
import com.wbb.base.util.GlideUtils
import com.wbb.base.wiget.labels.LabelsView
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.utils.LocalAddressManager

/**
 * Created by zj on 2021/1/25.
 * 首页商品列表
 */
class HomeCommodityAdapter(data: MutableList<MerchantInfo>) :
    BaseMultiItemQuickAdapter<MerchantInfo, BaseViewHolder>(data) {
    var mOnRecommendClickLinsener: OnRecommendClickLinsener? = null
    private var mAMapLocation: AMapLocation? = null

    //根据宽度计算，每个Item的高度
    private var high = 640

    init {
        addItemType(0, R.layout.view_home_commodity_layout)
        addItemType(1, R.layout.view_first_comm_layout)
        mAMapLocation = LocalAddressManager.instance().getCurrentAMapLocation()
        high = ((AppUtil.getScreanWidth() - AppUtil.dp2px(45f)) / 2 / 0.69).toInt()
    }

    /**
     * 获取城市定位
     */
    fun setAMapLocation(mAMapLocation: AMapLocation?) {
        this.mAMapLocation = mAMapLocation
        notifyDataSetChanged()
    }


    /**
     * 设置回调
     */
    fun setOnOnRecommendClickLinsener(mOnRecommendClickLinsener: OnRecommendClickLinsener?) {
        this.mOnRecommendClickLinsener = mOnRecommendClickLinsener
    }

    override fun convert(holder: BaseViewHolder, item: MerchantInfo) {
        if (item.type == 1) {//推荐位
            holder.getView<View>(R.id.ll_content).setOnClickListener {
                mOnRecommendClickLinsener?.onItemClickLinsener(holder.layoutPosition, item)
            }
            val rl_img = holder.getView<ImageView>(R.id.rl_img)
            item.recommend?.let {
                GlideUtils.getInstance().loadImage(rl_img, it.img)
            }

        } else {
            val tv_distance = holder.getView<TextView>(R.id.tv_distance)
            val ll_content = holder.getView<FrameLayout>(R.id.ll_content)
            val layPar = ll_content.layoutParams
            layPar.height = high
            ll_content.layoutParams = layPar


            val is_activity = holder.getView<TextView>(R.id.is_activity)
            tv_distance.visibility = View.GONE
            if (item.recruit) {
                is_activity.visibility = View.VISIBLE
            } else {
                is_activity.visibility = View.GONE
                val latitude = item.latitude
                val longitude = item.longitude
                if (latitude != null && longitude != null) {//去计算经纬度
                    mAMapLocation?.let {
                        val start = LngLat(longitude, latitude, false)
                        val end = LngLat(it.longitude, it.latitude, false)
                        val distance = AMapUtils.calculateLineDistance(start, end)
                        if (distance < 1000) {
                            tv_distance.visibility = View.VISIBLE
                            tv_distance.setText("<1km")
                        } else {
                            tv_distance.visibility = View.GONE
                        }
                    }
                }
            }
            holder.getView<View>(R.id.ll_content).setOnClickListener {
                mOnRecommendClickLinsener?.onItemClickLinsener(holder.layoutPosition, item)
            }
            val rl_mat_bg = holder.getView<RoundedImageView>(R.id.rl_mat_bg)
            val ll_libs = holder.getView<LabelsView>(R.id.ll_libs)

            val labs = item.merchantLabels
            if (labs != null && labs.size > 0) {
                ll_libs.visibility = View.VISIBLE
                ll_libs.setLabels(labs, object : LabelsView.LabelTextProvider<LabInfo> {
                    @SuppressLint("Range", "UseCompatLoadingForDrawables")
                    override fun getLabelText(
                        label: TextView?,
                        position: Int,
                        data: LabInfo?
                    ): CharSequence {
                        label?.setBackgroundResource(0)
                        return data!!.labelName
                    }
                })
            } else {
                ll_libs.visibility = View.GONE
            }
            val cb_is_favorites = holder.getView<CheckBox>(R.id.cb_is_favorites)
            cb_is_favorites.isChecked = item.follow
            cb_is_favorites.setOnClickListener {
                mOnRecommendClickLinsener?.onFavoritesClickLinsener(
                    holder.layoutPosition,
                    item,
                    item.follow
                )
            }

            holder.setText(R.id.tv_mt_name, item.name)
            GlideUtils.getInstance()
                .loadImage(rl_mat_bg, item.coverImg, R.mipmap.home_sj_default_bg)
        }
    }

    interface OnRecommendClickLinsener {
        /**
         * item点击
         */
        fun onItemClickLinsener(postion: Int, mMerchantInfo: MerchantInfo)

        /**
         * 收藏
         */
        fun onFavoritesClickLinsener(
            postion: Int,
            mMerchantInfo: MerchantInfo,
            isFavorites: Boolean
        )
    }
}