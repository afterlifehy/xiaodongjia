package com.wbb.xiaodongjia.ui.activity.video.videolist;

/**
 * @author zsy_18 result:2018/12/10
 */
public enum VideoSourceType {
    /**
     * 选用url播放
     */
    TYPE_URL,
    /**
     * 选用凭证播放
     */
    TYPE_AUTH,
    /**
     * 选用sts方式播放
     */
    TYPE_STS,
    /**
     * 选用直播的方式播放
     */
    TYPE_LIVE,
    /**
     * 错误的视频，不在列表中显示
     */
    TYPE_ERROR_NOT_SHOW
}
