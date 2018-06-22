package com.qiushi.wechatshop.ui.order

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.model.Order
import com.qiushi.wechatshop.util.ImageHelper

/**
 * Created by Rylynn on 2018-06-08.
 *
 * 订单Adapter
 */
class OrderAdapter : BaseQuickAdapter<Order, BaseViewHolder>(R.layout.item_order, null) {
    override fun convert(helper: BaseViewHolder, item: Order?) {
        //TODO 测试数据
        ImageHelper.loadAvatar(mContext, helper.getView(R.id.logo), Constants.AVATAR, 24)
        helper.setText(R.id.name, "咪蒙韩国代购" + helper.adapterPosition)
        helper.setText(R.id.status, "等待卖家发货")
        helper.setText(R.id.amount, "共计1件商品")
        helper.setText(R.id.price, "￥240.00")

        val list = ArrayList<Goods>()
        for (i in 1..2) {
            list.add(Goods("商品" + i))
        }

        val recyclerView: RecyclerView = helper.getView(R.id.mRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(mContext)
        recyclerView.adapter = OrderGoodsAdapter(list)

        helper.addOnClickListener(R.id.layout_shop).addOnLongClickListener(R.id.layout_shop)
    }
}