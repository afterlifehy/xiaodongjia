package com.wbb.base.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import com.wbb.base.BaseApplication
import com.wbb.base.help.ActivityCacheManager
import com.wbb.base.view.dsbridge.DWebView
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.text.DecimalFormat

/**
 * Created by hy on 2021/1/20.
 */
object AppUtil {

    fun getScreanWidth(): Int {
        val windowManager =
            BaseApplication.instance().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metric = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metric)
        return metric.widthPixels
    }

    fun getScreanHeight(): Int {
        val windowManager =
            BaseApplication.instance().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metric = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metric)
        return metric.heightPixels
    }

    fun hasNavigationBar(): Boolean {
        return getScreenHeight4() != getScreenHeight3()
    }

    fun toFloat(denominator: Long, numerator: Long): Double? {
        val df = DecimalFormat("0.00") //设置保留位数
        return java.lang.Double.valueOf(df.format((denominator.toFloat() / numerator).toDouble()))
    }

    /**
     * 不包含虚拟导航栏高度
     */
    fun getScreenHeight4(): Int { //2029
        return BaseApplication.instance().getResources().getDisplayMetrics().heightPixels
    }

    fun getScreenHeight3(): Int { //2248
        val windowManager = BaseApplication.instance()
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val defaultDisplay = windowManager.defaultDisplay
        val outPoint = Point()
        defaultDisplay.getRealSize(outPoint)
        return outPoint.y
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return null
    }

    /**
     * 判断应用是否安装
     *
     * @param pkgName
     * @return
     */
    fun checkAppInstalled(pkgName: String?): Boolean {
        if (pkgName == null || pkgName.isEmpty()) {
            return false
        }
        var packageInfo: PackageInfo?
        try {
            packageInfo = BaseApplication.instance().getPackageManager().getPackageInfo(pkgName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            packageInfo = null
            e.printStackTrace()
        }
        return packageInfo != null
    }

    //sp转px
    fun sp2px(spValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            spValue, BaseApplication.instance().resources.displayMetrics
        ).toInt()
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dp2px(dpValue: Float): Int {
        val scale = BaseApplication.instance().resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    //dp转px
    fun dip2px(dpValue: Float): Int {
        val scale: Float = BaseApplication.instance().resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 获取版本号名称
     *
     * @return
     */
    fun getVerName(): String? {
        var verName = ""
        try {
            verName = BaseApplication.instance().packageManager.getPackageInfo(
                BaseApplication.instance().packageName,
                0
            ).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return verName
    }

    fun getPackName(): String? {
        return BaseApplication.instance().packageName
    }

    /**
     * 获取版本号名称
     *
     * @return
     */
    fun getVerCode(): Long {
        var verCode: Long = 0
        try {
            verCode = BaseApplication.instance().packageManager.getPackageInfo(
                BaseApplication.instance().getPackageName(), 0
            ).versionCode.toLong()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return verCode
    }

    /**
     * 截图
     *
     * @param v
     * @return
     */
    fun getViewBp(v: View?): Bitmap? {
        if (null == v) {
            return null
        }
        v.isDrawingCacheEnabled = true
        v.buildDrawingCache()
        v.measure(
            View.MeasureSpec.makeMeasureSpec(v.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(
                v.height,
                View.MeasureSpec.EXACTLY
            )
        )
        v.layout(
            v.x.toInt(),
            v.y.toInt(),
            v.x.toInt() + v.measuredWidth,
            v.y.toInt() + v.measuredHeight
        )
        val b = Bitmap.createBitmap(v.drawingCache, 0, 0, v.measuredWidth, v.measuredHeight)
        v.isDrawingCacheEnabled = false
        v.destroyDrawingCache()
        return b
    }

    /**
     * web截图
     *
     * @return
     */
    fun getWebBp(webView: DWebView) {
        webView.measure(
            View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
            ),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        webView.layout(
            webView.getX() as Int,
            webView.getY() as Int,
            webView.getX() as Int + webView.measuredWidth,
            webView.getY() as Int + webView.measuredHeight
        )
        webView.isDrawingCacheEnabled = true
        webView.buildDrawingCache()
        val bm = Bitmap.createBitmap(
            webView.measuredWidth,
            webView.computeVerticalScrollRange(),
            Bitmap.Config.RGB_565
        )
        val bigcanvas = Canvas(bm)
        val paint = Paint()
        val iHeight = bm.height
        bigcanvas.drawBitmap(bm, 0f, iHeight.toFloat(), paint)
        webView.draw(bigcanvas)
    }

    fun getWebShot(webView: DWebView): Bitmap? {
        val bm = Bitmap.createBitmap(
            webView.measuredWidth,
            webView.computeVerticalScrollRange(),
            Bitmap.Config.RGB_565
        )
        val bigcanvas = Canvas(bm)
        val paint = Paint()
        val iHeight = bm.height
        bigcanvas.drawBitmap(bm, 0f, iHeight.toFloat(), paint)
        webView.draw(bigcanvas)
        return bm
    }

    /**
     * 跳转到手机浏览器
     */
    fun goBrowser(url: String?) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        ActivityCacheManager.instance().getLastActivity()?.startActivity(intent)
    }

    /**
     * 跳转到应用市场
     */
    fun goToMarket(packageName: String) {
        val uri = Uri.parse("market://details?id=$packageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            ActivityCacheManager.instance().getLastActivity()?.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * 根据百分比改变颜色透明度
     */
    fun changeAlpha(color: Int, fraction: Float): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val alpha = (Color.alpha(color) * fraction).toInt()
        return Color.argb(alpha, red, green, blue)
    }

    fun checkAppViewsionCode(versionCode: Int): Boolean {
        return getVerCode() < versionCode
    }

    /**
     * ***
     */
    fun replaceStar(value: String): String? {
        if (!TextUtils.isEmpty(value) && value.length > 6) {
            val sb = StringBuilder()
            for (i in value.indices) {
                val c = value[i]
                if (i in 3..6) {
                    sb.append('*')
                } else {
                    sb.append(c)
                }
            }
            return sb.toString()
        }
        return value
    }

    fun encodeHeadInfo(headInfo: String): String? {
        val stringBuffer = StringBuffer()
        var i = 0
        val length = headInfo.length
        while (i < length) {
            val c = headInfo[i]
            if (c <= '\u001f' || c >= '\u007f') {
                stringBuffer.append(String.format("\\u%04x", c.toInt()))
            } else {
                stringBuffer.append(c)
            }
            i++
        }
        return stringBuffer.toString()
    }

    fun telPhone(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+" + phone))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        BaseApplication.instance().startActivity(intent)
    }
}