package com.qiushi.wechatshop.util

import android.content.Context
import android.net.Uri
import android.os.Looper
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.qiushi.wechatshop.GlideApp
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.WAppContext
import com.qiushi.wechatshop.model.MyGlideUrl
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * Created by Rylynn on 2018-03-29.
 * Glide图片加载工具类
 */
object ImageHelper {

    private const val DEFAULT_AVATAR = 0//默认头像
    private const val PLACEHOLDER = R.color.imageBackground//默认图片占位颜色

    fun loadImage(ctx: Context, view: ImageView, url: String) {
        if (url.isNotEmpty()) {
            GlideApp.with(ctx).load(MyGlideUrl(url))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view)
        } else {
            GlideApp.with(ctx).load(PLACEHOLDER)
                    .dontAnimate().into(view)
        }
    }


    /**
     *     public static void loadAvatar(Context context, ImageView view, String url) {
    if (!TextUtils.isEmpty(url)) {
    GlideApp.with(context).load(new TopGlideUrl(url))
    .format(DecodeFormat.PREFER_ARGB_8888)
    .placeholder(DEFAULT_AVATAR)
    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
    .override(64, 64)
    .error(DEFAULT_PLACEHOLDER)
    .transform(new CircleCrop())
    .into(view);
    } else {
    loadDefaultAvatar(context, view);
    }
    }
     */

    fun loadAvatar(ctx: Context, view: ImageView, url: String, width: Int, height: Int) {
        if (url.isNotEmpty()) {
            GlideApp.with(ctx).load(MyGlideUrl(url))
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .placeholder(DEFAULT_AVATAR)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(width, height)
                    .transform(CircleCrop())
                    .into(view)
        } else {
            GlideApp.with(ctx).load(PLACEHOLDER)
                    .dontAnimate().into(view)
        }
    }


    fun loadImage(ctx: Context, view: ImageView, url: String, width: Int,
                  height: Int) {
        if (url.isNotEmpty()) {
            loadImage(ctx, view, url, width, height, PLACEHOLDER)
        } else {
            GlideApp.with(ctx).load(PLACEHOLDER)
                    .dontAnimate().into(view)
        }
    }

    fun loadImage(ctx: Context, view: ImageView, url: String, width: Int,
                  height: Int, placeHolder: Int) {
        GlideApp.with(ctx).load(MyGlideUrl(url))
                .override(width, height)
                .placeholder(placeHolder)
                .error(placeHolder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
    }

    fun loadImage(ctx: Context, view: ImageView, url: String, width: Int,
                  height: Int, radius: Float) {
        if (url.isNotEmpty()) {
            GlideApp.with(ctx).load(MyGlideUrl(url))
                    .override(width, height)

                    .error(PLACEHOLDER)
                    .transforms(CenterCrop(),
                            RoundedCornersTransformation(DensityUtils.dp2px(radius), 0, RoundedCornersTransformation.CornerType.LEFT))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view)
        } else {
            GlideApp.with(ctx).load(PLACEHOLDER)
                    .dontAnimate().into(view)
        }
    }

    /**
     * 加载用户主页背景图
     */
    fun loadUserBackground(ctx: Context, view: ImageView, uri: Uri) {
        GlideApp.with(ctx).load(uri)
                .format(DecodeFormat.PREFER_RGB_565)
                .sizeMultiplier(0.9f)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .dontAnimate()
                .priority(Priority.HIGH)
                .transforms(CenterCrop(),
                        BlurTransformation(6)
                )
                .into(view)
    }

    /**
     * 清除图片内存缓存
     */
    fun clearImageMemoryCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Glide.get(WAppContext.context).clearMemory()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 清除图片磁盘缓存
     */
    fun clearImageDiskCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Thread(Runnable { Glide.get(WAppContext.context).clearDiskCache() }).start()
            } else {
                Glide.get(WAppContext.context).clearDiskCache()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun clear() {
        clearImageMemoryCache()
        clearImageDiskCache()
    }
}