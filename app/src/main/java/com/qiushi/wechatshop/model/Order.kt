package com.qiushi.wechatshop.model

/**
 * Created by Rylynn on 2018-05-18.
 *
 * 订单
 */
data class Order(var id: Int, var number: String, var price: Double, var status: Int, var goods: ArrayList<Goods>, var create_time: Int,
                 var pay_time: Int, var deliver_time: Int, var achieve_time: Int, var payment: Int) {
    constructor() : this(1, "2039029302930", 240.00, 1, ArrayList<Goods>(), 1, 1, 1, 1, 1)
}