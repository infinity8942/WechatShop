package com.qiushi.wechatshop.model

import java.io.Serializable

class Content : Serializable {
    var id: Long = 0
    var oss_id: Long = 0
    var content: String = ""
    var is_del: Int = 0
    var img: String = ""
}