package com.qiushi.wechatshop.model

/**
 * Created by Rylynn on 2018-05-18.
 *
 * 订单
 */
data class Order(var id: Int, var number: String, var price: Double, var status: Int, var goods: ArrayList<Goods>, var create_time: Int,
                 var pay_time: Int, var deliver_time: Int, var achieve_time: Int, var payment: Int, var shop: Shop, var express: Express)