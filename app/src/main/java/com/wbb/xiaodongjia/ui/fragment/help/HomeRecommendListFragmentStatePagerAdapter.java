package com.wbb.xiaodongjia.ui.fragment.help;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.wbb.base.bean.HomeRecommendInfo;
import com.wbb.xiaodongjia.ui.fragment.HomeRecommendItemFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * 首页信息流公共的FragmentStatePagerAdapter
 */
public class HomeRecommendListFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private List<HomeRecommendInfo> categoryList;
    private List<Fragment> fragments;

    public HomeRecommendListFragmentStatePagerAdapter(FragmentManager fm, List<HomeRecommendInfo> categoryList) {
        super(fm);
        this.categoryList = categoryList;
        this.fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        HomeRecommendInfo mInfo = categoryList.get(position);
        if (mInfo.getId().equals("RECOMMEND") || mInfo.getId().equals("D_JIA_RECOMMEND")) {
            fragment = HomeRecommendItemFragment.Companion.getFragment(mInfo.getId(), mInfo.getName());
        } else {
            fragment = HomeRecommendItemFragment.Companion.getFragment(mInfo.getMHomeMenuInfo().getCategoryId(), "推荐");
        }
        fragments.add(fragment);
        return fragment;
    }

    /**
     * 获取当前viewPager显示的fragment
     *
     * @return
     */
    public List<Fragment> getFragments() {
        return fragments;
    }

    public void setCategoryList(List<HomeRecommendInfo> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }


    private HomeRecommendItemFragment mCurrentFragment;

    public HomeRecommendItemFragment getCurrentFragment() {
        return mCurrentFragment;
    }

}