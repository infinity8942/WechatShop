package com.qiushi.wechatshop.config

/**
 * Created by Rylynn on 2018-05-18.
 *
 * 常量
 */
object Constants {
    const val IS_DEVELOPER = true //TODO 开发模式开关,发版时置为false
    const val DEBUG = false //是否测试环境

    @JvmField
    val HOST = if (DEBUG) "http://top6000.qiushizixun.com/" else "http://wap.top6000.com/"
    const val OssEndPoint = "http://oss-cn-beijing.aliyuncs.com"
    @JvmField
    val OssCallback = HOST + "OSS/app_response_call_back" //"http://192.168.1.82:81"

    //Key
    const val ALI_OSS_APPKEY = "rCbpsTRroEUuf4hS"
    const val ALI_OSS_SECRET = "GvK8xfPJJjiq9eIdpwbJiXoFkOuzbk"
    const val BUGLY_APPID = "86cacb8516"

    //Notification
    const val LOGIN = 100 //登录
    const val LOGOUT = 101//登出
    const val LIKE = 200//点赞
    const val UNLIKE = 201//取消点赞
    const val COMMENT = 250//评论
    const val COLLECT = 300//收藏
    const val UNCOLLECT = 301//取消收藏


}