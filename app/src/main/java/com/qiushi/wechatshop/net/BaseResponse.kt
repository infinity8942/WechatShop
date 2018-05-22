package com.qiushi.wechatshop.net

class BaseResponse<T>(val code: Int,
                      val info: String,
                      val data: T) {

    fun isSuccess(): Boolean {
        if (code == 200) return true
        return false
    }
}