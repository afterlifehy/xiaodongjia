package com.wbb.xiaodongjia.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.launcher.ARouter
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.util.ARouterMap
import com.wbb.base.util.AppUtil
import com.wbb.base.util.MagicValue
import com.wbb.base.util.StatusBarUtils
import com.wbb.base.view.flycotablayout.listener.OnTabSelectListener
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.DongClassViewpagerAdapter
import com.wbb.xiaodongjia.base.VbBaseFragment
import com.wbb.xiaodongjia.databinding.FragmentEastClassBinding

/**
 * Created by hy on 2021/1/25.
 * 东家课堂
 */
class EastClassroomFragment : VbBaseFragment<BaseViewModel, FragmentEastClassBinding>(),
    View.OnClickListener {
    var tabTitleList: ArrayList<String> = ArrayList()
    var fragments: ArrayList<Fragment> = ArrayList()
    var classListFragment1: ClassListFragment? = null
    var classListFragment2: ClassListFragment? = null
    var dongClassViewpagerAdapter: DongClassViewpagerAdapter? = null
    var tabTitle: Array<String>? = null
    var currentPosition = 0

    override fun onReloadData() {
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun onGetClassTypeNam(): Any {
        return "东家课堂"
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_east_class
    }

    override fun initView() {
        binding.llContent.minimumHeight = StatusBarUtils.getStatusBarHeight() + AppUtil.dp2px(120f)

        classListFragment1 = ClassListFragment()
        val bundle = Bundle()
        bundle.putString("type", "D_JIA_UP")
        classListFragment1?.arguments = bundle

        classListFragment2 = ClassListFragment()
        val bundle2 = Bundle()
        bundle2.putString("type", "GOOD_COURSE")
        classListFragment2?.arguments = bundle2

        fragments.add(classListFragment1 as Fragment)
        fragments.add(classListFragment2 as Fragment)
        tabTitleList.add("东家进阶")
        tabTitleList.add("精品课")
        tabTitle = arrayOf("东家进阶", "精品课")

        dongClassViewpagerAdapter =
            DongClassViewpagerAdapter(childFragmentManager, fragments, tabTitleList)

        binding.vpDongClass.adapter = dongClassViewpagerAdapter

        binding.stlClass.setViewPager(binding.vpDongClass, tabTitle)
//        stl_class.setViewPager(vp_dongClass, tabTitleList, activity, fragments)

        binding.vpDongClass.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                setTabSelected(position)
                setTabUnSelected(currentPosition)
                currentPosition = position
            }

        })

        binding.stlClass.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {

                binding.vpDongClass.currentItem = position
                OnBuriedPointManager.get().getOnBuriedPointManager()
                    ?.ButtonClick("东家课堂", "东家课堂首页", if (position == 0) "东家进阶" else "精品课")
            }

            override fun onTabReselect(position: Int) {

            }

        })
        for (i in tabTitleList.indices) {
            setTabUnSelected(i)
        }
        setTabSelected(currentPosition)
        binding.rllSearch.setOnClickListener(this)
    }

    override fun initData() {

    }

    fun setTabSelected(position: Int) {
//        val layout_tab_selected_txt: RelativeLayout = LayoutInflater.from(BaseApplication.instance()).inflate(R.layout.layout_tab_selected_txt, null) as RelativeLayout
//        layout_tab_selected_txt.findViewById<TextView>(R.id.tv_tab).text = tabTitleList[position]
//        ((stl_class.getChildAt(0) as LinearLayout).getChildAt(position) as RelativeLayout).removeAllViews()
//        ((stl_class.getChildAt(0) as LinearLayout).getChildAt(position) as RelativeLayout).addView(layout_tab_selected_txt)
    }

    fun setTabUnSelected(position: Int) {
//        val layout_tab_unselected_txt: RelativeLayout = LayoutInflater.from(BaseApplication.instance()).inflate(R.layout.layout_tab_unselected_txt, null) as RelativeLayout
//        layout_tab_unselected_txt.findViewById<TextView>(R.id.tv_tab).text = tabTitleList[position]
//        ((stl_class.getChildAt(0) as LinearLayout).getChildAt(position) as RelativeLayout).removeAllViews()
//        ((stl_class.getChildAt(0) as LinearLayout).getChildAt(position) as RelativeLayout).addView(layout_tab_unselected_txt)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rll_search -> {
                ARouter.getInstance().build(ARouterMap.SEARCH)
                    .withString(ARouterMap.SEARCH_TYPE, MagicValue.SEARCH_TYPE_CLASS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return FragmentEastClassBinding.inflate(layoutInflater)
    }
}