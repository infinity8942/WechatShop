package com.qiushi.wechatshop.model

import java.io.Serializable

/**
 * Created by Rylynn on 2018-05-18.
 *
 * 买家、收货地址
 */
data class Buyer(var id: Long, var receiver: String, var avatar: String, var mobile: String, var address: String, var area: String, var is_default: Int) : Serializable