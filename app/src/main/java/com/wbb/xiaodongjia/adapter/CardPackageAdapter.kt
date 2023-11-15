package com.wbb.xiaodongjia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.aries.ui.view.radius.RadiusRelativeLayout
import com.wbb.base.bean.CardPackageBean
import com.wbb.base.util.GlideUtil
import com.wbb.base.util.GlideUtils
import com.wbb.xiaodongjia.R

/**
 * Created by hy on 2021/2/19.
 */
class CardPackageAdapter(val cardPackageList: MutableList<CardPackageBean>,val onClickListener: View.OnClickListener) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item_card_package = LayoutInflater.from(container.context).inflate(R.layout.item_card_package, null) as RadiusRelativeLayout
        val cardPackageBean = cardPackageList[position]
        GlideUtils.getInstance().loadImage(item_card_package.findViewById(R.id.riv_cardBg), cardPackageBean.backImage, R.mipmap.ic_card_package_placeholder)
        GlideUtils.getInstance().loadImage(item_card_package.findViewById(R.id.riv_cardLogo), cardPackageBean.logo, R.mipmap.ic_tools_placeholder)
        item_card_package.findViewById<TextView>(R.id.tv_cardName).text = cardPackageBean.merchantName
        item_card_package.findViewById<TextView>(R.id.tv_balance).text = "¥${cardPackageBean.balance}"
        item_card_package.findViewById<RadiusRelativeLayout>(R.id.rrl_cardPackage)?.tag = cardPackageBean
        item_card_package.findViewById<RadiusRelativeLayout>(R.id.rrl_cardPackage)?.setOnClickListener(onClickListener)
        container.addView(item_card_package)
        return item_card_package
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
//        view.setBackgroundColor(ContextCompat.getColor(BaseApplication.getInstance(),R.color.color_ff131b2a))
        return view === `object`
    }

    override fun getCount(): Int {
        return cardPackageList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }
}
