package com.qiushi.wechatshop.model

/**
 * Created by Rylynn on 2018-05-18.
 *
 * 订单
 */
data class Order(var id: Long, var numbers: String, var price: Double, var status: Int, var remind_pay: Int, var remind_send: Int, var type: Int, var goods: ArrayList<Goods>, var create_time: Long,
                 var pay_time: Long, var shipping_time: Long, var shipping_end_time: Long, var pay_type: Int, var shop: Shop, var express: Express, var num: Int,
                 var content: String, var user: Buyer)