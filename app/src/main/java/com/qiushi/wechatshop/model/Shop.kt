package com.qiushi.wechatshop.model

import com.heaven7.android.dragflowlayout.IDraggable
import java.io.Serializable

/**
 * Created by Rylynn on 2018-05-18.
 */
data class Shop(var id: Long, var name: String, var des: String, var logo: String, var goods: ArrayList<Goods>
                , var cash_flow: Float, var cash_forzen: Float, var cash_all: Float
                , var msg_count: Int, var menu_list: ArrayList<Function>) : Serializable, IDraggable {

    constructor(name: String) : this(1, name, "1", "1", ArrayList(), 0.1f, 0.1f, 0.1f, 1, ArrayList())

    override fun isDraggable(): Boolean {
        return when (name) {
            "我的店" -> false
            else -> true
        }
    }
}