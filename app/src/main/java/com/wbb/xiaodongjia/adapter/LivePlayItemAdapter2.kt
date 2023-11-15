package com.wbb.xiaodongjia.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wbb.base.bean.VideoPlayInfo
import com.wbb.xiaodongjia.listener.OnAddRecommendLinsener
import com.wbb.xiaodongjia.ui.activity.video.videolist.VideoPlayItemFragment

/**
 * Created by zj on 2020/4/27.
 */
class LivePlayItemAdapter2(
    fragment: Fragment,
    mType: String,
    mList: List<VideoPlayInfo>,
    mOnAddRecommendLinsener: OnAddRecommendLinsener
) : FragmentStateAdapter(fragment) {
    private var mList: List<VideoPlayInfo>? = null
    private var mOnAddRecommendLinsener: OnAddRecommendLinsener? = null
    private var mType: String? = null

    init {
        this.mType = mType
        this.mList = mList
        this.mOnAddRecommendLinsener = mOnAddRecommendLinsener
    }

    override fun getItemCount(): Int = mList!!.size

    override fun createFragment(position: Int): Fragment {
        val mInfo = mList?.get(position)
        val mFragment = VideoPlayItemFragment()
        return mFragment
    }


    fun setmList(mList: List<VideoPlayInfo>) {
        this.mList = mList
        notifyDataSetChanged()
    }

}