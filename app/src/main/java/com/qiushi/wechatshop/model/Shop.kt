package com.qiushi.wechatshop.model

import com.heaven7.android.dragflowlayout.IDraggable
import java.io.Serializable

/**
 * Created by Rylynn on 2018-05-18.
 */
data class Shop(var id: Long, var name: String, var des: String, var logo: String, var goods: ArrayList<Goods>) : Serializable, IDraggable {
    override fun isDraggable(): Boolean {
        return when (name) {
            "我的店" -> false
            else -> true
        }
    }
}