package com.wbb.xiaodongjia.adapter

import android.os.Bundle
import android.util.ArrayMap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.wbb.base.util.MagicValue
import com.wbb.xiaodongjia.ui.fragment.SearchResultFragment

/**
 * Created by hy on 2021/2/25.
 */
class SearchViewPagerAdapter(fm: FragmentManager?, var tabTitleList: ArrayList<String>) : FragmentPagerAdapter(fm) {
    var searchResultFragment: SearchResultFragment? = null
    var searchTypeMap: MutableMap<Int, String> = ArrayMap()
    var searchText = ""

    init {
        searchTypeMap[0] = MagicValue.SEARCH_TYPE_ALL
        searchTypeMap[1] = MagicValue.SEARCH_TYPE_BUSINESS
        searchTypeMap[2] = MagicValue.SEARCH_TYPE_CLASS
        searchTypeMap[3] = MagicValue.SEARCH_TYPE_ACTIVITY
    }

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putString("searchType", searchTypeMap[position])
        bundle.putString("searchTxt", searchText)
        searchResultFragment = SearchResultFragment()
        searchResultFragment?.arguments = bundle
        return searchResultFragment as SearchResultFragment
    }

    override fun getCount(): Int {
        return tabTitleList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitleList[position]
    }

    fun setSearchTxt(s: String) {
        this.searchText = s
    }
}