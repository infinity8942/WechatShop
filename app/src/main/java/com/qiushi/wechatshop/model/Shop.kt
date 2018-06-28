package com.qiushi.wechatshop.model

import com.heaven7.android.dragflowlayout.IDraggable
import java.io.Serializable

/**
 * Created by Rylynn on 2018-05-18.
 *
 * 店铺
 */
data class Shop(var id: Long, var name: String, var des: String, var logo: String, var cover: String, var is_owner: Boolean, var goods: ArrayList<Goods>
                , var cash_flow: Float, var cash_forzen: Float, var cash_all: Float
                , var menu_list: ArrayList<Function>,var is_boss:String) : Serializable, IDraggable {

    override fun isDraggable(): Boolean {
        return !is_owner
    }
}