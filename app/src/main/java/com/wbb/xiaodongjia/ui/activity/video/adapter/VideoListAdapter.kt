package com.wbb.xiaodongjia.ui.activity.video.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import com.wbb.base.emuns.SHARE_EMUN
import com.wbb.base.emuns.WEIXIN_SHAR_TYPE
import com.wbb.base.help.WxSharManager
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.bean.Video
import com.wbb.xiaodongjia.ui.activity.video.adapter.holder.VideoHodel
import com.wbb.xiaodongjia.ui.activity.video.videolist.BaseVideoListAdapter

/**
 * Created by zj on 2021/2/1.
 */
class VideoListAdapter(context: Context) : BaseVideoListAdapter<VideoHodel, Video>(context) {
    private var mOnSeekBarChangeListener: SeekBar.OnSeekBarChangeListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHodel {
        val mView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fragment_video_item_layout, parent, false)

        return VideoHodel(mView)
    }

    /**
     * 设置拖动进度
     */
    fun setOnSeekBarChangeListener(mOnSeekBarChangeListener: SeekBar.OnSeekBarChangeListener?) {
        this.mOnSeekBarChangeListener = mOnSeekBarChangeListener
    }

    override fun onBindViewHolder(holder: VideoHodel, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.sb_pro.setProgress(2)
        holder.iv_share.setOnClickListener {
//            WxSharManager.instance()
//                .showShareDialog(
//                    context,
//                    WxSharManager.ShareInfo(
//                        WEIXIN_SHAR_TYPE.TEXT,
//                        title = "分享图片",
//                        text = WxSharManager.ShareText(
//                            "https://water2021.oss-cn-shanghai.aliyuncs.com/1.png",
//                        )
//
//                    ),
//                    object :
//                        WxSharManager.OnShareResultLinsener {
//                        override fun onShareCancer() {
//                            Log.i("keey", "取消")
//                        }
//
//                        override fun onShareError() {
//                        }
//
//                        override fun onShareSucess() {
//                        }
//                    })


//            WxSharManager.instance()
//                .showShareDialog(
//                    context,
//                    WxSharManager.ShareInfo(
//                        WEIXIN_SHAR_TYPE.IMAGE,
//                        title = "分享图片",
//                        img = WxSharManager.ShareImg(
//                            "https://water2021.oss-cn-shanghai.aliyuncs.com/1.png",
//                            "https://water2021.oss-cn-shanghai.aliyuncs.com/1.png",
//                            "我是分享的图片",
//                        )
//
//                    ),
//                    object :
//                        WxSharManager.OnShareResultLinsener {
//                        override fun onShareCancer() {
//                            Log.i("keey", "取消")
//                        }
//
//                        override fun onShareError() {
//                        }
//
//                        override fun onShareSucess() {
//                        }
//                    })


            WxSharManager.instance()
                .showShareDialog(
                    context,
                    WxSharManager.ShareInfo(
                        WEIXIN_SHAR_TYPE.GRAPHIC,
                        title = "分享图片",
                        graphis = WxSharManager.ShareGraphic(
                            "https://gitee.com/",
                            "https://water2021.oss-cn-shanghai.aliyuncs.com/1.png",
                            "我是分享的图片",
                        )

                    ),object :
                        WxSharManager.OnShareResultLinsener {
                        override fun onShareCancer(mType: SHARE_EMUN) {
                        }

                        override fun onShareError(mType: SHARE_EMUN) {
                        }

                        override fun onShareSucess(mType: SHARE_EMUN) {
                        }
                    })
        }

        holder.sb_pro.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mOnSeekBarChangeListener?.onProgressChanged(seekBar, progress, fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mOnSeekBarChangeListener?.onStartTrackingTouch(seekBar)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mOnSeekBarChangeListener?.onStopTrackingTouch(seekBar)
            }
        })

    }

}
