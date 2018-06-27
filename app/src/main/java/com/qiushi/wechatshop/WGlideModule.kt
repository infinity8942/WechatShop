package com.qiushi.wechatshop

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.MemoryCategory
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.DiskCache
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.qiushi.wechatshop.util.FileUtil
import okhttp3.OkHttpClient
import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * Created by Rylynn on 2018-02-27.
 * Glide配置
 */
@GlideModule
class WGlideModule : AppGlideModule() {

    private val mRequestOption: RequestOptions = RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565)
            .error(R.color.imageBackground)
            .centerCrop()
            .disallowHardwareConfig()

    private val client = OkHttpClient.Builder()
            .connectTimeout(10000, TimeUnit.SECONDS)
            .readTimeout(10000, TimeUnit.SECONDS)
            .writeTimeout(10000, TimeUnit.SECONDS)
            .build()

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        glide.setMemoryCategory(MemoryCategory.HIGH)
        val factory = OkHttpUrlLoader.Factory(client)
        registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDiskCache {
            val imgFile = File(FileUtil.getRootFile(), "cache")
            DiskLruCacheWrapper.get(imgFile, DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE.toLong())
        }
        builder.setDefaultRequestOptions(mRequestOption)
    }

    override fun isManifestParsingEnabled(): Boolean = false
}