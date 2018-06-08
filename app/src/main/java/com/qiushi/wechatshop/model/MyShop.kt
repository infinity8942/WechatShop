package com.qiushi.wechatshop.model

import android.os.Parcel
import android.os.Parcelable

class MyShop(var id: Int, var name: String, var cash_flow: Float, var cash_forzen: Float,
             var cash_all: Float, var todo: Int, var function: ArrayList<Function>, var goods: ArrayList<ShopOrder>) {


    constructor(function: ArrayList<Function>, goods: ArrayList<ShopOrder>) : this(

            1,
            "lij",
            1999f,
            3333f,
            34324f,
            23,
            ArrayList(), ArrayList()
    )


}
