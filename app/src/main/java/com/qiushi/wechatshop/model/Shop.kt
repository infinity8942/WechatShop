package com.qiushi.wechatshop.model

import com.heaven7.android.dragflowlayout.IDraggable

/**
 * Created by Rylynn on 2018-05-18.
 */
data class Shop(var id: Long, var name: String, var des: String, var logo: String, var goods: ArrayList<Goods>) : IDraggable {

    constructor(name: String) : this(1, name, "1", "1", ArrayList())

    override fun isDraggable(): Boolean {
        return when (name) {
            "我的店" -> false
            else -> true
        }
    }
}