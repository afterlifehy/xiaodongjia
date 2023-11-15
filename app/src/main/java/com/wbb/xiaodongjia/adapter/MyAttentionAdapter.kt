package com.wbb.xiaodongjia.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.amap.api.location.AMapLocation
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.makeramen.roundedimageview.RoundedImageView
import com.wbb.base.adapter.CustomBaseQuickAdapter
import com.wbb.base.bean.LabInfo
import com.wbb.base.bean.MerchantFolownfo
import com.wbb.base.help.OnItemClickLinsener
import com.wbb.base.map.AMapUtils
import com.wbb.base.map.LngLat
import com.wbb.base.util.DateUtil
import com.wbb.base.util.DateUtils
import com.wbb.base.util.GlideUtils
import com.wbb.base.wiget.labels.LabelsView
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.emuns.ATTENTION_CLASS
import com.wbb.xiaodongjia.utils.LocalAddressManager

/**
 * Created by zj on 2021/2/22.
 */
class MyAttentionAdapter(data: MutableList<MerchantFolownfo>?, mType: Int) :
    CustomBaseQuickAdapter<MerchantFolownfo, BaseViewHolder>(
        R.layout.view_atent_layout, data
    ) {

    private var mType: Int = 1
    private var mOnItemClickLinsener: OnItemClickLinsener<MerchantFolownfo>? = null
    private var mAMapLocation: AMapLocation? = null

    init {
        this.mType = mType
        mAMapLocation = LocalAddressManager.instance().getCurrentAMapLocation()
    }

    /**
     * 设置点击事件
     */
    fun setOnItemClickLinsener(mOnItemClickLinsener: OnItemClickLinsener<MerchantFolownfo>?) {
        this.mOnItemClickLinsener = mOnItemClickLinsener

    }

    override fun convert(holder: BaseViewHolder, item: MerchantFolownfo) {
        val fl_bom_layout = holder.getView<FrameLayout>(R.id.fl_bom_layout)
        if (fl_bom_layout.getChildAt(0) != null) {
            fl_bom_layout.removeAllViews()
        }
        holder.getView<LinearLayout>(R.id.ll_conent).setOnClickListener {
            mOnItemClickLinsener?.onItemClick(holder.layoutPosition, item)
        }

        val tv_distance = holder.getView<TextView>(R.id.tv_distance)
        tv_distance.visibility = View.GONE
        val rl_icon = holder.getView<RoundedImageView>(R.id.rl_icon)

        var imgUrl = ""
        when (mType) {
            ATTENTION_CLASS.MERCHANT.mType -> {//商户类型
                val mmerchantSearchVO = item.merchantSearchVO
                if (mmerchantSearchVO == null) {
                    return
                }
                val latitude = item.merchantSearchVO?.latitude
                val longitude = item.merchantSearchVO?.longitude
                if (latitude != null && longitude != null) {
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
                mmerchantSearchVO?.let {
                    if (it.merchantImages != null && it.merchantImages!!.size > 0) {
                        imgUrl = it.merchantImages!!.get(0).imgPath
                    }
                }

                tv_distance.visibility = View.VISIBLE
                val addView = View.inflate(context, R.layout.view_sh_bom_layout, null)
                val ll_libs = addView.findViewById<LabelsView>(R.id.ll_libs)
                val tv_name = addView.findViewById<TextView>(R.id.tv_name)
                tv_name.setText(mmerchantSearchVO!!.name)
                val labs = mmerchantSearchVO.merchantLabels

                if (labs != null && labs.size > 0) {
                    ll_libs.visibility = View.VISIBLE
                    ll_libs.setLabels(labs, object : LabelsView.LabelTextProvider<LabInfo> {
                        @SuppressLint("Range", "UseCompatLoadingForDrawables")
                        override fun getLabelText(
                            label: TextView?,
                            position: Int,
                            data: LabInfo?
                        ): CharSequence {
                            if (!TextUtils.isEmpty(data?.color)) {
                                label?.setTextColor(Color.parseColor(data!!.color))
                            }

                            if (!TextUtils.isEmpty(data?.bgColor)) {
                                val mBg =
                                    context.getDrawable(R.drawable.attp_bg_shape) as GradientDrawable
                                mBg.setColor(Color.parseColor(data!!.bgColor))
                                label?.background = mBg
                            }
                            return data!!.labelName
                        }

                    })
                } else {
                    ll_libs.visibility = View.GONE
                }


                fl_bom_layout.addView(addView)
            }
            ATTENTION_CLASS.CLASSROOM.mType -> {
                val mCouresInfo = item.course!!
                imgUrl = mCouresInfo.imgPath
                val addView = View.inflate(context, R.layout.view_course_bom_layout, null)
                addView.findViewById<TextView>(R.id.tv_coures_name).setText(mCouresInfo.courseName)
                addView.findViewById<TextView>(R.id.tv_time).setText(mCouresInfo.minutes.toString())
                addView.findViewById<TextView>(R.id.tv_teacher).setText(mCouresInfo.author)

                fl_bom_layout.addView(addView)
            }
            ATTENTION_CLASS.ACTIVITY.mType -> {//活动
                val mInfo = item.activity
                if (mInfo == null) {
                    return
                }

                mInfo.activityImgList?.let {
                    imgUrl = it.get(0).imgPath
                }
                val addView = View.inflate(context, R.layout.view_activity_bom_layout, null)
                val tv_name = addView.findViewById<TextView>(R.id.tv_name)
                val tv_time = addView.findViewById<TextView>(R.id.tv_time)
                val tv_address = addView.findViewById<TextView>(R.id.tv_address)
                tv_address.setText(mInfo.cityName + mInfo.areaName + mInfo.address)
                tv_name.setText(mInfo.title)
                //  tv_time.setText(DateUtils.getDateFromYYYYMMDDHHMMSS(mInfo.submitTime))
                tv_time.setText(DateUtils.getDateFromYYYYMMDDHHMMSS(mInfo.createTimeLong))
                fl_bom_layout.addView(addView)
            }
        }
        if (!TextUtils.isEmpty(imgUrl)) {
            GlideUtils.getInstance()
                .loadImage(rl_icon, imgUrl)
        }


    }
}