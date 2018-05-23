package com.qiushi.wechatshop.net.exception

class Error(var code: Int, var msg: String) {

    override fun toString(): String {
        return "Error{" +
                "code=" + code +
                ",msg=" + msg +
                '}'.toString()
    }
}