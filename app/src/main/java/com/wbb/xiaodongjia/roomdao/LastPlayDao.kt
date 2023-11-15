package com.chengyun.parent.roomdao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wbb.xiaodongjia.roomdao.bean.LastPlayInfo

/**
 * Created by zj on 2020/7/29.
 */
@Dao
interface LastPlayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLastPlayInfo(mLastPlayInfo: LastPlayInfo)

    @Query("SELECT * FROM last_play_info")
    fun queryLastPlayInfo(): List<LastPlayInfo>?


    @Query("SELECT * FROM last_play_info WHERE typeStr LIKE :typeP AND  userId LIKE :userId ORDER BY addTime DESC LIMIT 0,1")
    fun getTypeLastPlayinfo(typeP: String, userId: String): LastPlayInfo?

    //删全部
    @Query("DELETE FROM last_play_info")
    fun deleteAll()
}