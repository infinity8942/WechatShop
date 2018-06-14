package com.qiushi.wechatshop.model

/**
 * Created by Rylynn on 2018-05-18.
 *
 * 朋友圈素材
 */
data class Moment(var id: Int, var content: String, var type: Int, var images: ArrayList<NineImage>) {

    //test
    constructor(content: String, images: ArrayList<NineImage>) : this(1, content, 1, images)
}