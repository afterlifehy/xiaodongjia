package com.wbb.base.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.wbb.base.BaseApplication
import com.wbb.base.R

/**
 * Created by hy on 2021/1/20.
 */
object GlideUtil {
    private val placeholder_normal: Int = R.mipmap.ic_app_logo
    private val placeholder_long: Int = R.mipmap.ic_app_logo

    fun loadImagePreview(url: String?, imageView: ImageView) {
        imageView.tag = null
        val options: RequestOptions = RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565)
            .placeholder(GlideUtil.placeholder_normal) //占位图
            .error(GlideUtil.placeholder_normal) //错误图
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(BaseApplication.instance()).asBitmap().load(url).apply(options).into(imageView)
    }

    fun loadImagePreview(url: Int?, imageView: ImageView) {
        imageView.tag = null
        val options: RequestOptions = RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565)
            .placeholder(GlideUtil.placeholder_normal) //占位图
            .error(GlideUtil.placeholder_normal) //错误图
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(BaseApplication.instance()).asBitmap().load(url).apply(options).into(imageView)
    }

    /*
     *加载图片(默认)int，centerCrop，有占位图
     */
    fun loadImage(id: Int, imageView: ImageView) {
        imageView.tag = null
        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .format(DecodeFormat.PREFER_RGB_565)
            .placeholder(GlideUtil.placeholder_normal) //占位图
            .error(GlideUtil.placeholder_normal) //错误图
            // .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(BaseApplication.instance()).asBitmap().load(id).apply(options).into(imageView)
    }
}