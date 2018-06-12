package com.qiushi.wechatshop

import com.qiushi.wechatshop.model.Shop
import com.qiushi.wechatshop.net.BaseResponse
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    /**
     * 用户关注的店铺
     */
    @POST("MobileApp/user/shop")
    fun shopList(): Observable<BaseResponse<ArrayList<Shop>>>

    /**
     * 用户关注的店铺详情
     */
    @FormUrlEncoded
    @POST("MobileApp/shop")
    fun shopDetail(@Field("shop_id") page: Long): Observable<BaseResponse<Shop>>


    @FormUrlEncoded
    @POST
    fun setTop(@Field("goods_id") goods_id: Long): Observable<BaseResponse<Boolean>>
}