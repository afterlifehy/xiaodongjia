package com.wbb.base.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;

import com.wbb.base.help.ActivityCacheManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 相机相册
 *
 * @author hy
 */
public class CameraUtil {

    public static int CameraRequestCode = 111;  //拍照
    public static int AlbumRequestCode = 222; //选择图片
    public static Uri saveUri;
    public static String imageFile;
    private ArrayMap<String, Object> image;

    public static void selectPhoto() {
        Activity activity = ActivityCacheManager.Companion.instance().getLastActivity();
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "选择图片"), AlbumRequestCode);
    }

    public static void selectPhoto(int requestCode) {
        Activity activity = ActivityCacheManager.Companion.instance().getLastActivity();
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "选择图片"), requestCode);
    }

    public static void selectPhotoFragment(Fragment fragment) {
        Activity activity = ActivityCacheManager.Companion.instance().getLastActivity();
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        fragment.startActivityForResult(Intent.createChooser(intent, "选择图片"), AlbumRequestCode);
    }

    public static File choosePhoto(Uri source) {
        File file = null;
        if (source == null) {
            return null;
        }
        String path = UriUtils.INSTANCE.getPathFromUri(source);
        if(!TextUtils.isEmpty(path)){
            file = new File(path);
        }
        return file;
    }

    public static void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT > 24) {

        }
        File file = FileAccessor.getUploadingTackPicFilePath();
        if (file != null) {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Uri uri = UriUtils.INSTANCE.getUri(file, intent);
            saveUri = uri;
            if (uri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            } else {
                return;
            }
            imageFile = file.getAbsolutePath();
        }
        Activity activity = ActivityCacheManager.Companion.instance().getLastActivity();
        if (activity != null) {
            activity.startActivityForResult(intent, CameraRequestCode);
        }
    }

    public static void takePhoto(int requestCode) {
        Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT > 24) {

        }
        File file = FileAccessor.getUploadingTackPicFilePath();
        if (file != null) {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Uri uri = UriUtils.INSTANCE.getUri(file, intent);
            saveUri = uri;
            if (uri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            } else {
                return;
            }
            imageFile = file.getAbsolutePath();
        }
        Activity activity = ActivityCacheManager.Companion.instance().getLastActivity();
        if (activity != null) {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public static void takePhotoFragment(Fragment fragment) {
        Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        File file = FileAccessor.getUploadingTackPicFilePath();
        if (file != null) {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Uri uri = UriUtils.INSTANCE.getUri(file, intent);
            saveUri = uri;
            if (uri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
            imageFile = file.getAbsolutePath();
        }
        fragment.startActivityForResult(intent, CameraRequestCode);
    }

//    public static File getSmallBitmap(String filePath) {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filePath, options);
//        // Calculate inSampleSize
//        options.inSampleSize = BitmapHelper.calculateInSampleSize(options, 480, 800);
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
//        if (bm == null) {
//            return null;
//        }
//        int degree = AppUtil.readPictureDegree(filePath);
//        bm = rotateBitmap(bm, degree);
//        return saveBitmap(bm, filePath, 70);
//    }

    private static Bitmap compressImage(Bitmap image, String srcPath) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 添加图片到sd卡并规定压缩比例，100默认原图
     */
    public static File saveBitmap(Bitmap bitmap, String savePath, int quality) {
        if (bitmap == null) {
            return null;
        }
        try {
            File f = new File(savePath);
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream fos = new FileOutputStream(f);
            f.createNewFile();
            // 把Bitmap对象解析成流
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            Log.d("bitmap", bitmap.getRowBytes() + "");
            fos.flush();
            fos.close();
            bitmap.recycle();
            return f;
        } catch (IOException e) {
            e.printStackTrace();
            bitmap.recycle();
            return null;
        }
    }

    /**
     * 旋转图片
     *
     * @param bitmap
     * @param rotate
     * @return
     */
    private static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }
}
