package com.qiushi.wechatshop.ui.manage

import android.support.v4.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.PriceUtil
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class ManagerAdapter(data: List<Goods>) : BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.manager_item_orther, data) {

    override fun convert(helper: BaseViewHolder, item: Goods) {

        ImageHelper.loadImageWithCorner(mContext, helper.getView(R.id.iv_shop), item.cover, 93, 94,
                RoundedCornersTransformation(DensityUtils.dp2px(10.toFloat()), 0, RoundedCornersTransformation.CornerType.LEFT))
        helper.setText(R.id.tv_shop_name, item.name)
        helper.setText(R.id.stock, item.stock.toString() + "库存")
        if (item.stock > item.limit_num) {
            helper.setTextColor(R.id.stock, ContextCompat.getColor(mContext, R.color.stock_blue))
            helper.setBackgroundRes(R.id.stock, R.drawable.bg_goods_stock_enough)
        } else {
            helper.setTextColor(R.id.stock, ContextCompat.getColor(mContext, R.color.stock_red))
            helper.setBackgroundRes(R.id.stock, R.drawable.bg_goods_stock_short)
        }
        helper.setText(R.id.views, item.views.toString() + "人已浏览")
        helper.setText(R.id.money, "¥ " + PriceUtil.doubleTrans(item.price))
        if (item.is_top) {
            helper.setText(R.id.tv_zd, "取消")
        } else {
            helper.setText(R.id.tv_zd, "置顶")
        }
        if (item.enable) {
            helper.setText(R.id.tv_xj, "下架")
        } else {
            helper.setText(R.id.tv_xj, "上架")
        }

        helper.addOnClickListener(R.id.iv_more).addOnClickListener(R.id.tv_zd)
                .addOnClickListener(R.id.tv_delete).addOnClickListener(R.id.tv_xj)
                .addOnClickListener(R.id.iv_edit)
    }
}