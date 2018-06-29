package com.qiushi.wechatshop

/**
 * Created by Rylynn on 2018-05-18.
 */
object Constants {

    const val IS_DEVELOPER = true //TODO 开发模式开关,发版时置为false
    const val DEBUG = true //是否测试环境
    @JvmField
    val HOST = if (DEBUG) "http://wechatshop.qiushizixun.com/" else "http://wap.top6000.com/MobileApp/"//TODO 正式地址

    //H5
    const val GOODS_DETAIL = "http://wechatshop.qiushizixun.com/H5/detail/id/"
    const val SHOPCART = "http://wechatshop.qiushizixun.com/H5/car"
    const val ADDRESS = "http://wechatshop.qiushizixun.com/H5/address"
    const val EXPRESS = "http://wechatshop.qiushizixun.com/H5/address"//TODO 物流H5
    const val PROTOCOL = "http://wechatshop.qiushizixun.com/H5/address"//TODO 协议H5

    //OSS
    const val OssEndPoint = "http://oss-cn-beijing.aliyuncs.com"
    @JvmField
    val OssCallback = HOST + "Oss/app_response_call_back" //"http://192.168.1.82:81"

    //每页请求数量
    const val PAGE_NUM = 15

    //手机号验证
    const val REGEX_MOBILE = "^1(3|4|5|7|8)\\d{9}$"

    //Key
    const val ALI_OSS_APPKEY = "rCbpsTRroEUuf4hS"
    const val ALI_OSS_SECRET = "GvK8xfPJJjiq9eIdpwbJiXoFkOuzbk"
    const val BUGLY_APPID = "86cacb8516"
    const val UMENG_APPKEY = "5b28cdddb27b0a532e000034"
    const val UMENG_SECRET = "17c7e39d636a0b8fba2fbeef778aee02"
    const val WX_ID = "wx5e51e01dcf0bf531"

    //Order Status
    const val READY_TO_PAY = 0  //待付款
    const val PAYED = 1         //待发货
    const val DELIVERED = 2     //已发货
    const val DONE = 3          //已完成
    const val CANCEL = 4        //已取消
    const val CUSTOM = 5        //自建订单

    //Code
    const val ADDIMG_RESUALT = 1003 //foot添加产品相册回调
    const val ADDIMG_ITEM_REQUEST = 1004 //ITEM添加产品相册回调
    const val EDIT_TEXT_REQUEST = 1005 //编辑文本的回调
    const val ADDIMG_BG = 1006//背景进入相册回调

    const val ADDSC_IMG = 1010//素材图片回调

    const val ADDIMG_LOGO = 1007//店铺装修logo
    const val ADDIMG_GOODS_BG = 1008//店铺装修背景

    const val DESCORA_REQUEST = 1011//店铺装修request

    const val ADD_IMG_REFRESH = 1012// 添加产品 刷新回调
    const val OPEN_SHOP_OR_ZX = 1013//开店 或者 装修
    const val MANAGER_GOODS = 1014//产品管理 置顶删除 等 刷新
}