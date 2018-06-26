package com.qiushi.wechatshop.ui.order

import android.content.Intent
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
        when (order.status) {
            0 -> {
                helper.setGone(R.id.layout_action, true).setText(R.id.action, "提醒支付")
                helper.setGone(R.id.number, false)
                helper.setText(R.id.status, "等待买家付款")
            }
            1 -> {
                helper.setGone(R.id.layout_action, true).setText(R.id.action, "标记发货")
                helper.setGone(R.id.number, true)
                helper.setText(R.id.status, "买家已付款")
            }
            2 -> {
                helper.setGone(R.id.layout_action, true).setText(R.id.action, "提醒支付")
                helper.setGone(R.id.number, false)
                helper.setText(R.id.status, "等待买家收货")
            }
            3 -> {
                helper.setGone(R.id.layout_action, false)
                helper.setGone(R.id.number, false)
                helper.setText(R.id.status, "已完成")
            }
        }
        helper.setText(R.id.amount, "共计" + order.num + "件商品")
        helper.setText(R.id.price, "￥" + order.price)

        val recyclerView: RecyclerView = helper.getView(R.id.mRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(mContext)
        val mAdapterGoods = OrderGoodsAdapter()
        recyclerView.adapter = mAdapterGoods
        mAdapterGoods.setNewData(order.goods)

        mAdapterGoods.setOnItemClickListener { _, _, _ ->
            val intent = Intent(mContext, OrderDetailActivity::class.java)
            intent.putExtra("id", order.id)
            mContext.startActivity(intent)
        }

        helper.addOnClickListener(R.id.layout_shop)
    }
}