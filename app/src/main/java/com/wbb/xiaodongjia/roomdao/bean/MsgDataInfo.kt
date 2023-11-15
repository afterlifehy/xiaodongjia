package com.wbb.xiaodongjia.roomdao.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by zj on 2021/3/19.
 */
@Entity(tableName = "msg_list_table")
data class MsgDataInfo(
    @PrimaryKey
    var id: Int,
    var siteMessageId: String
) {
}