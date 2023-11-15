package com.wbb.base.mvvm.base;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by zj on 2021/2/23.
 */

public class UrlUtils {
    public static String getUrlBy(String url) {
        String path = "";
        try {
            path = new String(url.getBytes(), "utf-8");
            path = URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }
}
