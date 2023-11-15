package com.wbb.base.bean

/**
 * Created by zj on 2021/3/19.
 */
data class MsgData(
    var acceptUserId: String = "",
    var acceptUserName: String = "",
    var message: String = "",
    var readStatus: String = "",
    var siteMessageId: String = "",
    var title: String = "",
    var siteMessageType: String = ""
) {
}