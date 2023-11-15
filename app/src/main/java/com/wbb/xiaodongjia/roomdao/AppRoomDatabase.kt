package com.wbb.xiaodongjia.roomdao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chengyun.parent.roomdao.AudilPlayScheduleDao
import com.chengyun.parent.roomdao.LastPlayDao
import com.wbb.xiaodongjia.roomdao.bean.AudioPlayScheduleInfo
import com.wbb.xiaodongjia.roomdao.bean.LastPlayInfo
import com.wbb.xiaodongjia.roomdao.bean.MsgDataInfo

/**
 * Created by zj on 2020/7/29.
 */
@Database(
    entities = [LastPlayInfo::class, AudioPlayScheduleInfo::class, MsgDataInfo::class],
    version = 1,
    exportSchema = false
)
abstract class AppRoomDatabase : RoomDatabase() {


    abstract fun getLastPlayInfoDao(): LastPlayDao

    abstract fun getAudioPlaySchduleinfoDao(): AudilPlayScheduleDao

}