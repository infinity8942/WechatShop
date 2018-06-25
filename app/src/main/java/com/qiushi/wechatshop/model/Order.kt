package com.qiushi.wechatshop.model

/**
 * Created by Rylynn on 2018-05-18.
 *
 * 订单
 */
data class Order(var id: Int, var number: String, var price: Double, var status: Int, var goods: ArrayList<Goods>, var create_time: Long,
                 var pay_time: Long, var deliver_time: Long, var achieve_time: Long, var payment: Int, var shop: Shop, var express: Express
                 , var count: Int, var content: String)