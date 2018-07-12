package com.qiushi.wechatshop.ui.shop

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Coupon

/**
 * Created by Rylynn on 2018-06-11.
 *
 * 串门店铺优惠券Adapter
 */
class ShopCouponAdapter : BaseQuickAdapter<Coupon, BaseViewHolder>(R.layout.item_shop_coupon) {
    override fun convert(helper: BaseViewHolder, item: Coupon?) {
        helper.setText(R.id.offset, "￥50")
        helper.setText(R.id.requirement, "指定商品满50.00使用")
        helper.setText(R.id.date, "2018.06.05-2018.11.05")
    }
}