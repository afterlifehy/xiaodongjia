package com.wbb.base.emuns

import com.wbb.base.R

/**
 * Created by zj on 2021/3/3.
 */
enum class SHARE_EMUN(var id: Int, var iconId: Int, var shareNam: String) {
    WEIXIN(1001, R.mipmap.weixin_icon, "微信"),
    CIRCLE_OF_FRIENDS(1002, R.mipmap.weixing_friends, "朋友圈"),
    COPY_URL(1003, R.mipmap.copy_url, "复制链接")

}