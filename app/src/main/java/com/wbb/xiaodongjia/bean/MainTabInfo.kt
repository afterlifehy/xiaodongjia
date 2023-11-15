package com.wbb.xiaodongjia.bean

import androidx.fragment.app.Fragment

/**
 * Created by zj on 2021/1/25.
 */
data class MainTabInfo(
    var tabName: String = "",
    var mFragment: Fragment,
    var imageAssetsFolder: String = "",
    var mAnimationName: String = ""
) {
}