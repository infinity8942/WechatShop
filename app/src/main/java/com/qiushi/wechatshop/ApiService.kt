package com.qiushi.wechatshop

import com.qiushi.wechatshop.model.*
import com.qiushi.wechatshop.net.BaseResponse
import com.qiushi.wechatshop.util.oss.UploadFile
import com.tencent.mm.opensdk.modelpay.PayReq
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    /**
     * 用户关注的店铺
     */
    @POST("User/shop_list")
    fun shopList(): Observable<BaseResponse<ArrayList<Shop>>>

    /**
     * 用户关注的店铺详情
     */
    @FormUrlEncoded
    @POST("Shop/shop")
    fun shopDetail(@Field("shop_id") page: Long): Observable<BaseResponse<Shop>>

    /**
     * 关注新店铺
     */
    @FormUrlEncoded
    @POST("User/add_shop")
    fun addShop(@Field("invite_code") code: String): Observable<BaseResponse<Shop>>

    /**
     * 修改关注的店铺
     */
    @FormUrlEncoded
    @POST("User/edit_shop")
    fun editShop(@Field("shop_ids") code: String): Observable<BaseResponse<Boolean>>

    /**
     * 订单列表
     */
    @FormUrlEncoded
    @POST("shop/order")
    fun orderList(@Field("start") start: Int, @Field("length") length: Int): Observable<BaseResponse<ArrayList<Order>>>

    /**
     * 用户订单
     */
    @FormUrlEncoded
    @POST("Order/user_order")
    fun userOrders(@Field("status") status: Int, @Field("keywords") keywords: String, @Field("start") start: Int, @Field("length") length: Int): Observable<BaseResponse<ArrayList<Order>>>

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("MobileApp/user/login")
    fun login(@Field("phone") phone: String, @Field("password") password: String): Observable<BaseResponse<Boolean>>

    /**
     * 微信登录
     */
    @FormUrlEncoded
    @POST("MobileApp/user/register")
    fun loginWX(@FieldMap params: Map<String, String>): Observable<BaseResponse<User>>

    /**
     * 绑定手机号
     */
    @FormUrlEncoded
    @POST("User/mobile_bind")
    fun bindPhone(@Field("phone") phone: String, @Field("verify_code") verify_code: String): Observable<BaseResponse<Boolean>>

    /**
     * 支付宝支付
     */
    @FormUrlEncoded
    @POST("MobileApp/order/alipay")
    fun getAliPay(@Field("number") phone: String): Observable<BaseResponse<String>>

    /**
     * 微信支付
     */
    @FormUrlEncoded
    @POST("MobileApp/order/wechatpay")
    fun getWechatPay(@Field("number") phone: String): Observable<BaseResponse<PayReq>>

    /**
     * 意见反馈
     */
    @FormUrlEncoded
    @POST("User/feedback")
    fun feedback(@Field("type") type: Int, @Field("des") des: String, @Field("phone") phone: String): Observable<BaseResponse<Boolean>>

    /**
     * 素材列表
     */
    @FormUrlEncoded
    @POST("Shop/moments")
    fun getMoments(@Field("shop_id") shop_id: Long, @Field("type") type: Int, @Field("start") start: Int, @Field("length") length: Int): Observable<BaseResponse<ArrayList<Moment>>>

    /**
     * 添加素材
     */
    @FormUrlEncoded
    @POST("Shop/moments_edit")
    fun addMoments(@Field("type") type: Int, @Field("content") content: String): Observable<BaseResponse<Boolean>>

    /**
     * 素材编辑
     */
    @FormUrlEncoded
    @POST("Shop/moments_edit")
    fun editMoments(@Field("id") moments_id: Long, @Field("type") type: Int, @Field("content") content: String): Observable<BaseResponse<Boolean>>

    /**
     * 删除素材
     */
    @FormUrlEncoded
    @POST("MobileApp/shop/moments/del")
    fun delMoments(@Field("moment_id") moment_id: Long): Observable<BaseResponse<Boolean>>

    //==============================================================================================
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
    @POST("Goods/shop_goods")
    fun getMnagerGoods(@Field("shop_id") shop_id: Long, @Field("state") state: Int, @Field("keyword") keyword: String): Observable<BaseResponse<List<Goods>>>

    @FormUrlEncoded
    @POST("Oss/get_token")
    fun gUploadFile(@Field("md5") md5: String): Observable<BaseResponse<UploadFile>>


    @POST("User/my_shop")
    fun getMyshop(): Observable<BaseResponse<Shop>>

    @FormUrlEncoded
    @POST("Menu/get_menu_info")
    fun getMore(@Field("shop_id") shop_id: Long): Observable<BaseResponse<More>>

    @FormUrlEncoded
    @POST("Menu/menu_edit")
    fun menuMore(@Field("shop_id") shop_id: Long, @Field("menu_id") menu_id: String, @Field("is_del") is_del: String): Observable<BaseResponse<Boolean>>


    @FormUrlEncoded
    @POST("Goods/goods_edit")
    fun postGoods(@Field("json") json: String): Observable<BaseResponse<Boolean>>

}