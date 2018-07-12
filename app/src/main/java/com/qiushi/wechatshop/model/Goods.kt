package com.qiushi.wechatshop.model

import java.io.Serializable

/**
 * Created by Rylynn on 2018-05-18.
 *
 * 商品
 */
data class Goods(var id: Long, var name: String, var price: Double, var cover: String, var views: Int, var sold: Int, var enable: Boolean,
                 var is_top: Boolean, var num: Int, var stock: Int, var limit_num: Int) : Serializable