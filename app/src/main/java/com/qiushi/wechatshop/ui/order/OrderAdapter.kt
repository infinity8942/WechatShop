package com.qiushi.wechatshop.ui.order

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Order
import com.qiushi.wechatshop.util.ImageHelper

/**
 * Created by Rylynn on 2018-06-08.
 *
 * 订单Adapter
 */
class OrderAdapter : BaseQuickAdapter<Order, BaseViewHolder>(R.layout.item_order, null) {
    override fun convert(helper: BaseViewHolder, item: Order?) {
        ImageHelper.loadAvatar(mContext, helper.getView(R.id.logo), Constants.AVATAR, 24)
        helper.setText(R.id.name, "咪蒙韩国代购" + helper.adapterPosition)
        helper.setText(R.id.status, "等待卖家发货")
    }
}