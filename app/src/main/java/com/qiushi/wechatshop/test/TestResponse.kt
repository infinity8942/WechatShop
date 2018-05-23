package com.qiushi.wechatshop.test

class TestResponse<out T>(val code: Int,
                          val msg: String,
                          val newslist: T) {

    fun isSuccess(): Boolean {
        if (code == 200) return true
        return false
    }
}