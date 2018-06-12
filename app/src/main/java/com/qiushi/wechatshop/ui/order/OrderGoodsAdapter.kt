package com.qiushi.wechatshop.ui.order

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.util.ImageHelper

/**
 * Created by Rylynn on 2018-06-08.
 *
 * 订单下商品Adapter
 */
class OrderGoodsAdapter(list: ArrayList<Goods>) : BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_order_goods, list) {
    override fun convert(helper: BaseViewHolder, item: Goods?) {
        ImageHelper.loadImageWithCorner(mContext, helper.getView(R.id.logo), Constants.AVATAR, 64, 64, 5.toFloat())
        helper.setText(R.id.name, "尼萌十月当季特别调制柠檬蛋糕夏日嬷嬷尼萌十月当季特别调制柠檬蛋糕夏日嬷嬷")
    }
}