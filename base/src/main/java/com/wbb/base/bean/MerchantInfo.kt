package com.wbb.base.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * Created by zj on 2021/2/25.
 */
data class MerchantInfo(
    var type: Int = 0,
    var enable: Boolean = false,
    var merchantLabels: MutableList<LabInfo>? = null,
    var merchantStoreId: String = "",
    var name: String = "",
    var follow: Boolean = false,
    var merchantImages: MutableList<ImgInfo>? = null,
    var address: String = "",
    var vipNum: String = "",
    var recruit: Boolean = false,
    var recommend: CommenTools? = null,
    var coverImg: String = "",
    var latitude: Double? = null,
    var longitude: Double? = null
) : MultiItemEntity {
    override val itemType: Int
        get() = type

}

data class LabInfo(
    var bgColor: String?,
    var color: String? = "",
    var labelId: String = "",
    var labelName: String = ""
)
