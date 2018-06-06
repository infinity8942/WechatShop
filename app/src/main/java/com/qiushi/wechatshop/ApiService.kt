package com.qiushi.wechatshop

import com.qiushi.wechatshop.model.Shop
import com.qiushi.wechatshop.net.BaseResponse
import com.qiushi.wechatshop.test.Beauty
import com.qiushi.wechatshop.test.TestResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("meinv")//TODO test
    fun test(@Query("key") key: String, @Query("page") page: Int, @Query("num") num: Int)
            : Observable<TestResponse<ArrayList<Beauty>>>

    /**
     * 用户关注的店铺
     */
    @POST("MobileApp/user/shop")
    fun shopList(): Observable<BaseResponse<ArrayList<Shop>>>
}