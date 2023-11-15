package com.wbb.xiaodongjia.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.wbb.base.bean.VideoPlayInfo;
import com.wbb.xiaodongjia.listener.OnAddRecommendLinsener;
import com.wbb.xiaodongjia.ui.activity.video.videolist.VideoPlayItemFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zj on 2019/12/20.
 */
public class LivePlayItemAdapter extends FragmentStatePagerAdapter {
    private List<VideoPlayInfo> mList;
    private List<Fragment> fragments;

    public LivePlayItemAdapter(@NonNull FragmentManager fm, List<VideoPlayInfo> mList) {
        super(fm);
        this.mList = mList;
        this.fragments = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment mFragment = VideoPlayItemFragment.Companion.getFragmetn(mList.get(position));
        fragments.add(mFragment);
        return mFragment;
    }

    /**
     * 获取当前viewPager显示的fragment
     *
     * @return
     */
    public List<Fragment> getFragments() {
        return fragments;
    }

    public void setmList(List<VideoPlayInfo> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);

    }

    @Override
    public int getCount() {
        // return 1;
        return mList == null ? 0 : mList.size();
    }
}
