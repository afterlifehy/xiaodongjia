package com.wbb.base.help;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

/**
 * Created by zj on 2021/1/25.
 */

public class MyClassicsHeader extends ClassicsHeader {
    private SmartRefreshPullLinsener mSmartRefreshPullLinsener;

    public MyClassicsHeader(Context context) {
        super(context);
    }

    public MyClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 这个是用来监听刷新的开始和完成
     *
     * @param mSmartRefreshPullLinsener
     */
    public void setmSmartRefreshPullLinsener(SmartRefreshPullLinsener mSmartRefreshPullLinsener) {
        this.mSmartRefreshPullLinsener = mSmartRefreshPullLinsener;
    }


    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        super.onStateChanged(refreshLayout, oldState, newState);
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
