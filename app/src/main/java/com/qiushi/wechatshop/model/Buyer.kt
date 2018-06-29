package com.qiushi.wechatshop.model

/**
 * Created by Rylynn on 2018-05-18.
 *
 * 买家
 */
data class Buyer(var id: Long, var receiver: String, var avatar: String, var mobile: String, var address: String, var area: String, var is_default: Int)