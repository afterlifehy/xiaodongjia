package com.wbb.base.wiget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wbb.base.R;
import com.wbb.base.bean.HomeRecommendInfo;
import com.wbb.base.util.AppUtil;

import java.util.List;


/**
 * Created by nl. on 16/6/2.
 */
public class TopSlidingTabs extends HorizontalScrollView {

    // @formatter:off
    private static final int[] ATTRS = new int[]{
            android.R.attr.textSize,
            android.R.attr.textColor
    };
    // @formatter:on

    private boolean isIndicatorFillTab = false;
    private boolean shouldExpand = false;
    private boolean isShowUnderline = false;
    private boolean isSelectedBold = false;
    private boolean isSelectedCenter = true; // 选中项是否居中
    private int scrollOffset = 52;
    private int selectedColor;
    private int indicatorColor = 0xFF666666;
    private int indicatorHeight = 5;
    private int underlineHeight = 0;
    private int underlineColor = 0x1A000000;
    private int verticalPadding = 10; // dp
    private int horizontalTabMargin = 10; // dp
    private int mSelectedTextSize;

    private int tabTextSize = 12;
    private int tabTextColor = 0xFF666666;

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private LinearLayout tabsContainer;

    private int tabCount;

    private int currentPosition = 0;

    private Paint rectPaint;

    private int lastScrollX = 0;

    private LinearGradient linearGradient;
    private boolean isGradientIndicator;
    private Matrix matrix = new Matrix();
    private RectF rectF = new RectF();
    private int indicatorRadius = 0;
    private Paint gradientPaint;
    private int linearGradientWidth;

    private int textBgDrawableSelector;
    private int tabLeftRightPadding;
    private int tabTopBottomPadding;

    public TopSlidingTabs(Context context) {
        this(context, null);
    }

    public TopSlidingTabs(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopSlidingTabs(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        verticalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, verticalPadding, dm);
        horizontalTabMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, horizontalTabMargin, dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

        // get system attrs (android:textSize and android:textColor)

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
        tabTextColor = a.getColor(1, tabTextColor);

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.TopSlidingTabs);

        selectedColor = a.getColor(R.styleable.TopSlidingTabs_tstSelectedColor, tabTextColor);
        indicatorColor = a.getColor(R.styleable.TopSlidingTabs_tstIndicatorColor, indicatorColor);
        indicatorHeight = a.getDimensionPixelSize(R.styleable.TopSlidingTabs_tstIndicatorHeight, indicatorHeight);
        verticalPadding = a.getDimensionPixelSize(R.styleable.TopSlidingTabs_tstVerticalPadding, verticalPadding);
        horizontalTabMargin = a.getDimensionPixelSize(R.styleable.TopSlidingTabs_tstHorizontalTabMargin, horizontalTabMargin);
        isShowUnderline = a.getBoolean(R.styleable.TopSlidingTabs_tstIsShowUnderline, isShowUnderline);
        if (isShowUnderline) {
            underlineColor = a.getColor(R.styleable.TopSlidingTabs_tstUnderlineColor, underlineColor);
            underlineHeight = a.getDimensionPixelSize(R.styleable.TopSlidingTabs_tstUnderlineHeight, underlineHeight);
        } else {
            underlineHeight = 0;
        }

        isIndicatorFillTab = a.getBoolean(R.styleable.TopSlidingTabs_tstIsIndicatorFillTab, isIndicatorFillTab);
        mSelectedTextSize = a.getDimensionPixelSize(R.styleable.TopSlidingTabs_tstSelectedTextSize, mSelectedTextSize);

        isSelectedBold = a.getBoolean(R.styleable.TopSlidingTabs_tstIsSelectedBold, isSelectedBold);
        isSelectedCenter = a.getBoolean(R.styleable.TopSlidingTabs_tstIsSelectedCenter, isSelectedCenter);
        if (!isSelectedCenter) { // 如果不要求选中项居中，再去读取偏移量
            scrollOffset = a.getDimensionPixelSize(R.styleable.TopSlidingTabs_tstScrollOffset, scrollOffset);
        }
        shouldExpand = a.getBoolean(R.styleable.TopSlidingTabs_tstShouldExpand, shouldExpand);

        textBgDrawableSelector = a.getResourceId(R.styleable.TopSlidingTabs_tstTabBackgroundSelector, 0);
        tabLeftRightPadding = a.getDimensionPixelSize(R.styleable.TopSlidingTabs_tstTabLeftRightPadding, 0);
        tabTopBottomPadding = a.getDimensionPixelSize(R.styleable.TopSlidingTabs_tstTabTopBottomPadding, 0);
        a.recycle();

        if (verticalPadding != 0) {
            tabsContainer.setPadding(0, verticalPadding, 0, verticalPadding);
        }

        textPaint = new Paint();
        textPaint.setTextSize(tabTextSize);

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.FILL);

        gradientPaint = new Paint();
        gradientPaint.setAntiAlias(true);
        gradientPaint.setStyle(Paint.Style.FILL);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        defaultTabLayoutParams.leftMargin = defaultTabLayoutParams.rightMargin = horizontalTabMargin;
        expandedTabLayoutParams.leftMargin = expandedTabLayoutParams.rightMargin = horizontalTabMargin;

        if (isInEditMode()) {
            setTabs(new String[]{"Tab1", "Tab2", "Tab3"});
            setCheckedIndex(0);
        }

    }


    private String[] tabsData;
    private List<HomeRecommendInfo> mList;

    public void setHomeList(List<HomeRecommendInfo> mList) {
        this.mList = mList;
        String[] tabsText = new String[mList.size()];
        for (int i = 0; i < mList.size(); i++) {
            HomeRecommendInfo homeRecommendInfo = mList.get(i);
            tabsText[i] = homeRecommendInfo.getName();
        }
        setTabs(tabsText);
    }

    public void setTabs(String[] data) {
        currentPosition = 0;
        tabsData = data;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {

        tabsContainer.removeAllViews();

        tabCount = tabsData.length;

        for (int i = 0; i < tabCount; i++) {
            addTab(i, tabsData[i]);
        }

        updateTabStyles();

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollToChild(currentPosition, 0);
                    }
                }, 300);

            }
        });

    }

    private float lastLeft, lastRight;
    private float ratio = 1.0f;

    private final float INDICATOR_SPEED_RATE = 0.2f;

    private final int MSG_WHAT_SCROLL_INDICATOR = 0x000088;

    private Handler indicatorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_WHAT_SCROLL_INDICATOR:
                    post(animateRunnable);
                    break;
            }
        }
    };

    private Runnable animateRunnable = () -> {
        ratio += INDICATOR_SPEED_RATE;
        ratio = ratio > 1.0f ? 1.0f : ratio;
        invalidate();
        if (ratio < 1.0f) {
            indicatorHandler.sendEmptyMessage(MSG_WHAT_SCROLL_INDICATOR);
        }
    };

    private void addTab(final int position, String title) {

        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();

        tab.setFocusable(true);
        if (mList != null && mList.size() > 0) {

        }

        tab.setOnClickListener(v -> {
            if (currentPosition == position) {
                return;
            }

            if (shouldInterceptItemSelect(position)) {
                return;
            }


            TextView lastTab = (TextView) tabsContainer.getChildAt(currentPosition);
            lastLeft = lastTab.getLeft();
            lastRight = lastTab.getRight();
            if (shouldExpand) {
                float textLength = textPaint.measureText(lastTab.getText().toString());
                float textIndent = ((lastRight - lastLeft) - textLength) / 2;
                lastLeft = lastLeft + textIndent;
                lastRight = lastRight - textIndent;
            }

            scrollToChild(position, 0);
            currentPosition = position;
            updateTabStyles();

            ratio = 0f;
            indicatorHandler.sendEmptyMessage(MSG_WHAT_SCROLL_INDICATOR);

            if (onTabSelectedListener != null) {
                onTabSelectedListener.onTabSelected(currentPosition);
            }

        });

        int top = tabTopBottomPadding == 0 ? verticalPadding / 2 : tabTopBottomPadding;
        int bottom = tabTopBottomPadding == 0 ? verticalPadding : tabTopBottomPadding;
        tab.setPadding(tabLeftRightPadding, top, tabLeftRightPadding, bottom);
//        tab.setPadding(0, verticalPadding/2, 0, verticalPadding);
        tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);

        if (textBgDrawableSelector != 0) {
            tab.setBackgroundResource(textBgDrawableSelector);
        }
    }

    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);
            if (v instanceof TextView) {
                TextView tab = (TextView) v;
                // boolean 来决定是否选中的需要加粗
                if (isSelectedBold && i == currentPosition) {
                    tab.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    tab.setTypeface(Typeface.DEFAULT);
                }
                if (i == currentPosition) {
                    tab.setTextColor(selectedColor);
                } else {
                    tab.setTextColor(tabTextColor);
                }

                if (mSelectedTextSize != 0 && i == currentPosition) {
                    tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSelectedTextSize);
                } else {
                    tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                }

                if (textBgDrawableSelector != 0) {
                    if (i == currentPosition) {
                        tab.setSelected(true);
                    } else {
                        tab.setSelected(false);
                    }
                }
            }
        }
    }

    /**
     * 选中项文字大小
     *
     * @param size 单位px
     */
    public void setSelectedTextSize(int size) {
        mSelectedTextSize = size;
        updateTabStyles();
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
        updateTabStyles();
    }

    public void setTextBgDrawableResource(int textBgDrawableId) {
        if (textBgDrawableId != 0) {
            for (int i = 0; i < tabCount; i++) {
                View tab = tabsContainer.getChildAt(i);
                tab.setBackgroundResource(textBgDrawableId);
            }
        }
    }

    public void scrollToChild(int position, int offset) {
        if (tabCount == 0) {
            return;
        }
        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;
        if (position == 0) {
            newScrollX -= ((MarginLayoutParams) tabsContainer.getChildAt(position).getLayoutParams()).leftMargin;
        }
        if (position > 0 || offset > 0) {
            if (isSelectedCenter) {
                scrollOffset = getWidth() / 2 - tabsContainer.getChildAt(position).getWidth() / 2;
            }
            newScrollX -= scrollOffset;
        }
        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            smoothScrollTo(newScrollX, 0);
        }

    }

    public void setLinearGradient(int width) {
        isGradientIndicator = true;
        linearGradientWidth = width;
        linearGradient = new LinearGradient(0, 0, 1, 0, Color.parseColor("#ff4647"), Color.parseColor("#ff4242"), Shader.TileMode.CLAMP);
        gradientPaint.setShader(linearGradient);
        indicatorRadius = AppUtil.INSTANCE.dp2px( 12f);
    }

    private Paint textPaint;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (tabCount == 0) {
            return;
        }

        final int height = getHeight();

        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        if (!isIndicatorFillTab && shouldExpand) {
            float textLength = textPaint.measureText(((TextView) currentTab).getText().toString());
            float textIndent = ((lineRight - lineLeft) - textLength) / 2;
            lineLeft = lineLeft + textIndent;
            lineRight = lineRight - textIndent;
        }

        lineLeft = (lineLeft - lastLeft) * ratio + lastLeft;
        lineRight = (lineRight - lastRight) * ratio + lastRight;

        // draw indicator line
        if (isIndicatorFillTab) {
            if (isGradientIndicator) {
                drawLinearGradientIndicator(lineLeft, height - underlineHeight - indicatorHeight, lineRight, height - underlineHeight, canvas);
            } else {
                rectPaint.setColor(indicatorColor);
                canvas.drawRect(lineLeft, height - underlineHeight - indicatorHeight, lineRight, height - underlineHeight, rectPaint);
            }
        } else {
            if (isGradientIndicator) {
                drawLinearGradientIndicator(lineLeft, height - verticalPadding - indicatorHeight, lineRight, height - verticalPadding, canvas);
            } else {
                rectPaint.setColor(indicatorColor);
                canvas.drawRect(lineLeft, height - verticalPadding - indicatorHeight, lineRight, height - verticalPadding, rectPaint);
            }
        }

        // 是否有underline
        if (isShowUnderline) {
            rectPaint.setColor(underlineColor);
            canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);
        }

    }

    private void drawLinearGradientIndicator(float lineLeft, float top, float lineRight, float bottom, Canvas canvas) {
        boolean b = (lineRight - lineLeft) > linearGradientWidth;
        float minwidth = b ? linearGradientWidth : (lineRight - lineLeft);
        float realLeft = b ? (lineLeft + ((lineRight - lineLeft) - linearGradientWidth) / 2) : lineLeft;
        rectF.set(realLeft, top, realLeft + minwidth, bottom);
        matrix.reset();
        matrix.preTranslate(realLeft, 0);
        matrix.preScale(minwidth, 1);
        linearGradient.setLocalMatrix(matrix);
        canvas.drawRoundRect(rectF, indicatorRadius, indicatorRadius, gradientPaint);
    }

    public void setCheckedIndex(int index) {
        if (currentPosition == index) return;
        currentPosition = index;
        updateTabStyles();
        if (onTabSelectedListener != null) {
            onTabSelectedListener.onTabSelected(currentPosition);
        }
    }

    public void setCheckedIndexNoListener(int index) {
        if (currentPosition == index) return;
        currentPosition = index;
        updateTabStyles();
    }

    public int getCheckedIndex() {
        return currentPosition;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    /**
     * 选项少的时候，是否需要平分宽度
     *
     * @param shouldExpand
     */
    public void setShouldExpand(boolean shouldExpand) {
        if (this.shouldExpand != shouldExpand) {
            this.shouldExpand = shouldExpand;
            requestLayout();
        }
    }

    public boolean isShouldExpand() {
        return shouldExpand;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        textPaint.setTextSize(tabTextSize);
        updateTabStyles();
    }


    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getTextColor() {
        return tabTextColor;
    }


    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private OnTabSelectedListener onTabSelectedListener;

    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        this.onTabSelectedListener = onTabSelectedListener;
    }

    public interface OnTabSelectedListener {
        void onTabSelected(int selectedTabIndex);
    }

    public boolean shouldInterceptItemSelect(int position) {
        if (onInterceptListener != null) {
            return onInterceptListener.onIntercept(position);
        }
        return false;
    }

    private OnInterceptListener onInterceptListener;

    public void setOnInterceptListener(OnInterceptListener onInterceptListener) {
        this.onInterceptListener = onInterceptListener;
    }

    public interface OnInterceptListener {
        boolean onIntercept(int position);
    }

    public void setTextBgDrawableSelector(int textBgDrawableSelector) {
        this.textBgDrawableSelector = textBgDrawableSelector;
    }

    public void setTabLeftRightPadding(int tabLeftRightPadding) {
        this.tabLeftRightPadding = tabLeftRightPadding;
    }

    public void setTabTopBottomPadding(int tabTopBottomPadding) {
        this.tabTopBottomPadding = tabTopBottomPadding;
    }
}
