package com.wbb.xiaodongjia.ui.activity.video

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.wbb.base.mvvm.base.BaseViewModel
import com.chouyou.waterbridge.base.DefaultFragmetnAttachActivity
import com.wbb.base.util.ARouterMap
import com.wbb.base.util.StatusBarUtils
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.ui.activity.video.videolist.VidePlayListFragment

/**
 * Created by zj on 2021/2/1.
 * 视频播放加载类
 */
@Route(path = ARouterMap.VIDEO_PLAY)
class VideoPlayActivity : DefaultFragmetnAttachActivity<BaseViewModel>() {
    override val loadFragment: Fragment?
        get() = VidePlayListFragment()

    override fun initData() {
    }

    companion object {
        //获取单个播放
        const val COURSE_SING = "course_sing"

        //获取播放列表
        const val COURSE_LIST = "course_list"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setTranslucentStatus(this)
        StatusBarUtils.setNavigationBarColor(this, R.color.black)
        //StatusBarUtils.setCommonUI(this, true)
    }


}