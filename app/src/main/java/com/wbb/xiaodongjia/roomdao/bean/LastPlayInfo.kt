package com.wbb.xiaodongjia.roomdao.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by zj on 2020/7/29.
 */
@Entity(tableName = "last_play_info")
data class LastPlayInfo(
        @PrimaryKey
        var id: Int,
        var addTime: Long,
        var type: Int,
        var typeStr: String = "",
        var userId: String = ""


) {

}