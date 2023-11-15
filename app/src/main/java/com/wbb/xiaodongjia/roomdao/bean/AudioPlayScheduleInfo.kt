package com.wbb.xiaodongjia.roomdao.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by zj on 2020/8/17.
 */
@Entity(tableName = "audio_play_schedule_info")
data class AudioPlayScheduleInfo(
        @PrimaryKey
        var id: String = "",
        var schedule: Long = 0,
        var name: String = "",
        var commodityId: Int = 0,
        var courseId: Int = 0,
        var userId: String = ""
) {
}