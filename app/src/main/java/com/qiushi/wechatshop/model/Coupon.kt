package com.qiushi.wechatshop.model

/**
 * Created by Rylynn on 2018-05-18.
 *
 * 优惠券
 */
data class Coupon(var id: Int, var name: String, var offset: Int, var start_time: Int, var end_time: Int, var requirement: Int, var is_get: Boolean) {

    //test
    constructor(offset: Int) : this(1, "", offset, 1, 1, 1, false)
}