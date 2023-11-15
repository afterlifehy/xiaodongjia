package com.wbb.base.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.collection.LongSparseArray;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.Map;

public class BitmapHelper {
    public static final String TAG = "Mem#BitmapHelper";
    private static final boolean CACHE_RESOURCES = true;
    private static int sReqWidth;
    private static int sReqHeight;
    private static LongSparseArray<ResourceCache> sResourceCaches;

    public static ResourceCache getResourceCache(Object target) {
        if (target == null) {
            return ResourceCache.getSingleInstance();
        } else {
            if (sResourceCaches == null) sResourceCaches = new LongSparseArray<>();
            ResourceCache cache;
            int key = target.hashCode();
            if ((cache = sResourceCaches.get(key)) == null) {
                cache = ResourceCache.newInstance();
                sResourceCaches.put(key, cache);
            }
            return cache;
        }
    }

    public static void recycleAllResourceCache() {
        if (sResourceCaches != null) {
            for (int i = 0; i < sResourceCaches.size(); i++) {
                ResourceCache cache = sResourceCaches.valueAt(i);
                if (cache != null) {
                    cache.recycleAll();
                }
            }
            sResourceCaches.clear();
        }
    }

    public static void loadBackground(Activity activity, int viewId, int resId) {
        if (activity == null || viewId < 0 || resId < 0) return;
        View view = activity.findViewById(viewId);
        loadBackground(activity, view, resId);
    }

    public static void loadBackground(Context context, View view, int resId) {
        if (context == null || view == null || resId < 0) return;
        loadBackground(view, context.getResources(), resId, CACHE_RESOURCES ? getResourceCache(context) : null, false);
    }

    public static void loadBackground(Fragment fragment, View view, int resId) {
        if (fragment == null || view == null || resId < 0) return;
        loadBackground(view, fragment.getResources(), resId, CACHE_RESOURCES ? getResourceCache(fragment) : null, false);
    }

    private static void loadBackground(View view, Resources res, int resId, ResourceCache cache, boolean exactSize) {
        if (view == null || res == null || resId < 0) return;
        final Resources r = res;
        loadDrawable(view, res, resId, cache, exactSize, new Callback<View, Drawable>() {
            @Override
            public void callback(View view, Drawable drawable) {
                if (view != null && drawable != null) {
                    setBackground(view, drawable);
                }
            }
        });
    }

    public static void loadImage(Fragment fragment, int viewId, int resId) {
        if (fragment == null || viewId < 0 || resId < 0) return;
        Activity activity = fragment.getActivity();
        View view;
        if (activity != null && (view = activity.findViewById(viewId)) instanceof ImageView) {
            loadImage(fragment, (ImageView) view, resId);
        }
    }

    public static void loadImage(Activity activity, int viewId, int resId) {
        if (activity == null || viewId < 0 || resId < 0) return;
        View view = activity.findViewById(viewId);
        if (view instanceof ImageView)
            loadImage(activity, (ImageView) view, resId);
    }

    public static void loadImage(Context context, ImageView view, int resId) {
        if (context == null || view == null || resId < 0) return;
        loadImage(view, context.getResources(), resId, CACHE_RESOURCES ? getResourceCache(context) : null, false);
    }

    public static void loadImage(Fragment fragment, ImageView view, int resId) {
        if (fragment == null || view == null || resId < 0) return;
        loadImage(view, fragment.getResources(), resId, CACHE_RESOURCES ? getResourceCache(fragment) : null, false);
    }

    /**
     * @param exactSize see {@link #decodeSampledBitmapFromResource(Resources, int, int, int, boolean, ResourceCache)}
     */
    private static void loadImage(ImageView view, Resources res, int resId, ResourceCache cache, boolean exactSize) {
        if (view == null || res == null || resId < 0) return;
        final Resources r = res;
        loadDrawable(view, res, resId, cache, exactSize, new Callback<View, Drawable>() {
            @Override
            public void callback(View view, Drawable drawable) {
                if (view != null && drawable != null) {
                    ((ImageView) view).setImageDrawable(drawable);
                }
            }
        });
    }

    /**
     * @param exactSize see {@link #decodeSampledBitmapFromResource(Resources, int, int, int, boolean, ResourceCache)}
     */
    private static void loadDrawable(View view, Resources res, int resId, ResourceCache cache, boolean exactSize, Callback<View, Drawable> callback) {
        if (view == null || res == null || resId < 0) return;
        Drawable drawable = null;
        if (isResourcePicture(res, resId)) {
            int reqWidth;
            int reqHeight;

            if (view.getWidth() != 0 && view.getHeight() != 0) {
                reqWidth = view.getWidth();
                reqHeight = view.getHeight();
            } else {
                initScreenSize();
                reqWidth = sReqWidth;
                reqHeight = sReqHeight;
            }

            Bitmap bitmap = decodeSampledBitmapFromResource(res, resId, reqWidth, reqHeight, exactSize, cache);
            drawable = new BitmapDrawable(res, bitmap);
        } else {
            try {
                drawable = res.getDrawable(resId);
            } catch (Exception ignored) {
            }
        }
        if (callback != null) callback.callback(view, drawable);
    }

    public static void setBackground(ImageView view, int drawable) {
        if (view == null) return;
        view.setImageResource(drawable);
    }

    public static void setBackground(View view, Drawable drawable) {
        if (view == null) return;
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }


    private static void initScreenSize() {
        sReqWidth = AppUtil.INSTANCE.getScreanWidth();
        sReqHeight = AppUtil.INSTANCE.getScreanHeight();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        //default inSampleSize
        int inSampleSize = 1;
        if (options == null) return inSampleSize;
        //size of original picture
        final int height = options.outHeight;
        final int width = options.outWidth;

        if (height > reqHeight || width > reqWidth) {
            // calculate the ratio for scaling
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight, boolean exactSize, ResourceCache cache) {
        Bitmap bitmap = null;
        if (cache != null && (bitmap = cache.get(resId)) != null) {
            if (!bitmap.isRecycled()) {
                return bitmap;
            } else {
                cache.remove(resId);
                //TODO m:<span style="font-family:FangSong_GB2312;">lorss</span> how to deal with the views which has set background with this Bitmap
            }
        }
        if (!isResourcePicture(res, resId)) {
        } else if (exactSize) {
            final BitmapFactory.Options op = new BitmapFactory.Options();
            op.outWidth = reqWidth;
            op.outHeight = reqHeight;
            op.inPreferredConfig = Bitmap.Config.RGB_565;
            try {
                bitmap = BitmapFactory.decodeResource(res, resId, op);
            } catch (Exception ignored) {
            }
        } else {
            try {
                if (res == null || resId < 0 || reqWidth == 0 || reqHeight == 0) return null;
                // get the size of original picture
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(res, resId, options);

                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeResource(res, resId, options);
            } catch (Exception ignored) {
            }
        }
        if (bitmap != null && cache != null) {
            cache.addBitmapToMemoryCache(resId, bitmap);
        }
        return bitmap;
    }

    /**
     * whether the Resource represented by resId is picture(e.g. jpg or png).
     */
    public static boolean isResourcePicture(Resources res, int resId) {
        if (res == null || resId < 0) return false;
        TypedValue value = new TypedValue();
        res.getValue(resId, value, true);
        if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            //isColorDrawable
            return false;
        }

        if (!TextUtils.isEmpty(value.string)) {
            final String file = value.string.toString();
            if (file.endsWith(".xml")) {
                //isDrawable but xml
                return false;
            }
        }
        return true;
    }

    public static int bytesOf(Bitmap bitmap) {
        if (bitmap == null) return 0;
        if (Build.VERSION.SDK_INT >= 19) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= 12) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes();
        }
    }

    public interface Callback<T, R> {
        void callback(T t, R r);
    }


    public static class ResourceCache extends LruCache {

        private static final String TAG = "Mem#ResourceCache";
        private static ResourceCache sResourceCache;
        private LongSparseArray<WeakReference<Bitmap>> mLeakBitmaps;

        /**
         * @param maxSize for caches that do not override {@link #sizeOf}, this is
         *                the maximum number of entries in the cache. For all other caches,
         *                this is the maximum sum of the sizes of the entries in this cache.
         */
        public ResourceCache(int maxSize) {
            super(maxSize);
        }

        /**
         * get the Single Instance.
         */
        public static ResourceCache getSingleInstance() {
            if (sResourceCache == null) {
                synchronized (BitmapHelper.class) {
                    if (sResourceCache == null) {
                        sResourceCache = ResourceCache.newInstance();
                    }
                }
            }
            return sResourceCache;
        }

        public static ResourceCache newInstance() {
            long maxMemory/*KB*/ = (Runtime.getRuntime().maxMemory()/*byte*/ / 1024);
            long cacheSize = maxMemory / 8;
            if (cacheSize > Integer.MAX_VALUE) {
            }

            return new ResourceCache((int) cacheSize);
        }

        @NonNull
        @Override
        protected Map<Integer, Bitmap> onCreateMap() {
            return new ArrayMap<Integer, Bitmap>();
        }

        @Override
        protected int sizeOf(Integer key, Bitmap bitmap) {
            return bytesOf(bitmap) / 1024;
        }

        @Override
        protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
            if (evicted) {/*caused by evictAll (methods which called trimToSize)  */
                if (oldValue != null) {
                    //m:<span style="font-family:FangSong_GB2312;">lorss</span> just record the leaked Bitmap which has not been recycled
                    if (mLeakBitmaps == null) mLeakBitmaps = new LongSparseArray<>();
                    mLeakBitmaps.put(key, new WeakReference<Bitmap>(oldValue));
                }
            } else /*caused by remove or put*/ {
                if (oldValue != null && !oldValue.isRecycled()) {
                    try {
                        oldValue.recycle();
                        System.gc();
                    } catch (Exception ignored) {
                    }
                }
                logStatus();
            }
        }

        public void addBitmapToMemoryCache(Integer key, Bitmap bitmap) {
            if (get(key) == null) {
                put(key, bitmap);
            }
            logStatus();
        }

        /**
         * recycle all the Cached Resource, including the leaking Bitmap that has not been recycled which maybe is still being used.
         * <br/>Please ensure that all the Bitmap cached not used again after recycled, otherwise it will throw Exception.
         * <br/>See : {@link Bitmap#recycle()}
         */
        public void recycleAll() {
            removeAll();
            if (mLeakBitmaps != null) {
                for (int i = 0; i < mLeakBitmaps.size(); i++) {
                    WeakReference<Bitmap> ref = mLeakBitmaps.valueAt(i);
                    if (ref != null && ref.get() != null) {
                        ref.get().recycle();
                    }
                }
            }
        }

        private void logStatus() {
        }

    }
}
