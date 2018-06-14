package com.qiushi.wechatshop.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.previewlibrary.loader.IZoomMediaLoader;
import com.previewlibrary.loader.MySimpleTarget;

/**
 * 素材九宫格图片加载类
 */
public class NineImageLoader implements IZoomMediaLoader {
    @Override
    public void displayImage(@NonNull Fragment context, @NonNull String path, final @NonNull MySimpleTarget<Bitmap> simpleTarget) {
        Glide.with(context).asBitmap().load(path).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onLoadStarted(Drawable placeholder) {
                simpleTarget.onLoadStarted();
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                simpleTarget.onLoadFailed(errorDrawable);
            }

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                simpleTarget.onResourceReady(resource);
            }
        });
    }

    @Override
    public void onStop(@NonNull Fragment context) {
        Glide.with(context).onStop();
    }

    @Override
    public void clearMemory(@NonNull Context c) {
        Glide.get(c).clearMemory();
    }
}