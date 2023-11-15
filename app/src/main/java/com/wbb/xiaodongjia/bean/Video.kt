package com.wbb.xiaodongjia.bean

import com.aliyun.player.source.UrlSource
import com.aliyun.player.source.VidAuth
import com.aliyun.player.source.VidSts
import com.wbb.base.ext.toWan
import com.wbb.base.ext.toWan00
import com.wbb.xiaodongjia.ui.activity.video.videolist.IVideoSourceModel
import com.wbb.xiaodongjia.ui.activity.video.videolist.VideoSourceType

/**
 * Copyright (C), 2019-2020, 南通筹友
 * FileName: Video
 * Author: OnionMac by 张琦
 * Date: 2020-03-31 15:53
 * Description:
 */
data class Video(
    var id: Int,
    var videoId: String, //通过id 授权视频播放
    var imageUrl: String?,//封面图
    var videoUrl: String?,//播放地址
    var videoSn: String,
    var videoTitle: String,
    var userId: String?, //用户sn
    var userName: String?,//用户名字
    var userLogo: String?,//用户logo
    var isFollow: Int? = 1, //是否关注
    var commentCount: Int?,
    var isLike: Int?,
    var vidAuth: VidAuth,
    var likeCount: Int, var miraId: String = ""
) : IVideoSourceModel {

    override fun getSourceType(): VideoSourceType {
        return VideoSourceType.TYPE_URL
    }

    override fun getVidAuthSource(): VidAuth? {
        return vidAuth
    }

    override fun getUrlSource(): UrlSource? {
        val url = UrlSource()
        url.uri = videoUrl
        return url
    }

    override fun getVidStsSource(): VidSts? {
        val vid = VidSts()
        vid.vid = videoId
        return vid
    }

    override fun getFirstFrame(): String? {
        return imageUrl
    }

    override fun getUUID(): String {
        return videoId
    }

    fun getLike(): String {

        if (likeCount < 10000) {
            return "${likeCount}"
        }

        return "${likeCount.toString().toWan()}"
    }

    fun getComment(): String {

        if (commentCount!! < 10000) {
            return "${commentCount}"
        }

        return "${commentCount.toString().toWan()}"
    }

    companion object {
        const val WIDTH = 110F
        const val HEIGHT = 150F
        const val LIKE = 1 //点赞了
        const val UNLIKE = 0 //没有点赞
    }

    val xLikeCount: String?
        get() {
            if (likeCount < 10000) {
                return "$likeCount"
            }
            return "${likeCount.toString().toWan00()}w"
        }
}