package com.qiushi.wechatshop.net.exception

object ErrorStatus {
    /**
     * 未知错误
     */
    @JvmField
    val UNKNOWN_ERROR = 1002

    /**
     * 服务器内部错误
     */
    @JvmField
    val SERVER_ERROR = 1003

    /**
     * 网络连接超时
     */
    @JvmField
    val NETWORK_ERROR = 1004

    /**
     * 解析异常
     */
    @JvmField
    val JSON_ERROR = 1005
}