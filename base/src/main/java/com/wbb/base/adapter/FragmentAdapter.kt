package com.wbb.base.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Created by zj on 2021/2/22.
 */
class FragmentAdapter(fm: FragmentManager, fmList: List<Fragment>?) :
    FragmentStatePagerAdapter(fm) {
    private var fmList: List<Fragment>? = null

    init {
        this.fmList = fmList
    }

    override fun getCount(): Int {
        return fmList!!.size
    }

    override fun getItem(position: Int): Fragment {
        return fmList!!.get(position)
    }
}