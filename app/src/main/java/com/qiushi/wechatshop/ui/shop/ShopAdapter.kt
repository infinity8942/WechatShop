package com.qiushi.wechatshop.ui.shop

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * Created by Rylynn on 2018-06-08.
 *
 * 串门店铺详情Adapter
 */
class ShopAdapter : BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_shop_goods, null) {
    override fun convert(helper: BaseViewHolder, item: Goods?) {
        ImageHelper.loadImageWithCorner(mContext, helper.getView(R.id.iv_cover_feed), Constants.IMAGE1, 150, 150,
                RoundedCornersTransformation(DensityUtils.dp2px(10f), 0, RoundedCornersTransformation.CornerType.TOP))
        helper.setText(R.id.tv_title, "尼萌特别推出最好吃的蛋糕" + helper.adapterPosition)
    }
}