package com.wbb.xiaodongjia.listener

import com.wbb.xiaodongjia.bean.Video


interface PlayAuthListener {

    fun getPlayAuth(video: Video, position: Int)

}