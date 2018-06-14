package com.qiushi.wechatshop.model

/**
 * Created by Rylynn on 2018-05-18.
 *
 * 商品
 */
data class Goods(var id: Int, var name: String, var price: Double, var cover: String, var views: Int) {

    //test
    constructor(name: String) : this(1, name, 50.5, "", 1)
}