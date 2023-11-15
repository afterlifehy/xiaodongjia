package com.wbb.xiaodongjia.roomdao.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by zj on 2020/8/14.
 */
@Entity(tableName = "first_ai_play_info")
data class AiPlayInfo(
        @PrimaryKey
        var id: Int,
        var time: Long) {
}