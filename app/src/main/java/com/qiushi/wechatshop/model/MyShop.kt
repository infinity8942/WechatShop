package com.qiushi.wechatshop.model

import android.os.Parcel
import android.os.Parcelable
import com.chad.library.adapter.base.entity.MultiItemEntity

class MyShop(var id: Int, var name: String, var cash_flow: Float, var cash_forzen: Float,
             var cash_all: Float, var todo: Int, var menu_list: ArrayList<Function>, var goods: ArrayList<ShopOrder>) : MultiItemEntity {
    override fun getItemType(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // cash_flow:流动金额
    // cash_forzen:冻结金额
    //cash_all:资产总额

    constructor(menu_list: ArrayList<Function>, goods: ArrayList<ShopOrder>) : this(

            1,
            "测试名字",
            1000.0f,
            1000.0f,
            1234.0f,
            4,
            menu_list, goods
    )


}
