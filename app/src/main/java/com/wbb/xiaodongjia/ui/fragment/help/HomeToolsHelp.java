package com.wbb.xiaodongjia.ui.fragment.help;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.wbb.base.help.MyClassicsHeader;
import com.wbb.base.help.SmartRefreshPullLinsener;
import com.wbb.base.util.AppUtil;
import com.wbb.base.util.StatusBarUtils;
import com.wbb.base.view.MyRefreshHeader;
import com.wbb.xiaodongjia.R;

/**
 * Created by zj on 2019/9/17.
 * 这个帮助类是用来控制toobar的显示或者隐藏
 */
public class HomeToolsHelp implements SmartRefreshPullLinsener {
    private AppBarLayout apl_bt;
    private Toolbar home_toolbar;//tilte
    private Context context;
    //上一次滑动的距离
    //搜索框
    private LinearLayout ll_sec;
    private ImageView iv_call_phone;//电话图标
    private ImageView iv_mesg;//消息图标
    private TextView tv_city_name;//地址名称
    private ImageView iv_addr_icon;//地址图标
    private View title_top;//一个用来吸顶的VIEW
    private LinearLayout ll_content;//
    private LinearLayout ll_title_content;//title的高度
    SmartRefreshLayout store_house_ptr_frame;//刷新控件
    com.wbb.xiaodongjia.databinding.FragmentHomeLayoutBinding mFragmentHomeLayoutBinding;

    public HomeToolsHelp(Context context, com.wbb.xiaodongjia.databinding.FragmentHomeLayoutBinding mFragmentHomeLayoutBinding) {
        this.context = context;
        this.mFragmentHomeLayoutBinding = mFragmentHomeLayoutBinding;
        findView();
        init();
        initToolBg();
        setSerHeight();
    }

    private void findView() {

        home_toolbar = mFragmentHomeLayoutBinding.homeTopHead.homeToolbar;
        apl_bt = mFragmentHomeLayoutBinding.aplBt;
        ll_sec = mFragmentHomeLayoutBinding.homeTopHead.llSec;
        iv_call_phone = mFragmentHomeLayoutBinding.homeTopHead.ivCallPhone;
        iv_mesg = mFragmentHomeLayoutBinding.homeTopHead.ivMesg;
        tv_city_name = mFragmentHomeLayoutBinding.homeTopHead.tvCityName;
        iv_addr_icon = mFragmentHomeLayoutBinding.homeTopHead.ivAddrIcon;
        title_top = mFragmentHomeLayoutBinding.homeTopHead.titleTop;
        store_house_ptr_frame = mFragmentHomeLayoutBinding.homeSmar;
        ll_content = mFragmentHomeLayoutBinding.llContent;
        ll_title_content = mFragmentHomeLayoutBinding.homeTopHead.llTitleContent;

    }

    /**
     * 设置搜索框距离屏幕上面的高度,适配齐刘海
     */
    private void setSerHeight() {
        int viewHeight = StatusBarUtils.getStatusBarHeight();
        ViewGroup.LayoutParams layoutParams = title_top.getLayoutParams();
        layoutParams.height = viewHeight;
        title_top.setLayoutParams(layoutParams);
        ll_title_content.post(new Runnable() {
            @Override
            public void run() {
                int minHeight = (ll_title_content.getHeight() - AppUtil.INSTANCE.dp2px(15f));
                ll_content.setMinimumHeight(minHeight);
            }
        });

    }

    private void init() {


        store_house_ptr_frame.post(new Runnable() {//要延迟去获取,否则拿不到
            @Override
            public void run() {
                RefreshHeader refreshHeader = store_house_ptr_frame.getRefreshHeader();
                if (refreshHeader instanceof MyRefreshHeader) {
                    MyRefreshHeader mSmartRefreshHeader = (MyRefreshHeader) refreshHeader;
                    mSmartRefreshHeader.setmSmartRefreshPullLinsener(HomeToolsHelp.this);

                }
            }
        });
        apl_bt.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                //verticalOffset始终为0以下的负数
                // float percent = (Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange());
                float percent = (Math.abs(verticalOffset * 1.0f) / 500);
                if (verticalOffset > 500 || verticalOffset < -500 || percent > 1) {//不用滚动到全部
                    percent = 1;
                }
                setIcon(percent);
                home_toolbar.setBackgroundColor(changeAlpha(Color.WHITE, percent));
                title_top.setBackgroundColor(changeAlpha(Color.WHITE, percent));

            }
        });

    }


    /**
     * 设置图标
     *
     * @param percent
     */
    private void setIcon(float percent) {
        if (percent > 0.5) {
            iv_call_phone.setImageResource(R.mipmap.new_home_call_blak);
            iv_mesg.setImageResource(R.mipmap.not_msg);
            iv_addr_icon.setImageResource(R.mipmap.homt_city_icon);
            tv_city_name.setTextColor(context.getResources().getColor(R.color.color_171B20));
            ll_sec.setBackgroundResource(R.drawable.shape_home_search_new_bl_bg);

        } else {
            iv_call_phone.setImageResource(R.mipmap.new_home_call);
            iv_mesg.setImageResource(R.mipmap.not_msg);
            tv_city_name.setTextColor(context.getResources().getColor(R.color.color_171B20));
            iv_addr_icon.setImageResource(R.mipmap.homt_city_icon);
            ll_sec.setBackgroundResource(R.drawable.shape_home_search_new_bg);

        }
    }

    /**
     * 根据百分比改变颜色透明度
     */
    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }

    private void initToolBg() {
        // 获取Drawable对象
        Drawable mDrawable = ContextCompat.getDrawable(context, R.drawable.home_tools_bg);
        if (mDrawable == null) {
            return;
        }
        // 设置Drawable的透明度
        mDrawable.setAlpha(0);
        home_toolbar.setBackground(mDrawable);
    }

    @Override
    public void onPulling() {
        home_toolbar.setVisibility(View.GONE);
    }

    @Override
    public void onReleasing() {

        home_toolbar.setVisibility(View.VISIBLE);

    }
}
