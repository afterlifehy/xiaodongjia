package com.wbb.xiaodongjia.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by hy on 2021/1/28.
 */
class DongClassViewpagerAdapter(fm: FragmentManager?,val mFagments:MutableList<Fragment>,val tabTitle:MutableList<String>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return mFagments[position]
    }

    override fun getCount(): Int {
        return mFagments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitle[position]
    }
}