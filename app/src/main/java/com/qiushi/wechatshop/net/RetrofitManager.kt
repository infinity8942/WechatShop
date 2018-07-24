package com.qiushi.wechatshop.net

import com.orhanobut.logger.Logger
import com.qiushi.wechatshop.ApiService
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.WAppContext
import com.qiushi.wechatshop.model.PhoneInfo
import com.qiushi.wechatshop.util.NetworkUtil
import com.qiushi.wechatshop.util.Utils
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitManager {

    private var client: OkHttpClient? = null
    private var retrofit: Retrofit? = null

    val service: ApiService by lazy { getRetrofit()!!.create(ApiService::class.java) }

    /**
     * 设置公共参数
     */
    private fun addQueryParameterInterceptor(): Interceptor {
        return Interceptor {
            val originalRequest = it.request()
            val request: Request
            val modifiedUrl = originalRequest.url().newBuilder().build()
            request = originalRequest.newBuilder().url(modifiedUrl).build()
            Logger.d("URL = " + request.url().toString())
            var response = it.proceed(request)
            val responseBody = response.body()
            if (null != responseBody) {

                if (Constants.IS_DEVELOPER && "POST" == request.method()) {//打印请求参数
                    val sb = StringBuilder()
                    if (request.body() is FormBody) {
                        val body = request.body() as FormBody?
                        if (null != body) {
                            for (i in 0 until body.size()) {
                                sb.append(body.encodedName(i)).append("=").append(body.encodedValue(i)).append(",")
                            }
                            if (sb.isNotEmpty()) {
                                sb.delete(sb.length - 1, sb.length)
                                Logger.d("Params ? " + sb.toString())
                            }
                        }
                    }
                }
                val content = responseBody.string()
                Logger.d("Response = $content")
                response = response.newBuilder()
                        .body(okhttp3.ResponseBody.create(responseBody.contentType(), content))
                        .build()
            }

            response
        }
    }

    /**
     * 设置头
     */
    private fun addHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                    .header("imei", PhoneInfo.getInstance().imei)
                    .header("model", PhoneInfo.getInstance().model)
                    .header("brand", PhoneInfo.getInstance().brand)
                    .header("version", PhoneInfo.getInstance().version)
                    .header("channel", PhoneInfo.getInstance().channel)
                    .header("device", "android")
                    .method(originalRequest.method(), originalRequest.body())

            val headers = Utils.getHttpHeaders()
            if (headers != null) {
                for (entry in headers.entries) {
                    requestBuilder.addHeader(entry.key, entry.value)
                }
            }

            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    /**
     * 设置缓存
     */
    private fun addCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (!NetworkUtil.isNetworkAvailable()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build()
            }
            val response = chain.proceed(request)
            if (NetworkUtil.isNetworkAvailable()) {
                val maxAge = 0
                // 有网络时 设置缓存超时时间0个小时 ,意思就是不读取缓存数据,只对get有用,post没有缓冲
                response.newBuilder()
                        .header("Cache-Control", "public, max-age=$maxAge")
                        .removeHeader("Retrofit")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .build()
            } else {
                // 无网络时，设置超时为4周  只对get有用,post没有缓冲
                val maxStale = 60 * 60 * 24 * 28
                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                        .removeHeader("nyn")
                        .build()
            }
            response
        }
    }

    private fun getRetrofit(): Retrofit? {
        if (retrofit == null) {
            synchronized(RetrofitManager::class.java) {
                if (retrofit == null) {
                    val httpLoggingInterceptor = HttpLoggingInterceptor()
                    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

                    //设置请求的缓存的大小跟位置
                    val cacheFile = File(WAppContext.context!!.cacheDir, "cache")
                    val cache = Cache(cacheFile, 1024 * 1024 * 50) //50Mb 缓存的大小

                    client = OkHttpClient.Builder()
                            .addInterceptor(addQueryParameterInterceptor())  //参数添加
                            .addInterceptor(addHeaderInterceptor()) // token过滤
                            //.addInterceptor(addCacheInterceptor())
                            .addInterceptor(httpLoggingInterceptor) //日志,所有的请求响应度看到
                            .cache(cache)  //添加缓存
                            .connectTimeout(60L, TimeUnit.SECONDS)
                            .readTimeout(60L, TimeUnit.SECONDS)
                            .writeTimeout(60L, TimeUnit.SECONDS)
                            .build()

                    retrofit = Retrofit.Builder()
                            .baseUrl(Constants.HOST)
                            .client(client!!)
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                }
            }
        }
        return retrofit
    }
}