package com.qiushi.wechatshop.ui.manage

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.ShopOrder
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class ManagerAdapter(data: List<ShopOrder>) : BaseQuickAdapter<ShopOrder, BaseViewHolder>(R.layout.manager_item_orther, data) {

    override fun convert(helper: BaseViewHolder, item: ShopOrder) {

        ImageHelper.loadImageWithCorner(mContext, helper.getView(R.id.iv_shop), item.cover, 93, 94,
                RoundedCornersTransformation(DensityUtils.dp2px(10.toFloat()), 0, RoundedCornersTransformation.CornerType.LEFT))

        helper.addOnClickListener(R.id.iv_more)
                .addOnClickListener(R.id.tv_zd)
                .addOnClickListener(R.id.tv_delete)
                .addOnClickListener(R.id.tv_xj)
    }
}