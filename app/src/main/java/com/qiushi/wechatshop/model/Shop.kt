package com.qiushi.wechatshop.model

/**
 * Created by Rylynn on 2018-05-18.
 */
data class Shop(var id: Long, var name: String, var logo: String, var goods: ArrayList<Goods>)