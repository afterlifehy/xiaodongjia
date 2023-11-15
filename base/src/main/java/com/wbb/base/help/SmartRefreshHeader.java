package com.wbb.base.help;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.wbb.base.R;
import com.wbb.base.util.AppUtil;

/**
 *
 */
@SuppressLint("RestrictedApi")
public class SmartRefreshHeader extends LinearLayout implements RefreshHeader {
    private ViewGroup container;
    private ImageView animation_iv;
    private AnimationDrawable animationDrawable;
    private SmartRefreshPullLinsener mSmartRefreshPullLinsener;

    public SmartRefreshHeader(Context context) {
        super(context);
        initView(context);
    }

    /**
     * 这个是用来监听刷新的开始和完成
     *
     * @param mSmartRefreshPullLinsener
     */
    public void setmSmartRefreshPullLinsener(SmartRefreshPullLinsener mSmartRefreshPullLinsener) {
        this.mSmartRefreshPullLinsener = mSmartRefreshPullLinsener;
    }

    private void initView(Context context) {
        container = (ViewGroup) LayoutInflater.from(context).inflate(
                R.layout.x_recyclerview_animation_header, null);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.bottomMargin = AppUtil.INSTANCE.dp2px(2f);
        params.topMargin = AppUtil.INSTANCE.dp2px(2f);
        addView(container, params);
        animation_iv = findViewById(R.id.animation_iv);
        animationDrawable = (AnimationDrawable) context.getResources().getDrawable(R.drawable.loading_anim);
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
        if (percent >= 0.4f) {
            int index = Math.round((percent - 0.4f) / ((1.0f - 0.4f) / animationDrawable.getNumberOfFrames()));
            if (index < 0) {
                index = 0;
            }
            if (index >= animationDrawable.getNumberOfFrames()) {
                index = animationDrawable.getNumberOfFrames() - 1;
            }
            Drawable currentFrame = animationDrawable.getFrame(index);
            animation_iv.setImageDrawable(currentFrame);
        }
    }


    @Override
    public void onReleased(RefreshLayout refreshLayout, int height, int extendHeight) {
        animation_iv.setImageDrawable(animationDrawable);
        animationDrawable.start();
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        animationDrawable.stop();
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
        if (newState == RefreshState.None) {//取消
            if (mSmartRefreshPullLinsener != null) {
                mSmartRefreshPullLinsener.onReleasing();
            }
        } else if (newState == RefreshState.PullDownToRefresh) {//开始刷新
            if (mSmartRefreshPullLinsener != null) {
                mSmartRefreshPullLinsener.onPulling();
            }
        }
    }
}
