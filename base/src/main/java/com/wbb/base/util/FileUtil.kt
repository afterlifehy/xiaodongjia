package com.wbb.base.util

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.format.DateFormat
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.uuzuche.lib_zxing.camera.BitmapLuminanceSource
import com.uuzuche.lib_zxing.decoding.DecodeFormatManager
import com.wbb.base.BaseApplication
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Created by hy on 2021/1/20.
 */
object FileUtil {

    val TAG: String = FileUtil::class.java.name
    var EXTERNAL_STOREPATH: String = FileUtil.getExternalStorePath().toString()
    const val GM_PATH = "/Xiaodongjia"
    val IMAGE_DIR = "$EXTERNAL_STOREPATH$GM_PATH/image"
    val FILE_DIR = "$EXTERNAL_STOREPATH$GM_PATH/file"
    val DOWNLOAD_DIR = "$EXTERNAL_STOREPATH$GM_PATH/download"
    val DCIM = "$EXTERNAL_STOREPATH/DCIM/Camera"
    private val bitmap: Bitmap? = null

    /**
     * 外置存储卡的路径
     */
    fun getExternalStorePath(): String? {
        return if (FileUtil.isExistExternalStore()) {
            Environment.getExternalStorageDirectory().absolutePath
        } else null
    }


    /**
     * 是否有外存卡
     */
    fun isExistExternalStore(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * 返回下载存放目录
     */
    fun getDownLoadPathName(): File? {
        if (!FileUtil.isExistExternalStore()) {
            ToastUtils.showToash("储存卡已拔出，语音功能将暂时不可用")
            return null
        }
        val directory = File(FileUtil.DOWNLOAD_DIR)
        if (!directory.exists() && !directory.mkdirs()) {
            ToastUtils.showToash("Path to file could not be created")
            return null
        }
        return directory
    }

    fun getUploadingTackPicFilePath(): File? {
        val dirFile = File(FileUtil.IMAGE_DIR)
        if (!dirFile.exists()) {
            dirFile.mkdir()
        }
        return File(FileUtil.IMAGE_DIR, DateFormat.format("yyyyMMddhhmmss", Calendar.getInstance(Locale.CHINA)).toString() + ".jpg")
    }

    fun compressImage(image: Bitmap?, size: Int): Bitmap? {
        val baos = ByteArrayOutputStream()
        if (image == null) {
            return null
        }
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos) // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 100
        while (baos.toByteArray().size / 1024 > size * 1024) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            options -= 10 // 每次都减少10
            baos.reset() // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos) // 这里压缩options%，把压缩后的数据存放到baos中
        }
        val isBm = ByteArrayInputStream(baos.toByteArray()) // 把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null)
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            inSampleSize = if (width > height) {
                Math.round(height.toFloat() / reqHeight.toFloat())
            } else {
                Math.round(width.toFloat() / reqWidth.toFloat())
            }
        }
        return inSampleSize
    }

    fun saveBitmap(bmp: Bitmap, path: String?): File? {
        val appDir = File(path)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // 通知相册有新图片
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val uri = Uri.fromFile(file)
        intent.data = uri
        BaseApplication.instance().sendBroadcast(intent)
        return file
    }

    /**
     * 获取文件名
     *
     * @param pathName
     * @return
     */
    fun getFileName(pathName: String): String? {
        val start = pathName.lastIndexOf("/")
        return if (start != -1) {
            pathName.substring(start + 1, pathName.length)
        } else pathName
    }

    /**
     * 解析二维码图片工具类
     *
     * @param analyzeCallback
     */
    fun analyzeBitmap(mBitmap: Bitmap?, analyzeCallback: CodeUtils.AnalyzeCallback?) {
        /**
         * 首先判断图片的大小,若图片过大,则执行图片的裁剪操作,防止OOM
         */
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true; // 先获取原大小
//        Bitmap mBitmap = BitmapFactory.decodeFile(path, options);
//        options.inJustDecodeBounds = false; // 获取新的大小
//
//        int sampleSize = (int) (options.outHeight / (float) 400);
//
//        if (sampleSize <= 0)
//            sampleSize = 1;
//        options.inSampleSize = sampleSize;
//        mBitmap = BitmapFactory.decodeFile(path, options);
        val multiFormatReader = MultiFormatReader()

        // 解码的参数
        val hints: Hashtable<DecodeHintType, Any> = Hashtable<DecodeHintType, Any>(2)
        // 可以解析的编码类型
        var decodeFormats: Vector<BarcodeFormat?> = Vector<BarcodeFormat?>()
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = Vector<BarcodeFormat?>()

            // 这里设置可扫描的类型，我这里选择了都支持
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS)
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
        }
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        // 设置继续的字符编码格式为UTF8
        // hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        // 设置解析配置参数
        multiFormatReader.setHints(hints)

        // 开始对图像资源解码
        var rawResult: Result? = null
        try {
            rawResult = multiFormatReader.decodeWithState(BinaryBitmap(HybridBinarizer(BitmapLuminanceSource(mBitmap))))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (rawResult != null) {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeSuccess(mBitmap, rawResult.getText())
            }
        } else {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeFailed()
            }
        }
    }

}