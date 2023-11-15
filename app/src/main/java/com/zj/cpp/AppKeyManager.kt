package com.zj.cpp

/**
 * Created by zj on 2021/4/1.
 */
class AppKeyManager {
    init {
        System.loadLibrary("md5native-lib")
    }

    external fun httpKey(): String
    external fun stringToMd5JNI(origin: String?): String? //。
}