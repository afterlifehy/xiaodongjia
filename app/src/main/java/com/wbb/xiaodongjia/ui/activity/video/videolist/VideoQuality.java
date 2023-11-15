package com.wbb.xiaodongjia.ui.activity.video.videolist;

/**
 * @author zsy_18 result:2019/4/17
 */
public enum VideoQuality {
    /**
     *
     */
    DEFAULT("OD"),
    /**
     *
     */
    DOWNLOAD("LD"),
    VIDEO("video"),
    /**
     *
     */
    PLAY("FD");

    VideoQuality(String value) {
        this.mValue = value;
    }
    private String mValue;

    public String getValue() {
        return mValue;
    }
}
