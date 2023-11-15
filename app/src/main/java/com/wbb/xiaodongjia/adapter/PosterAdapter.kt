package com.wbb.xiaodongjia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.aries.ui.view.radius.RadiusRelativeLayout
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.wbb.base.pf.PreferencesHelp
import com.wbb.base.util.AppUtil
import com.wbb.base.util.Constant
import com.wbb.base.util.GlideUtils
import com.wbb.xiaodongjia.R

/**
 * Created by hy on 2021/2/19.
 */
class PosterAdapter(val posterList: MutableList<Int>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item_poster = LayoutInflater.from(container.context)
            .inflate(R.layout.item_poster, null) as RadiusRelativeLayout
        val poster = posterList[position]
        GlideUtils.getInstance().loadImage(
            item_poster.findViewById(R.id.riv_poster),
            poster,
            R.mipmap.ic_life_circle_placeholder
        )
        val qrBitmap = CodeUtils.createImage(
            Constant.POST_SHARE + "?code=${PreferencesHelp.getINVITE_CODE()}",
            AppUtil.dip2px(72f),
            AppUtil.dip2px(72f),
            null
        )
        item_poster.findViewById<ImageView>(R.id.iv_scan).setImageBitmap(qrBitmap)
        container.addView(item_poster)
        return item_poster
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return posterList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }

}
