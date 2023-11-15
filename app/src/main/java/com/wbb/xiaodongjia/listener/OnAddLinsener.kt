package com.wbb.xiaodongjia.listener

import com.wbb.base.bean.VideoPlayInfo

/**
 * Created by zj on 2020/3/18.
 */
interface OnAddRecommendLinsener {
    fun onAddRecommendData(dataList: List<VideoPlayInfo>?)
}