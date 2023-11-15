package com.chengyun.parent.roomdao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wbb.xiaodongjia.roomdao.bean.AudioPlayScheduleInfo

/**
 * Created by zj on 2020/8/14.
 */
@Dao
interface AudilPlayScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlayScheduleInfo(mAudioPlayScheduleInfo: AudioPlayScheduleInfo)


    @Query("SELECT * FROM audio_play_schedule_info WHERE id LIKE :eid AND userId LIKE :userId")
    fun queryPlayScheduleInfo(eid: String?, userId: String): AudioPlayScheduleInfo
}