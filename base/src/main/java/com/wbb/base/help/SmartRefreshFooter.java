package com.wbb.base.help;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.wbb.base.R;

/**
 * Author：xubo
 * Time：2019-05-06
 * Description：
 */
@SuppressLint("RestrictedApi")
public class SmartRefreshFooter extends LinearLayout implements RefreshFooter {
    private ProgressBar progressBar;
    private TextView textView;
    protected boolean moMoreData = false;
    private String noMoreText = "我是有底线的";

    public SmartRefreshFooter(Context context) {
        super(context);
        initView(context);
    }

    public void initView(Context context) {
        setGravity(Gravity.CENTER);
        progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loading_imofan_round_progress_bar));
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(45, 45));
        addView(progressBar);
        textView = new TextView(context);
        textView.setTextColor(context.getResources().getColor(R.color.font_dark_gray));
        textView.setTextSize(14);
        textView.setText("加载中…");
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = (int) getResources().getDimension(R.dimen.x_recycler_view_footer_margin);
        layoutParams.setMargins(margin, margin, margin, margin);
        textView.setLayoutParams(layoutParams);
        addView(textView);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setNoDataText(String text) {
        this.noMoreText = text;
    }

    public void setTextSize(int size) {
        textView.setTextSize(size);
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        this.moMoreData = noMoreData;
        if (noMoreData) {
            textView.setText(noMoreText);
            progressBar.setVisibility(View.GONE);
        }
        return true;
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }


    @Override
    public void onReleased(RefreshLayout refreshLayout, int height, int extendHeight) {
        if (!moMoreData) {
            progressBar.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {

    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        return 0;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        if (!moMoreData) {
            switch (newState) {
                case None:
                case PullUpToLoad:
                case Loading:
                case LoadReleased:
                case ReleaseToLoad:
                    progressBar.setVisibility(VISIBLE);
                    textView.setVisibility(VISIBLE);
                    textView.setText("加载中…");
                    break;
                case Refreshing:
                    progressBar.setVisibility(GONE);
                    textView.setVisibility(GONE);
                    break;
            }
        }
    }
}
