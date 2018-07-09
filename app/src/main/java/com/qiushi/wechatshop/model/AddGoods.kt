package com.qiushi.wechatshop.model

import java.io.Serializable

class AddGoods : Serializable {

    var shop_id: Long = 0
    var name: String = ""
    var cover: String = ""
    var price: Double = 0.00
    var stock: Int = 0
    var brief: String = ""
    var content: ArrayList<Content>? = null
    var goods_id: Long = 0
    var is_todo: Boolean = false
    var cover_url: String = ""
    var sales_brief: String = ""
    var left_todo: Int = 0
}