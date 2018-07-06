package com.qiushi.wechatshop.ui.order

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.GlideApp
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.model.Order
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.web.WebActivity

/**
 * Created by Rylynn on 2018-06-08.
 *
 * 订单Adapter
 *
 * isManage 是否为卖家的订单管理
 */
class OrderAdapter(private val isManage: Boolean) : BaseQuickAdapter<Order, BaseViewHolder>(R.layout.item_order, null) {
    override fun convert(helper: BaseViewHolder, order: Order) {

        if (order.type == 1) {
            GlideApp.with(mContext).load(R.mipmap.ic_custom)
                    .override(DensityUtils.dp2px(24.toFloat()), DensityUtils.dp2px(24.toFloat()))
                    .transform(CircleCrop())
                    .into(helper.getView(R.id.logo))
        } else {
            ImageHelper.loadAvatar(mContext, helper.getView(R.id.logo), order.shop.logo, 24)
        }

        helper.setText(R.id.name, order.shop.name)

        when (order.status) {
            Constants.READY_TO_PAY -> {
                if (isManage) {
                    helper.setText(R.id.status, "等待买家付款")
//                            .setText(R.id.action, if (order.remind_pay == 0) "提醒支付" else "已提醒")
                    helper.setGone(R.id.action, false)
                    helper.setGone(R.id.action1, true).setText(R.id.action1, "修改价格")
                            .setGone(R.id.action2, true).setText(R.id.action2, "删除订单")
                } else {
                    helper.setText(R.id.status, "等待付款")
                    helper.setGone(R.id.action, true).setText(R.id.action, "立即付款")
                    helper.setGone(R.id.action1, true).setText(R.id.action1, "取消订单")
                            .setGone(R.id.action2, false)
                }
                helper.setGone(R.id.numbers, false)
            }
            Constants.PAYED -> {
                if (isManage) {
                    helper.setText(R.id.status, "买家已付款").setText(R.id.action, "确认发货")
                            .setGone(R.id.numbers, true)
                } else {
                    helper.setText(R.id.status, "等待卖家发货").setText(R.id.action, if (order.remind_send == 0) "提醒发货" else "已提醒")
                    helper.setGone(R.id.numbers, false)
                }
                helper.setGone(R.id.action, true).setGone(R.id.action1, false).setGone(R.id.action2, false)
            }
            Constants.DELIVERED -> {
                if (isManage) {
                    helper.setText(R.id.status, "等待买家收货").setGone(R.id.action, false)
                } else {
                    helper.setText(R.id.status, "卖家已发货")
                            .setGone(R.id.action, true).setText(R.id.action, "确认收货")
                }
                helper.setGone(R.id.action1, true).setText(R.id.action1, "查看物流")
                        .setGone(R.id.action2, false).setGone(R.id.numbers, false)
            }
            Constants.DONE -> {
                if (isManage) {
                    helper.setGone(R.id.action, false).setGone(R.id.action2, false)
                    helper.setText(R.id.action1, "删除订单")
                } else {
                    helper.setGone(R.id.action, true).setText(R.id.action, "再次购买")
                    helper.setText(R.id.action1, "删除订单")
                    helper.setGone(R.id.action2, true).setText(R.id.action2, "查看物流")
                }
                helper.setText(R.id.status, "已完成").setGone(R.id.action1, true)
                        .setGone(R.id.numbers, false)
            }
            Constants.CUSTOM -> {
                if (isManage) {
                    helper.setGone(R.id.action, false)
                    helper.setGone(R.id.action1, true).setText(R.id.action1, "删除订单")
                            .setGone(R.id.action2, false)
                }
                helper.setText(R.id.status, "").setGone(R.id.numbers, false)
            }
        }

        helper.setText(R.id.amount, "共计" + order.num + "件商品")
        helper.setText(R.id.price, "￥" + order.price)

        val recyclerView: RecyclerView = helper.getView(R.id.mRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(mContext)
        val mAdapterGoods = OrderGoodsAdapter(isManage)
        recyclerView.adapter = mAdapterGoods
        mAdapterGoods.setNewData(order.goods)

        mAdapterGoods.setOnItemClickListener { _, _, position ->
            goToGoodsDetails(order.goods[position])
        }

        helper.addOnClickListener(R.id.layout_shop).addOnClickListener(R.id.action)
                .addOnClickListener(R.id.action1).addOnClickListener(R.id.action2)
    }

    /**
     * 跳转商品详情页
     */
    private fun goToGoodsDetails(goods: Goods) {
        val intent = Intent(mContext, WebActivity::class.java)
        intent.putExtra(WebActivity.PARAM_TITLE, goods.name)
        intent.putExtra(WebActivity.PARAM_URL, Constants.GOODS_DETAIL + goods.id)
        mContext.startActivity(intent)
    }
}