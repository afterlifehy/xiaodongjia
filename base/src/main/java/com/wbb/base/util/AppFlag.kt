package com.wbb.base.util

import androidx.collection.ArrayMap
import com.wbb.base.mvvm.base.UrlManager

object AppFlag {

    val languageSymbolMap: MutableMap<String, String> = ArrayMap()

    init {
        languageSymbolMap[MagicValue.ZH] = "cn"
        languageSymbolMap[MagicValue.EN] = "en"
    }

    var languageTypeMap: MutableMap<String, String> = ArrayMap()

    init {
        languageTypeMap[MagicValue.ZH] = "-cn"
        languageTypeMap[MagicValue.EN] = ""
    }

    var languageList: MutableList<String> = object : ArrayList<String>() {
        init {
            add("中文简体")
            add("English")
        }
    }

    var languageListMap: MutableMap<String, String> = ArrayMap()

    init {
        languageListMap["中文简体"] = MagicValue.ZH
        languageListMap["English"] = MagicValue.EN
    }

    var languageListMap2: MutableMap<String, String> = ArrayMap()

    init {
        languageListMap2[MagicValue.ZH] = "中文简体"
        languageListMap2[MagicValue.EN] = "English"
    }

    var valuationMap: MutableMap<String, String> = ArrayMap()

    init {
        valuationMap["CNY"] = "（¥）"
        valuationMap["USD"] = "（$）"
    }

    var valuationSymbolMap: MutableMap<String, String> = ArrayMap()

    init {
        valuationSymbolMap["CNY"] = "¥"
        valuationSymbolMap["USD"] = "$"
    }

    var serverPathMap: MutableMap<Int, String> = ArrayMap()

    init {
        serverPathMap[1] = UrlManager.DEVELOP_HOST
        serverPathMap[2] = UrlManager.TEST_HOST
        serverPathMap[3] = UrlManager.PRE_HOST
        serverPathMap[4] = UrlManager.FORMLA_HOST
    }

    var h5PathMap: MutableMap<Int, String> = ArrayMap()

    init {
        h5PathMap[1] = Constant.h5_dev
        h5PathMap[2] = Constant.h5_test
        h5PathMap[3] = Constant.h5_pre
        h5PathMap[4] = Constant.h5_formal
    }

}