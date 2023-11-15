package com.wbb.base.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;


import com.wbb.base.BaseApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by zj on 2018/12/4 0004.
 */
public class AssetsUtils {
    public static String getAssetsString(String fileName) {
        Context applicationContext = BaseApplication.instance().getApplicationContext();
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = applicationContext.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 根据名称获取图片ID
     *
     * @param resourceName
     * @return
     */
    public static int getMipmapResourceID(String resourceName) {
        Context applicationContext = BaseApplication.instance().getApplicationContext();
        Resources res = applicationContext.getResources();
        int picid = res.getIdentifier(resourceName, "mipmap", applicationContext.getPackageName());
        return picid;

    }

    public static int getDrawableResourceID(String resourceName) {
        Context applicationContext = BaseApplication.instance().getApplicationContext();
        Resources res = applicationContext.getResources();
        int picid = res.getIdentifier(resourceName, "drawable", applicationContext.getPackageName());
        return picid;

    }
}
