package com.qiushi.wechatshop.net

class BaseResponse<out T>(val code: Int,
                          val msg: String,
                          val data: T) {

    fun isSuccess(): Boolean {
        if (code == 200) return true
        return false
    }
}