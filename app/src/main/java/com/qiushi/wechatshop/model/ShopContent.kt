package com.qiushi.wechatshop.model

class ShopContent() {
    /**
     * id     新编辑的时候 id为空,
    oss_id    当传图时此项不为空，content为空
    content   当传文时此项不为空，oss_id为空
    is_del    当要删除该项时is_del为1 ,新编辑的时候 isdel为空
     */

    var id: Int = 0
    var oss_id: Int = 0
    lateinit var content: String
    var is_del: Int = 0
}