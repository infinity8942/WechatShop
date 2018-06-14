package com.qiushi.wechatshop.util.oss


class Error(var code: Int, var messs: String) : Throwable() {


    override fun toString(): String {
        return "Error{" +
                "code=" + +code
        '}'.toString()
    }
}