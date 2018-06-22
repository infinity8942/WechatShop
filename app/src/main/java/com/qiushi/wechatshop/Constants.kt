package com.qiushi.wechatshop

/**
 * Created by Rylynn on 2018-05-18.
 */
object Constants {
    //测试图片
    const val IMAGE1 = "http://image.hnol.net/c/2018-06/04/20/201806042031543551-2053485.jpg"
    const val IMAGE2 = "http://image.hnol.net/c/2018-05/30/17/201805301706219791-5900080.jpg"
    const val IMAGE3 = "http://image.hnol.net/c/2018-05/25/20/201805252007168161-2053485.jpg"
    const val AVATAR = "https://static.chiphell.com/forum/201806/06/111637jjxw8cwnj88xss8k.jpg"

    //测试店铺ID
    const val SHOP_ID: Long = 10091

    //主页测试图片
    const val GOOD0 = "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1528702578&di=a30dcf155043b230465a96ef469c5d41&src=http://pic15.photophoto.cn/20100429/0035035090388467_b.jpg"
    const val GOOD1 = "http://pic29.photophoto.cn/20131010/0035035066575835_b.jpg"
    const val GOOD2 = "http://pic29.nipic.com/20130507/3822951_123501747000_2.jpg"
    const val GOOD3 = "http://pic33.photophoto.cn/20141119/0035035080831168_b.jpg"
    const val GOOD4 = "http://pic21.photophoto.cn/20111116/0035035024493346_b.jpg"
    const val GOOD5 = "http://pic29.nipic.com/20130507/3822951_123515435000_2.jpg"
    const val GOOD6 = "http://img5.duitang.com/uploads/item/201509/26/20150926093118_S2PQj.thumb.700_0.jpeg"
    const val GOOD7 = "http://pic150.nipic.com/file/20171227/21540071_114434490000_2.jpg"

    const val IS_DEVELOPER = true //TODO 开发模式开关,发版时置为false
    const val DEBUG = true //是否测试环境
    @JvmField
    val HOST = if (DEBUG) "http://wechatshop.qiushizixun.com/" else "http://wap.top6000.com/MobileApp/"

    const val OssEndPoint = "http://oss-cn-beijing.aliyuncs.com"
    @JvmField
    val OssCallback = HOST + "Oss/app_response_call_back" //"http://192.168.1.82:81"

    const val PAGE_NUM = 15 //每页请求数量

    //Key
    const val ALI_OSS_APPKEY = "rCbpsTRroEUuf4hS"
    const val ALI_OSS_SECRET = "GvK8xfPJJjiq9eIdpwbJiXoFkOuzbk"
    const val BUGLY_APPID = "86cacb8516"
    const val UMENG_APPKEY = "5b28cdddb27b0a532e000034"
    const val UMENG_SECRET = "17c7e39d636a0b8fba2fbeef778aee02"

    //Code

    const val ADDIMG_RESUALT = 1003 //foot添加产品相册回调
    const val ADDIMG_ITEM_REQUEST = 1004 //ITEM添加产品相册回调
    const val EDIT_TEXT_REQUEST = 1005 //编辑文本的回调
    const val ADDIMG_BG = 1006//背景进入相册回调

    const val ADDIMG_LOGO = 1007//店铺装修logo
    const val ADDIMG_GOODS_BG = 1008//店铺装修背景


    const val TOKEN = "2813c9014ff9581978e3f616e09b1d79"
    const val CILIENT = "9939e296d7e891ef87a2fa7f521b874a"
}