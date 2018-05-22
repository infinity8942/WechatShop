package com.qiushi.wechatshop

import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.net.BaseResponse
import io.reactivex.Observable
import retrofit2.http.POST

interface ApiService {

    @POST("MobileAppv2/clarity")
    fun getHotWord(): Observable<BaseResponse<List<Goods>>>
}