package com.wbb.base.util;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.wbb.base.R;

/**
 * Created by zj on 2019/4/1.
 */
public class GlideUtils {
    private static GlideUtils mGlideUtils;
    private static RequestOptions options;

    private GlideUtils() {
        initOption();
    }

    public static GlideUtils getInstance() {
        if (mGlideUtils == null) {
            synchronized (GlideUtils.class) {
                if (mGlideUtils == null) {
                    mGlideUtils = new GlideUtils();
                }
            }
        }
        return mGlideUtils;
    }

    /**
     * 初始化一个默认的配置
     */
    private void initOption() {
        options = new RequestOptions();
        options.error(R.drawable.img_defalut_bg).placeholder(R.drawable.img_defalut_bg);
        /*
            DiskCacheStrategy.NONE： 表示不缓存任何内容。
            DiskCacheStrategy.DATA： 表示只缓存原始图片。
            DiskCacheStrategy.RESOURCE： 表示只缓存转换过后的图片。
            DiskCacheStrategy.ALL ： 表示既缓存原始图片，也缓存转换过后的图片。
            DiskCacheStrategy.AUTOMATIC： 表示让Glide根据图片资源智能地选择使用哪一种缓存策略（默认选项）
         */
        options.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
    }

    /**
     * 返回默认的配置
     *
     * @return
     */
    public RequestOptions getDefaultOption() {
        return options;
    }

    public void loadImage(ImageView iv, String url) {
        Glide.with(iv).load(url).apply(getDefaultOption()).into(iv);
    }

    public void loadImage(ImageView iv, int url) {
        Glide.with(iv).load(url).apply(getDefaultOption()).into(iv);
    }

    public void loadImage(ImageView iv, String url, int placeholder) {
        RequestOptions options = new RequestOptions().error(placeholder).placeholder(placeholder).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(iv).load(url).apply(options).into(iv);
    }

    public void loadImage(ImageView iv, int url, int placeholder) {
        RequestOptions options = new RequestOptions().error(placeholder).placeholder(placeholder).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(iv).load(url).apply(options).into(iv);
    }

    public void loadImage(ImageView iv, String url, int placeholder, int mRadius) {
        RequestOptions options = RequestOptions.noAnimation().transform(new CropRoundRadiusTransformation(iv.getContext(), mRadius)).error(placeholder).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(iv).load(url).apply(options).into(iv);
    }


}


