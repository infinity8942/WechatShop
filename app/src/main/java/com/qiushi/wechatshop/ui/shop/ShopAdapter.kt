package com.qiushi.wechatshop.ui.shop

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.PriceUtil
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * Created by Rylynn on 2018-06-08.
 *
 * 串门店铺详情Adapter
 */
class ShopAdapter : BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_shop_goods) {
    override fun convert(helper: BaseViewHolder, goods: Goods) {

        if (helper.adapterPosition % 2 != 0) {
            helper.itemView.setPadding(DensityUtils.dp2px(9.toFloat()), DensityUtils.dp2px(8.toFloat()),
                    DensityUtils.dp2px(4.5.toFloat()), 0)
        } else {
            helper.itemView.setPadding(DensityUtils.dp2px(4.5.toFloat()), DensityUtils.dp2px(8.toFloat()),
                    DensityUtils.dp2px(9.toFloat()), 0)
        }

        ImageHelper.loadImageWithCorner(mContext, helper.getView(R.id.iv_cover_feed), goods.cover, 150, 150,
                RoundedCornersTransformation(DensityUtils.dp2px(10f), 0, RoundedCornersTransformation.CornerType.TOP))
        helper.setText(R.id.tv_title, goods.name)
        helper.setText(R.id.price, "￥" + PriceUtil.doubleTrans(goods.price))
        helper.setText(R.id.amount, "已售" + goods.sold)

        helper.addOnClickListener(R.id.cart)
    }
}