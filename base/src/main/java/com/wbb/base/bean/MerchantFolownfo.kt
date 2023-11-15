package com.wbb.base.bean

/**
 * Created by zj on 2021/2/27.
 */
data class MerchantFolownfo(
    var followId: String = "",
    var id: String = "",
    var mid: String = "",
    var type: Int = 1,
    var merchantSearchVO: MerchantInfo? = null,
    var activity: FolownLifeCircleActivityBean? = null,
    var course: CourseItemInfo? = null
) {
}