package com.wbb.base.viewbase

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import androidx.annotation.Nullable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

/**
 * Created by zj on 2020/11/30.
 */
class DownloadImgBitmap {

    fun downLoadImg(
        context: Context,
        url: String,
        mOnDownloadImgBitmapLinsener: OnDownloadImgBitmapLinsener? = null
    ) {
        if (TextUtils.isEmpty(url)) {
            mOnDownloadImgBitmapLinsener?.onImgDownloadError("url为空")
            return
        }
        Glide.with(context).asBitmap().load(url).listener(object : RequestListener<Bitmap?> {
            override fun onLoadFailed(
                @Nullable e: GlideException?,
                model: Any,
                target: Target<Bitmap?>,
                isFirstResource: Boolean
            ): Boolean {
                mOnDownloadImgBitmapLinsener?.onImgDownloadError(e.toString())
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any,
                target: Target<Bitmap?>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        }).into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                mOnDownloadImgBitmapLinsener?.onImgDownloadSucess(resource)
            }
        })
    }

    interface OnDownloadImgBitmapLinsener {
        fun onImgDownloadSucess(bitmap: Bitmap)
        fun onImgDownloadError(errorMsg: String)
    }
}