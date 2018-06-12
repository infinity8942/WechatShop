package com.qiushi.wechatshop

import com.qiushi.wechatshop.model.Order
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

    /**
     * 订单列表
     */
    @POST("MobileApp/shop/order")
    fun orderList(): Observable<BaseResponse<ArrayList<Order>>>

//    @FormUrlEncoded
//    @POST("MobileApp/shop/mine")
//    fun managerShop():Observable<>
}