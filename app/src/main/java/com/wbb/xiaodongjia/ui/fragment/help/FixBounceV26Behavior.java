package com.wbb.xiaodongjia.ui.fragment.help;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.OverScroller;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.appbar.AppBarLayout;

import java.lang.reflect.Field;

/**
 * 功能详细描述：在26版本的以上，谷歌解决了之前存在已久的一个问题：AppBarLayout、CollapsingToolbarLayout和RecyclerView共存时，无法通过fling快速展开AppBarLayout
 * 但是随之而来的是一个新问题，当给AppBarlayout施加一个向上的fling后在自行滑动未结束前给下方的RecyclerView施加一个向下的fling会导致抖动
 */

public class FixBounceV26Behavior extends AppBarLayout.Behavior {

    private OverScroller mScroller1;

    public FixBounceV26Behavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        bindScrollerValue(context);
    }

    /**
     * 反射注入Scroller以获取其引用
     *
     * @param context
     */
    private void bindScrollerValue(Context context) {
        if (mScroller1 != null) return;
        mScroller1 = new OverScroller(context);
        try {
            Class<?> clzHeaderBehavior = getClass().getSuperclass().getSuperclass();
            Field fieldScroller = clzHeaderBehavior.getDeclaredField("mScroller");
            fieldScroller.setAccessible(true);
            fieldScroller.set(this, mScroller1);
        } catch (Exception e) {}
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed, int type) {
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            //上滑appbar然后迅速下滑list时, HeaderBehavior的mScroller并未停止, 会导致上下来回晃动？？
            if (mScroller1.computeScrollOffset()) {
                mScroller1.abortAnimation();
            }
            //当target滚动到边界时主动停止target
            if (getTopAndBottomOffset() == 0) {
                ViewCompat.stopNestedScroll(target, type);
            }
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }
}

    

