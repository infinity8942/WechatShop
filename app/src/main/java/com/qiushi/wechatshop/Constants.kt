package com.qiushi.wechatshop

/**
 * Created by Rylynn on 2018-05-18.
 */
object Constants {
    //测试图片
    const val IMAGE1 = "http://image.hnol.net/c/2018-06/04/20/201806042031543551-2053485.jpg"
    const val IMAGE2 = "http://image.hnol.net/c/2018-05/30/17/201805301706219791-5900080.jpg"
    const val IMAGE3 = "http://image.hnol.net/c/2018-05/25/20/201805252007168161-2053485.jpg"

    const val IS_DEVELOPER = true //TODO 开发模式开关,发版时置为false
    const val DEBUG = false //是否测试环境

    @JvmField
//    val HOST = if (DEBUG) "http://top6000.qiushizixun.com/" else "http://wap.top6000.com/"
    val HOST = "http://api.tianapi.com/"//TODO test
    const val OssEndPoint = "http://oss-cn-beijing.aliyuncs.com"
    @JvmField
    val OssCallback = HOST + "OSS/app_response_call_back" //"http://192.168.1.82:81"

    const val PAGE_NUM = 15 //每页请求数量

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