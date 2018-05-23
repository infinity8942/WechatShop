package com.qiushi.wechatshop.util;

import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.qiushi.wechatshop.WAppContext;

/**
 * Created by Rylynn on 2018-03-29.
 * Glide图片加载工具类
 */
public class ImageHelper {

    private final static int DEFAULT_AVATAR = 0;//默认头像
    private final static int DEFAULT_PLACEHOLDER = 0;//默认图片占位颜色

    public static void loadAvatar(Context context, ImageView view, String url, int width, int height) {
//        if (!TextUtils.isEmpty(url)) {
//            GlideApp.with(context).load(new MyGlideUrl(url))
//                    .format(DecodeFormat.PREFER_ARGB_8888)
//                    .placeholder(DEFAULT_AVATAR)
//                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                    .override(width, height)
//                    .error(DEFAULT_AVATAR)
//                    .transform(new CircleCrop())
//                    .into(view);
//        } else {
//            GlideApp.with(context).load(DEFAULT_AVATAR)
//                    .transform(new CircleCrop()).into(view);
//        }
    }

    //Normal Image
    public static void loadImage(Context context, ImageView view, String url) {
//        if (!TextUtils.isEmpty(url)) {
//            GlideApp.with(context).load(new MyGlideUrl(url))
//                    .format(DecodeFormat.PREFER_RGB_565)
//                    .sizeMultiplier(0.9f)
//                    .placeholder(DEFAULT_PLACEHOLDER)
//                    .error(DEFAULT_PLACEHOLDER)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .priority(Priority.HIGH)
//                    .centerCrop()
//                    .into(view);
//        } else {
//            GlideApp.with(context).load(DEFAULT_PLACEHOLDER)
//                    .format(DecodeFormat.PREFER_RGB_565)
//                    .dontAnimate()
//                    .into(view);
//        }
    }

    public static void loadImage(Context context, ImageView view, String url, int width, int height) {
        loadImage(context, view, url, width, height, DEFAULT_PLACEHOLDER);
    }

    public static void loadImage(Context context, ImageView view, String url, int width, int height, int placeHolder) {
//        if (!TextUtils.isEmpty(url)) {
//            GlideApp.with(context).load(new MyGlideUrl(url))
//                    .format(DecodeFormat.PREFER_RGB_565)
//                    .override(width, height)
//                    .sizeMultiplier(0.9f)
//                    .placeholder(placeHolder)
//                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                    .error(placeHolder)
//                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .priority(Priority.HIGH)
//                    .centerCrop()
//                    .into(view);
//        } else {
//            GlideApp.with(context).load(placeHolder)
//                    .format(DecodeFormat.PREFER_RGB_565)
//                    .dontAnimate()
//                    .into(view);
//        }
    }

    public static void loadResource(Context context, ImageView view, int res, float radius) {
//        GlideApp.with(context).load(res)
//                .format(DecodeFormat.PREFER_RGB_565)
//                .dontAnimate()
//                .transforms(new CenterCrop(),
//                        new RoundedCornersTransformation(DensityUtils.dp2px(radius), 0))
//                .into(view);
    }

    /**
     * 加载用户主页背景图
     */
    public static void loadUserBackground(Context context, ImageView view, Uri uri) {
//        GlideApp.with(context).load(uri)
//                .format(DecodeFormat.PREFER_RGB_565)
//                .sizeMultiplier(0.9f)
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                .dontAnimate()
//                .priority(Priority.HIGH)
//                .transforms(new CenterCrop(),
//                        new BlurTransformation(6)
//                )
//                .into(view);
    }

    /**
     * 清除图片内存缓存
     */
    public static void clearImageMemoryCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Glide.get(WAppContext.INSTANCE.getContext()).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除图片磁盘缓存
     */
    public static void clearImageDiskCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(WAppContext.INSTANCE.getContext()).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(WAppContext.INSTANCE.getContext()).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}