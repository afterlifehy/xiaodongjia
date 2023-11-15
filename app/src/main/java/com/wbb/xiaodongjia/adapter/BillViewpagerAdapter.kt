package com.wbb.xiaodongjia.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.wbb.xiaodongjia.ui.fragment.BillListFragment

/**
 * Created by hy on 2021/2/2.
 */
class BillViewpagerAdapter(fm: FragmentManager?, val tabTitleList: ArrayList<String>) : FragmentPagerAdapter(fm) {
    var billListFragment: BillListFragment? = null

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putInt("type", position)
        billListFragment = BillListFragment()
        billListFragment?.arguments = bundle
        return billListFragment as BillListFragment
    }

    override fun getCount(): Int {
        return tabTitleList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitleList[position]
    }
}