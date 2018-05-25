package com.qiushi.wechatshop.util;

import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.qiushi.wechatshop.GlideApp;
import com.qiushi.wechatshop.R;
import com.qiushi.wechatshop.WAppContext;
import com.qiushi.wechatshop.model.MyGlideUrl;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by Rylynn on 2018-03-29.
 * Glide图片加载工具类
 */
public class ImageHelper {

    private final static int DEFAULT_AVATAR = 0;//默认头像
    private final static int PLACEHOLDER = R.color.imageBackground;//默认图片占位颜色

    public static void loadImage(Context ctx, ImageView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            GlideApp.with(ctx).load(new MyGlideUrl(url))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view);
        } else {
            GlideApp.with(ctx).load(PLACEHOLDER)
                    .dontAnimate().into(view);
        }
    }

    public static void loadImage(Context ctx, ImageView view, String url, int width,
                                 int height) {
        if (!TextUtils.isEmpty(url)) {
            loadImage(ctx, view, url, width, height, PLACEHOLDER);
        } else {
            GlideApp.with(ctx).load(PLACEHOLDER)
                    .dontAnimate().into(view);
        }
    }

    public static void loadImage(Context ctx, ImageView view, String url, int width,
                                 int height, int placeHolder) {
        GlideApp.with(ctx).load(new MyGlideUrl(url))
                .override(width, height)
                .placeholder(placeHolder)
                .error(placeHolder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view);
    }

    public static void loadImage(Context ctx, ImageView view, String url, int width,
                                 int height, float radius) {
        if (!TextUtils.isEmpty(url)) {
            GlideApp.with(ctx).load(new MyGlideUrl(url))
                    .override(width, height)
                    .transforms(new CenterCrop(),
                            new RoundedCornersTransformation(DensityUtils.dp2px(radius), 0))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view);
        } else {
            GlideApp.with(ctx).load(PLACEHOLDER)
                    .dontAnimate().into(view);
        }
    }

    /**
     * 加载用户主页背景图
     */
    public static void loadUserBackground(Context ctx, ImageView view, Uri uri) {
        GlideApp.with(ctx).load(uri)
                .format(DecodeFormat.PREFER_RGB_565)
                .sizeMultiplier(0.9f)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .dontAnimate()
                .priority(Priority.HIGH)
                .transforms(new CenterCrop(),
                        new BlurTransformation(6)
                )
                .into(view);
    }

    /**
     * 清除图片内存缓存
     */
    public static void clearImageMemoryCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Glide.get(WAppContext.context).clearMemory();
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
                        Glide.get(WAppContext.context).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(WAppContext.context).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clear() {
        clearImageMemoryCache();
        clearImageDiskCache();
    }
}