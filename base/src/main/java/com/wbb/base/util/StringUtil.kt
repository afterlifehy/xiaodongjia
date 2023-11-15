package com.wbb.base.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.Spannable
import android.text.TextUtils
import android.widget.EditText
import com.wbb.base.BaseApplication
import com.wbb.base.R
import com.wbb.base.ext.i18n
import com.zrq.spanbuilder.Spans
import com.zrq.spanbuilder.TextStyle
import java.net.URLDecoder
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by zj on 2020/12/9.
 */
object StringUtil {
    /**
     * 判断是否是数字
     */
    fun isNumeric(str: String): Boolean {
        if (TextUtils.isEmpty(str)) {
            return false
        }
        val pattern = Pattern.compile("^[0-9]+(.[0-9]+)?\$")
        val isNum = pattern.matcher(str)
        if (isNum.matches()) {
            return true
        }
        return false
    }

    /**
     * 两个字符串包含个数
     */
    fun strContainCount(str1: String, str2: String): Int {
        return StringCountHelp().countStr(str1, str2)
    }

    /**
     * 输入内容校验小数点保留几位
     */
    fun inputAmountCheck(str: String, editText: EditText, mPrecision: Int): String {
        var mEmpStr = str
        if (TextUtils.isEmpty(str) || !str.contains(".")) {
            return mEmpStr
        }
        if (mPrecision <= 0) {//如果精度为0，直接干掉小数点
            if (str.indexOf(".") == 0) {
                mEmpStr = ""
            } else {
                val empStr = str
                mEmpStr = empStr.substring(0, str.indexOf("."))
            }

        } else {
            //如果输入的是小数点
            if (str.indexOf(".") == 0) {
                mEmpStr = "0."
            }
            val count = strContainCount(str, ".")
            if (count > 1) {//如果输入了两个. 默认只保留一个
                mEmpStr = str.substring(0, str.indexOf(".", 2))
            }
            if (count > 0) {
                //算出小数点后面有几位，用来限制位数
                val lastFew = (str.length - str.indexOf(".") - 1)
                if (lastFew > mPrecision) {
                    mEmpStr = BigDecimalUtils.toDownValue(str, mPrecision)
                }
            }
        }
        if (!str.equals(mEmpStr)) {
            editText.setText(mEmpStr)
            editText.setSelection(editText.text.length)
        }

        return mEmpStr
    }

    /**
     * 根据url获取参数
     */
    fun getUrlParameter(url: String): Map<String, String>? {
        val parMap = HashMap<String, String>()
        if (TextUtils.isEmpty(url) || !url.contains("?")) {
            return parMap
        }
        val indxe = url.indexOf("?")
        val newUrl = url.substring(indxe + 1)
        val keValue = newUrl.split("&")
        for (value in keValue) {
            val empVlaue = value.split("=")
            parMap.put(empVlaue[0], URLDecoder.decode(empVlaue[1], "UTF-8"))
        }
        return parMap

    }

    fun replaceStar(value: String): String? {
        if (!TextUtils.isEmpty(value) && value.length > 6) {
            val sb = StringBuilder()
            for (i in 0 until value.length) {
                val c = value[i]
                if (i >= 3 && i <= 6) {
                    sb.append('*')
                } else {
                    sb.append(c)
                }
            }
            return sb.toString()
        }
        return value
    }

    fun replaceAllStar(value: String): String? {
        var star = ""
        for (i in 0 until value.length) {
            star += "*"
        }
        return star
    }

    /**
     * 复制文本
     *
     * @param content
     */
    fun copyString(content: String?) {
        val cm = BaseApplication.instance()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 将文本内容放到系统剪贴板里。
        val clipData = ClipData.newPlainText("simple text copy", content)
        cm.setPrimaryClip(clipData)
        ToastUtils.showToash(i18n(R.string.复制成功))
    }

    //textview不同字体大小，颜色
    fun getSpan(strings: Array<String>, sizes: IntArray, colors: IntArray): Spannable? {
        val builder: Spans.Builder = Spans.builder()
        for (i in strings.indices) {
            builder.text(
                strings[i], sizes[i], BaseApplication.instance().getResources().getColor(
                    colors[i]
                )
            )
        }
        return builder.build()
    }

    //textview不同字体大小，颜色
    fun getSpan(
        strings: Array<String>,
        sizes: IntArray,
        colors: IntArray,
        textStyles: Array<TextStyle>
    ): Spannable? {
        val builder: Spans.Builder = Spans.builder()
        for (i in strings.indices) {
            if (!TextUtils.isEmpty(strings[i])) {
                builder.text(
                    strings[i], sizes[i], BaseApplication.instance().resources.getColor(
                        colors[i]
                    )
                ).style(textStyles[i])
            }
        }
        return builder.build()
    }

    /**
     *
     * 是否是中文
     *
     * @param c
     *
     * @return
     */
    fun isChinese(c: Char): Boolean {
        val ub = Character.UnicodeBlock.of(c)
        return if (ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
            ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION
            || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
        ) {
            true
        } else false
    }


    fun isChinese(str: String?): Boolean {
        val regEx = "[\\u4e00-\\u9fa5]+"
        val p = Pattern.compile(regEx)
        val m: Matcher = p.matcher(str)
        return if (m.find()) true else false
    }

}