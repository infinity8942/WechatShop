package com.qiushi.wechatshop.ui.shop

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.util.ImageHelper

/**
 * Created by Rylynn on 2018-06-08.
 *
 * 串门店铺详情Adapter
 */
class ShopAdapter : BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_shop_goods, null) {
    override fun convert(helper: BaseViewHolder, item: Goods?) {
        ImageHelper.loadImage(mContext, helper.getView(R.id.iv_cover_feed), Constants.IMAGE1)
        helper.setText(R.id.tv_title, "商品" + helper.adapterPosition)
    }
}