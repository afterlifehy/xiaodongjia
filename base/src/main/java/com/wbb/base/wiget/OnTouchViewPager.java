package com.wbb.base.wiget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * 可以禁止滑动的viewpager  默认不可滑动
 * @author yuancl@baihe.com
 *
 */
public class OnTouchViewPager extends ViewPager {

	private boolean scrollAble = true;

	public OnTouchViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public OnTouchViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
//		if (!scrollAble) {
//			return true;
//		}
		return super.onTouchEvent(ev);
	}

	public void setScrollAble(boolean b) {
		this.scrollAble = b;
	}

}
