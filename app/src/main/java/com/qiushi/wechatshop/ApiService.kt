package com.qiushi.wechatshop

import com.qiushi.wechatshop.model.Order
import com.qiushi.wechatshop.model.Shop
import com.qiushi.wechatshop.net.BaseResponse
import com.qiushi.wechatshop.util.oss.UploadFile
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    /**
     * 用户关注的店铺
     */
    @POST("user/shop")
    fun shopList(): Observable<BaseResponse<ArrayList<Shop>>>

    /**
     * 关注新店铺
     */
    @FormUrlEncoded
    @POST("MobileApp/user/add_shop")
    fun addShop(@Field("invite_code") code: String): Observable<BaseResponse<Shop>>

    /**
     * 用户关注的店铺详情
     */
    @FormUrlEncoded
    @POST("shop")
    fun shopDetail(@Field("shop_id") page: Long): Observable<BaseResponse<Shop>>

    /**
     * 订单列表
     */
    @POST("shop/order")
    fun orderList(): Observable<BaseResponse<ArrayList<Order>>>


    /**
     * 置顶 取消置顶
     */
    @FormUrlEncoded
    @POST("Goods/goods_top")
    fun setTop(@Field("goods_id") goods_id: Long): Observable<BaseResponse<Boolean>>

    /**
     * 上架下架
     */

    @FormUrlEncoded
    @POST("Goods/goods_enable")
    fun upShop(@Field("goods_id") goods_id: Long): Observable<BaseResponse<Boolean>>


    @FormUrlEncoded
    @POST("Goods/goods_del")
    fun deleteShop(@Field("goods_id") goods_id: Long): Observable<BaseResponse<Boolean>>


    @FormUrlEncoded
    @POST("Oss/get_token")
    fun gUploadFile(@Field("md5") md5: String): Observable<BaseResponse<UploadFile>>
}