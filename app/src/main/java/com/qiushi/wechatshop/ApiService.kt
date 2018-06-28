package com.qiushi.wechatshop

import com.qiushi.wechatshop.model.*
import com.qiushi.wechatshop.net.BaseResponse
import com.qiushi.wechatshop.util.oss.UploadFile
import com.tencent.mm.opensdk.modelpay.PayReq
import io.reactivex.Observable
import retrofit2.http.*

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
     * 店铺装修
     */
    @FormUrlEncoded
    @POST("Shop/edit_shop")
    fun editShop(@Field("name") name: String, @Field("oss_id") oss_id: String,
                 @Field("shop_id") shop_id: Long, @Field("bg_oss_id") bg_oss_id: String): Observable<BaseResponse<String>>

    /**
     * 店铺、用户订单列表
     */
    @FormUrlEncoded
    @POST("Order/shop_order")
    fun getOrders(@Field("shop_id") shop_id: Long, @Field("numbers") numbers: String,
                  @Field("pay_time") pay_time: Int, @Field("key") key: String,
                  @Field("start_time") start_time: Long, @Field("end_time") end_time: Long,
                  @Field("from") from: Int, @Field("status") status: Int,
                  @Field("start") start: Int, @Field("length") length: Int): Observable<BaseResponse<ArrayList<Order>>>

    /**
     * 手动添加订单
     */
    @FormUrlEncoded
    @POST("Order/add_order")
    fun addOrder(@Field("shop_id") shop_id: Long, @Field("goods_id") goods_id: Long,
                 @Field("des") des: String,
                 @Field("price") price: Double, @Field("amount") amount: Int): Observable<BaseResponse<Boolean>>

    /**
     * 标记发货
     */
    @FormUrlEncoded
    @POST("Order/shipping_order")
    fun markAsDeliver(@Field("order_id") order_id: Long, @Field("express_number") express_number: String): Observable<BaseResponse<Boolean>>

    /**
     * 提醒支付
     */
    @FormUrlEncoded
    @POST("Order/remind_pay")
    fun notifyToPay(@Field("order_id") order_id: Long): Observable<BaseResponse<Boolean>>

    /**
     * 提醒发货
     */
    @FormUrlEncoded
    @POST("Order/remind_send")
    fun notifyToDeliver(@Field("order_id") order_id: Long): Observable<BaseResponse<Boolean>>

    /**
     * 确认收货
     */
    @FormUrlEncoded
    @POST("Order/achieve_order")
    fun markAsDone(@Field("order_id") order_id: Long): Observable<BaseResponse<Boolean>>

    /**
     * 修改价格
     */
    @FormUrlEncoded
    @POST("Order/reset_order_price")
    fun editOrderPrice(@Field("id") order_id: Long, @Field("price") price: Double): Observable<BaseResponse<Boolean>>

    /**
     * 删除订单
     */
    @FormUrlEncoded
    @POST("Order/del_order")
    fun delOrder(@Field("order_id") order_id: Long): Observable<BaseResponse<Boolean>>

    /**
     * 取消订单
     */
    @FormUrlEncoded
    @POST("Order/cancel_order")
    fun cancelOrder(@Field("order_id") order_id: Long): Observable<BaseResponse<Boolean>>

    /**
     * 订单详情
     */
    @FormUrlEncoded
    @POST("Order/order_detail")
    fun getOrderDetail(@Field("shop_id") shop_id: Long, @Field("order_id") order_id: Long): Observable<BaseResponse<Order>>

    /**
     * 添加商品到购物车
     */
    @FormUrlEncoded
    @POST("User/add_to_cart")
    fun addToShopCart(@Field("shop_id") shop_id: Long, @Field("goods_id") goods_id: Long,
                      @Field("num") num: Int): Observable<BaseResponse<Boolean>>

    /**
     * 手机号登录
     */
    @FormUrlEncoded
    @POST("User/mobile_login")
    fun loginPhone(@Field("phone") phone: String, @Field("verify_code") verify_code: String): Observable<BaseResponse<User>>

    /**
     * 微信登录
     */
    @FormUrlEncoded
    @POST("User/third_login")
    fun loginWX(@Field("third_token") third_token: String, @Field("openid") openid: String, @Field("mobile") mobile: String): Observable<BaseResponse<User>>

    /**
     * 绑定手机号
     */
    @FormUrlEncoded
    @POST("User/mobile_bind")
    fun bindPhone(@Field("phone") phone: String, @Field("verify_code") verify_code: String): Observable<BaseResponse<String>>

    /**
     * 发送验证码
     */
    @FormUrlEncoded
    @POST("User/send_verify")
    fun sendVerifyCode(@Field("phone") phone: String): Observable<BaseResponse<String>>

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

    /**
     * 待办事项
     */
    @FormUrlEncoded
    @POST("Shop/shop_todo")
    fun getToDo(@Field("shop_id") shop_id: Long): Observable<BaseResponse<Todo>>

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
    fun getMnagerGoods(@Field("shop_id") shop_id: Long, @Field("status") status: Int, @Field("keyword") keyword: String, @Field("page") page: Int): Observable<BaseResponse<List<Goods>>>

    @FormUrlEncoded
    @POST("Oss/get_token")
    fun gUploadFile(@Field("md5") md5: String): Observable<BaseResponse<UploadFile>>

    @FormUrlEncoded
    @POST("User/my_shop")
    fun getMyshop(@Field("page") page: Int): Observable<BaseResponse<Shop>>

    @FormUrlEncoded
    @POST("Menu/get_menu_info")
    fun getMore(@Field("shop_id") shop_id: Long): Observable<BaseResponse<More>>

    @FormUrlEncoded
    @POST("Menu/menu_edit")
    fun menuMore(@Field("shop_id") shop_id: Long, @Field("menu_id") menu_id: String, @Field("is_del") is_del: String): Observable<BaseResponse<Boolean>>


    @FormUrlEncoded
    @POST("Goods/goods_edit")
    fun postGoods(@Field("json") json: String): Observable<BaseResponse<Boolean>>

    @FormUrlEncoded
    @POST("Goods/edit_goods_info")
    fun getGoods(@Field("goods_id") goods_id: Long): Observable<BaseResponse<AddGoods>>


    /**
     * 添加素材
     *
     */
    @FormUrlEncoded
    @POST("Shop/moments_edit")
    fun addMoments(@Field("type") type: Int, @Field("id") id: Long, @Field("content") content: String, @Field("shop_id") shop_id: Long, @Field("images") images: String): Observable<BaseResponse<Boolean>>


    /**
     * 信息编辑页面
     */
    @FormUrlEncoded
    @POST("Shop/moments_edit_info")
    fun editMomentsInfo(@Field("id") id: Long): Observable<BaseResponse<Moment>>

    @GET("https://api.weixin.qq.com/sns/oauth2/access_token")
    fun getWXtoken(@Query("appid") appid: String, @Query("secret") secret: String,
                   @Query("code") code: String, @Query("grant_type") grant_type: String)


    /**
     * 选择商品 (订单)
     */
    @FormUrlEncoded
    @POST("Order/check_goods")
    fun checkGoods(@Field("shop_id") shop_id: Long, @Field("page") page: Int): Observable<BaseResponse<SelectOrder>>


}