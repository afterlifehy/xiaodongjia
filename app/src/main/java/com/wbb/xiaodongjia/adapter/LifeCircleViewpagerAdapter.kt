package com.wbb.xiaodongjia.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.wbb.base.BaseApplication
import com.wbb.base.bean.LifeCircleActivityBean
import com.wbb.base.ext.i18n
import com.wbb.base.util.GlideUtils
import com.wbb.base.util.LogUtil
import com.wbb.base.util.StringUtil
import com.wbb.xiaodongjia.R

/**
 * Created by hy on 2021/1/27.
 */
class LifeCircleViewpagerAdapter(var lifeCircleActivityList: MutableList<LifeCircleActivityBean>, var onClickListener: View.OnClickListener) : PagerAdapter() {
    var colors = intArrayOf(R.color.white_txt, R.color.white_65_color)
    var sizes = intArrayOf(16, 14)
    var participantsAdapter: ParticipantsAdapter? = null
    var mCurrentView: View? = null
    var viewList: MutableList<String> = ArrayList()

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
//        if (lifeCircleActivityList != null && lifeCircleActivityList.size > 0) {
//            LogUtil.v("1")
//            return Integer.MAX_VALUE
//        }
        return lifeCircleActivityList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item_life_circle = View.inflate(BaseApplication.instance(), R.layout.item_life_circle, null)
        var activityBean = lifeCircleActivityList[position % lifeCircleActivityList.size]
        GlideUtils.getInstance().loadImage(item_life_circle.findViewById(R.id.riv_life), activityBean.bannerUrl, R.mipmap.ic_life_circle_placeholder)
        item_life_circle.findViewById<TextView>(R.id.tv_lifeTitle).text = activityBean.title
        item_life_circle.findViewById<TextView>(R.id.tv_participantRemind).text = activityBean.remainNum.toString()
        val strings = arrayOf(activityBean.attendNum.toString(), i18n(R.string.东家参加))
        item_life_circle.findViewById<TextView>(R.id.tv_participants).text = StringUtil.getSpan(strings, sizes, colors)
        item_life_circle.findViewById<ProgressBar>(R.id.pb_participants).progress = activityBean.attendNum * 100 / (activityBean.remainNum + activityBean.attendNum)
        var rv_participants = item_life_circle.findViewById<RecyclerView>(R.id.rv_participants)
        rv_participants.setHasFixedSize(true)
        rv_participants.layoutManager = LinearLayoutManager(BaseApplication.instance(), LinearLayoutManager.HORIZONTAL, false)
        participantsAdapter = ParticipantsAdapter(activityBean.attendIcons as ArrayList)
        rv_participants.adapter = participantsAdapter

        item_life_circle.findViewById<CardView>(R.id.cv_lifeCircle).setOnClickListener(onClickListener)
        container.addView(item_life_circle)
        return item_life_circle
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
        mCurrentView = `object` as View
    }

    fun setStatus() {
        mCurrentView?.tag = "change"
        this.notifyDataSetChanged()
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }

    override fun getItemPosition(`object`: Any): Int {
        return if ("change" == (`object` as View).tag) {
            POSITION_NONE
        } else POSITION_UNCHANGED
    }
}