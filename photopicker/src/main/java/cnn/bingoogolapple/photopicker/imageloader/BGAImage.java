package cnn.bingoogolapple.photopicker.imageloader;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.widget.ImageView;

public class BGAImage {
    private static final String TAG = BGAImage.class.getSimpleName();
    private static BGAImageLoader sImageLoader;

    private BGAImage() {
    }

    private static final BGAImageLoader getImageLoader() {
        if (sImageLoader == null) {
            synchronized (BGAImage.class) {
                if (sImageLoader == null) {
                    sImageLoader = new BGAGlideImageLoader();
                }
            }
        }
        return sImageLoader;
    }

    /**
     * 设置开发者自定义 ImageLoader
     */
    public static void setImageLoader(BGAImageLoader imageLoader) {
        sImageLoader = imageLoader;
    }

    private static final boolean isClassExists(String classFullName) {
        try {
            Class.forName(classFullName);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void display(ImageView imageView, @DrawableRes int loadingResId, @DrawableRes int failResId, String path, int width, int height, final BGAImageLoader.DisplayDelegate delegate) {
        try {
            getImageLoader().display(imageView, path, loadingResId, failResId, width, height, delegate);
        } catch (Exception e) {
            Log.d(TAG, "显示图片失败：" + e.getMessage());
        }
    }

    public static void display(ImageView imageView, @DrawableRes int placeholderResId, String path, int width, int height, final BGAImageLoader.DisplayDelegate delegate) {
        display(imageView, placeholderResId, placeholderResId, path, width, height, delegate);
    }

    public static void display(ImageView imageView, @DrawableRes int placeholderResId, String path, int width, int height) {
        display(imageView, placeholderResId, path, width, height, null);
    }

    public static void display(ImageView imageView, @DrawableRes int placeholderResId, String path, int size) {
        display(imageView, placeholderResId, path, size, size);
    }

    public static void download(String path, final BGAImageLoader.DownloadDelegate delegate) {
        try {
            getImageLoader().download(path, delegate);
        } catch (Exception e) {
            Log.d(TAG, "下载图片失败：" + e.getMessage());
        }
    }

    public static void pause(Context activity) {
        getImageLoader().pause(activity);
    }

    public static void resume(Context activity) {
        getImageLoader().resume(activity);
    }
}