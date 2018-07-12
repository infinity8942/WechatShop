package com.qiushi.wechatshop.ui.order

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.PriceUtil

/**
 * Created by Rylynn on 2018-06-08.
 *
 * 订单下商品Adapter
 */
class OrderGoodsAdapter(private val isManage: Boolean) : BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_order_goods) {
    override fun convert(helper: BaseViewHolder, goods: Goods) {
        ImageHelper.loadImageWithCorner(mContext, helper.getView(R.id.logo), goods.cover, 64, 64, 5.toFloat())
        helper.setText(R.id.name, goods.name)
        if (!isManage)
            helper.setText(R.id.price, "￥" + PriceUtil.doubleTrans(goods.price)).setText(R.id.num, "X " + goods.num)
    }
}