package com.qiushi.wechatshop

import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {

    /**
     * 热门搜索词
     */
    @GET("v3/queries/hot")
    fun getHotWord(): Observable<ArrayList<String>>
}