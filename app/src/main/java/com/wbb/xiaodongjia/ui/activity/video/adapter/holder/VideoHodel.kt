package com.wbb.xiaodongjia.ui.activity.video.adapter.holder

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.wbb.xiaodongjia.R

/**
 * Created by zj on 2021/2/1.
 */
class VideoHodel(mView: View) : BaseHolder(mView) {
    var img_thumb: ImageView
    var mRootView: RelativeLayout
    var iv_play_icon: ImageView
    var sb_pro: SeekBar
    var iv_share: ImageView
    override fun getCoverView(): ImageView {
        return img_thumb
    }

    override fun getContainerView(): ViewGroup {
        return mRootView
    }

    override fun getPlayIcon(): ImageView {
        return iv_play_icon
    }

    override fun getSeekBarSchedule(): SeekBar {
        return sb_pro
    }

    init {
        img_thumb = mView.findViewById(R.id.img_thumb)
        mRootView = mView.findViewById(R.id.ll_root_view)
        iv_play_icon = mView.findViewById(R.id.iv_play_icon)
        sb_pro = mView.findViewById(R.id.sb_pro)
        iv_share = mView.findViewById(R.id.iv_share)
    }
}