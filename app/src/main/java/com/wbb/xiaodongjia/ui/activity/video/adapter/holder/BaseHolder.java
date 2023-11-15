package com.wbb.xiaodongjia.ui.activity.video.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by zj on 2021/2/1.
 */

public abstract class BaseHolder extends RecyclerView.ViewHolder {
    public BaseHolder(View itemView) {
        super(itemView);
    }

    public abstract ImageView getCoverView();

    public abstract ViewGroup getContainerView();

    public abstract ImageView getPlayIcon();

    /**
     * 获取进度条控件
     *
     * @return
     */
    public abstract SeekBar getSeekBarSchedule();
}
