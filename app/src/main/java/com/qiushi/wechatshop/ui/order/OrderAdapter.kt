package com.qiushi.wechatshop.ui.order

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Order
import com.qiushi.wechatshop.util.ImageHelper

/**
 * Created by Rylynn on 2018-06-08.
 *
 * 订单Adapter
 */
class OrderAdapter : BaseQuickAdapter<Order, BaseViewHolder>(R.layout.item_order, null) {
    override fun convert(helper: BaseViewHolder, order: Order) {

        ImageHelper.loadAvatar(mContext, helper.getView(R.id.logo), order.shop.logo, 24)
        helper.setText(R.id.name, order.shop.name)
        helper.setText(R.id.status, "等待卖家发货")//TODO 状态
        helper.setText(R.id.amount, "共计" + order.count + "件商品")
        helper.setText(R.id.price, "￥" + order.price)

        val recyclerView: RecyclerView = helper.getView(R.id.mRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(mContext)
        val mAdapterGoods = OrderGoodsAdapter()
        recyclerView.adapter = mAdapterGoods
        mAdapterGoods.setNewData(order.goods)

        helper.addOnClickListener(R.id.layout_shop).addOnLongClickListener(R.id.layout_shop)
    }
}