package com.qiushi.wechatshop.model

/**
 * Created by Rylynn on 2018-05-18.
 *
 * 用户（卖家、买家）
 */
data class User(var id: Long, var shop_id: Long, var nick: String, var avatar: String, var phone: String, var address: String)