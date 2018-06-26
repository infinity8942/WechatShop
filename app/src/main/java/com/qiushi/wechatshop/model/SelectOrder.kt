package com.qiushi.wechatshop.model

class SelectOrder(var pagenum: String, var goods_list: ArrayList<Goods>?) {
    constructor() : this("1", null)
}