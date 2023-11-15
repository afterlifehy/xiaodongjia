package com.wbb.base.bean

/**
 * Created by zj on 2021/2/1.
 */
data class User(
    var userId: String = "",
    var isWater: Boolean = false
) {
    companion object {
        const val FOLLOW = 1 //关注了
        const val UNFOLLOW = 0 //未关注

        const val BLOCK = 1 //拉黑了
        const val UNBLOCK = 0 //未拉黑

        const val MALE = "male"
        const val FEMALE = "female"
    }
}
