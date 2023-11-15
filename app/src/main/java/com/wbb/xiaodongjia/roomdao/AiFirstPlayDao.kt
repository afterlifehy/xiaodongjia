package com.wbb.xiaodongjia.roomdao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wbb.xiaodongjia.roomdao.bean.AiPlayInfo

/**
 * Created by zj on 2020/8/14.
 */
@Dao
interface AiFirstPlayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAiFirstPlayInfo(mAiPlayInfo: AiPlayInfo)


    @Query("SELECT * FROM first_ai_play_info WHERE id LIKE :eid ORDER BY time DESC LIMIT 0,1")
    fun queryCurrAiIsFirstLearn(eid: Int): AiPlayInfo
}