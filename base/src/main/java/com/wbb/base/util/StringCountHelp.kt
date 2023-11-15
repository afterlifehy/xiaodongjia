package com.wbb.base.util

/**
 * Created by zj on 2020/12/16.
 */
class StringCountHelp {
    var counter = 0

    /**
     * 判断str1中包含str2的个数
     * @param str1
     * @param str2
     * @return counter
     */
    fun countStr(str1: String, str2: String): Int {
        if (str1.indexOf(str2) == -1) {
            return 0
        } else if (str1.indexOf(str2) != -1) {
            counter++
            countStr(
                str1.substring(
                    str1.indexOf(str2) +
                            str2.length
                ), str2
            )
            return counter
        }
        return counter
    }
}