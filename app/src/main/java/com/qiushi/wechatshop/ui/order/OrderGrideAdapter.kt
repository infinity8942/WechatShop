package com.qiushi.wechatshop.ui.order

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.model.ShopOrder
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class OrderGrideAdapter(data: List<Goods>) : BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.orderlist_item, data) {
    override fun convert(helper: BaseViewHolder?, item: Goods?) {
        ImageHelper.loadImageWithCorner(mContext, helper?.getView(R.id.img)!!, item?.cover!!, 167, 167,
                RoundedCornersTransformation(DensityUtils.dp2px(10.toFloat()), 0, RoundedCornersTransformation.CornerType.ALL))
    }
}