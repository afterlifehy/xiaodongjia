package com.wbb.base.util

import android.text.TextUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by hy on 2020/11/18.
 */
object DateUtil {

    /**
     * 将时间戳转date
     */
    fun getLongToDate(pattern: String?, dateString: Long?): Date? {
        val format = SimpleDateFormat(pattern)
        val d = format.format(dateString)
        var date: Date? = null
        try {
            date = format.parse(d)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    fun getDataToLong(data: String?, format: String?): Long? {
        val simpleDateFormat = SimpleDateFormat(format)
        var date = Date()
        try {
            date = simpleDateFormat.parse(data)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date.time
    }

    /**
     * 将date转化成固定格式（默认 yyyy-MM-dd HH:mm:ss 当前时间 ）
     */
    fun getDateToString(format: String?, date: Date?): String? {
        var format = format
        var date = date
        if (TextUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss"
        }
        if (date == null) {
            date = Date()
        }
        val sdf = SimpleDateFormat(format)
        return sdf.format(date)
    }

    /**
     * string转date
     *
     * @param strTime
     * @param formatType
     * @return
     */
    fun getStringToDate(strTime: String?, format: String?): Date? {
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        var date: Date? = null
        try {
            date = formatter.parse(strTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    /**
     * 将固定格式转化成时间戳（默认 yyyy-MM-dd HH:mm:ss）
     */
    fun getStringToLong(format: String?, dateString: String?): Long {
        var format = format
        if (TextUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss"
        }
        val sdf = SimpleDateFormat(format)
        return try {
            val date = sdf.parse(dateString)
            date.time
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }

    //long转String
    fun getLongToString(date: Long, type: String?): String? {
        return SimpleDateFormat(type, Locale.getDefault())
            .format(Date(date))
    }

    //获得当天24点时间
    fun getTimesnight(): Long {
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = 24
        cal[Calendar.SECOND] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal.timeInMillis
    }

    /**
     * 时间戳转天小时分
     *
     * @param duration
     * 天：1 小时：2 分：3
     * @return
     */
    fun time2Date(duration: Long, type: Int): String? {
        var time: String? = ""
        val days = duration / (1000 * 60 * 60 * 24)
        val hours = duration % (1000 * 60 * 60 * 24) / 3600000
        val minute = duration % (1000 * 60 * 60) / 60000
        when (type) {
            1 -> {
                if (days < 10) {
                    time += "0"
                }
                time += days
            }
            2 -> {
                if (hours < 10) {
                    time += "0"
                }
                time += hours
            }
            3 -> {
                if (minute < 10) {
                    time += "0"
                }
                time += minute
            }
        }
        return time
    }
}