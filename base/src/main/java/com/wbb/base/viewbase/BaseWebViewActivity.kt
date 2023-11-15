package com.wbb.base.viewbase

import android.graphics.Bitmap
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.util.FileAccessor
import com.wbb.base.util.ToastUtils
import java.util.*

/**
 * Created by zj on 2020/11/30.
 */
abstract class BaseWebViewActivity<VM : BaseViewModel> : BaseDataActivityKt<VM>(), DownloadImgBitmap.OnDownloadImgBitmapLinsener {

    override fun onImgDownloadError(errorMsg: String) {
        ToastUtils.showToash(errorMsg)
    }

    override fun onImgDownloadSucess(bitmap: Bitmap) {
        FileAccessor.saveBitmap(bitmap, FileAccessor.DCIM)
        ToastUtils.showToash("图片保存成功")
    }

    fun getSortJson(json: org.json.JSONObject): String? {
        val iteratorKeys: Iterator<String> = json.keys().iterator()
        val map: TreeMap<String?, String?> = TreeMap()
        while (iteratorKeys.hasNext()) {
            val key = iteratorKeys.next()
            val value = json.getString(key)
            map[key] = value
        }
        var sort = ""
        val keySets: List<String> = ArrayList<String>(map.keys)
        for (i in 0 until map.size) {
            val key = keySets[i]
            val value = map[key].toString()
            sort += key + value
        }
        return sort
    }
}