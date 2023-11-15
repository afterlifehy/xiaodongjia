package com.wbb.base.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.camera.BitmapLuminanceSource;
import com.uuzuche.lib_zxing.decoding.DecodeFormatManager;
import com.wbb.base.BaseApplication;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;


public class FileAccessor {

    public static final String TAG = FileAccessor.class.getName();
    public static String EXTERNAL_STOREPATH = getExternalStorePath();
    public static final String XDJ_PATH = "/XiaoDongJia";
    public static final String IMAGE_DIR = EXTERNAL_STOREPATH + XDJ_PATH + "/image";
    public static final String FILE_DIR = EXTERNAL_STOREPATH + XDJ_PATH + "/file";
    public static final String DOWNLOAD_DIR = EXTERNAL_STOREPATH + XDJ_PATH + "/download";
    public static final String DCIM = EXTERNAL_STOREPATH + "/DCIM/Camera";
    private static Bitmap bitmap;

    /**
     * 返回图片存放目录
     */
    public static File getImagePathName() {
        if (!isExistExternalStore()) {
            ToastUtils.INSTANCE.showToash("储存卡已拔出，语音功能将暂时不可用");
            return null;
        }

        File directory = new File(IMAGE_DIR);
        if (!directory.exists() && !directory.mkdirs()) {
            ToastUtils.INSTANCE.showToash("Path to file could not be created");
            return null;
        }

        return directory;
    }

    /**
     * 返回图片存放目录
     */
    public static File getFilePathName() {
        if (!isExistExternalStore()) {
            ToastUtils.INSTANCE.showToash("储存卡已拔出，语音功能将暂时不可用");
            return null;
        }

        File directory = new File(FILE_DIR);
        if (!directory.exists() && !directory.mkdirs()) {
            ToastUtils.INSTANCE.showToash("Path to file could not be created");
            return null;
        }
        return directory;
    }

    /**
     * 返回下载存放目录
     */
    public static File getDownLoadPathName() {
        if (!isExistExternalStore()) {
            ToastUtils.INSTANCE.showToash("储存卡已拔出，语音功能将暂时不可用");
            return null;
        }

        File directory = new File(DOWNLOAD_DIR);
        if (!directory.exists() && !directory.mkdirs()) {
            ToastUtils.INSTANCE.showToash("Path to file could not be created");
            return null;
        }
        return directory;
    }

    /**
     * 外置存储卡的路径
     */
    public static String getExternalStorePath() {
        if (isExistExternalStore()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 是否有外存卡
     */
    public static boolean isExistExternalStore() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取指定文件大小
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
        }
        return size;
    }

    public static File downloadFile(String path, String fileName) {
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File localFile = new File(path, fileName);
        return localFile;
    }

    public static File getUploadingTackPicFilePath() {
        File dirFile = new File(IMAGE_DIR);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File localFile = new File(IMAGE_DIR, DateFormat.format("yyyyMMddhhmmss",
                Calendar.getInstance(Locale.CHINA)) + ".jpg");
        return localFile;
    }


    //网络图片转bitmap
    public static Bitmap returnBitMap(final String url) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imageurl = null;
                try {
                    imageurl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection) imageurl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return bitmap;
    }

    public static Bitmap compressImage(Bitmap image) {
        Bitmap bitmap = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (image != null) {
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 1000) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                options -= 10;// 每次都减少10
                baos.reset();// 重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
            bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        }
        return bitmap;
    }

    public static Bitmap compressImage(Bitmap image, int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (image == null) {
            return null;
        }
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > size * 1024) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            options -= 10;// 每次都减少10
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public static File saveFile(Bitmap bm, String fileName) {
        File dirFile = new File(DCIM);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(DCIM, fileName);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        try {
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if ((!myCaptureFile.getParentFile().exists())
                && (!myCaptureFile.getParentFile().mkdirs())) {
            myCaptureFile = null;
        }
        return myCaptureFile;
    }

    public static File saveBitmap(Bitmap bmp, String path) {
        File appDir = new File(path);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 通知相册有新图片
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        BaseApplication.instance().sendBroadcast(intent);
        return file;
    }

    public static void saveImageToGallery(Bitmap bmp) {
        File appDir = new File(DCIM);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BaseApplication.instance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

    public static File getSaveFile(Context context, String fileName) {
        return new File(context.getFilesDir(), fileName);
    }

    /**
     * 获取文件名
     *
     * @param pathName
     * @return
     */
    public static String getFileName(String pathName) {
        int start = pathName.lastIndexOf("/");
        if (start != -1) {
            return pathName.substring(start + 1, pathName.length());
        }
        return pathName;
    }

    /**
     * 解析二维码图片工具类
     *
     * @param analyzeCallback
     */
    public static void analyzeBitmap(Bitmap mBitmap, CodeUtils.AnalyzeCallback analyzeCallback) {

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

        MultiFormatReader multiFormatReader = new MultiFormatReader();

        // 解码的参数
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(2);
        // 可以解析的编码类型
        Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector<BarcodeFormat>();

            // 这里设置可扫描的类型，我这里选择了都支持
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        // 设置继续的字符编码格式为UTF8
        // hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        // 设置解析配置参数
        multiFormatReader.setHints(hints);

        // 开始对图像资源解码
        Result rawResult = null;
        try {
            rawResult = multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(new BitmapLuminanceSource(mBitmap))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rawResult != null) {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeSuccess(mBitmap, rawResult.getText());
            }
        } else {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeFailed();
            }
        }
    }

    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return base64;
    }

    public static String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.NO_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }
}
