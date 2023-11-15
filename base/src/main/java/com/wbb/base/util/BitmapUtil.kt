package com.wbb.base.util

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * Created by zj on 2021/3/3.
 */
object BitmapUtil {

    /**
     * 缩放Bitmap对象
     *
     * @param srcBmp Bitmap对象
     * @param width  缩放后的宽度
     * @param height 缩放后的高度
     * @return 缩放后的Bitmap对象
     */
    fun resizeBitmap(srcBmp: Bitmap, width: Int, height: Int): Bitmap? {
        var dstBmp: Bitmap? = null
        if (isBitmapAvailable(srcBmp)) {
            val w = srcBmp.width
            val h = srcBmp.height
            val matrix = Matrix()
            matrix.postScale(width.toFloat() / w, height.toFloat() / h)
            dstBmp = Bitmap.createBitmap(srcBmp, 0, 0, w, h, matrix, true)
        }
        return dstBmp
    }

    /**
     * 判断Bitmap对象是否有效
     *
     * @param bmp Bitmap对象
     * @return true if bitmap is not null and not be recycled
     */
    fun isBitmapAvailable(bmp: Bitmap?): Boolean {
        return bmp != null && !bmp.isRecycled
    }
}