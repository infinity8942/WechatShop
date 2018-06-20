package com.qiushi.wechatshop.ui.manage

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.model.Shop
import com.qiushi.wechatshop.model.ShopOrder
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class ManagerGoodsAdapter(data: List<Goods>) : BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.manager_item_orther, data) {
    override fun convert(helper: BaseViewHolder?, item: Goods?) {
        val view = helper?.getView<ImageView>(R.id.iv_shop)

        ImageHelper.loadImageWithCorner(mContext, view!!, item?.cover!!, 93, 94,
                RoundedCornersTransformation(DensityUtils.dp2px(10.toFloat()), 0, RoundedCornersTransformation.CornerType.LEFT))
        helper.setText(R.id.tv_shop_name, item.name)
        helper.setText(R.id.views, item.views.toString() + "人已经浏览")
        helper.setText(R.id.money, "¥ " + item.price)

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
        helper!!.addOnClickListener(R.id.iv_more)
                .addOnClickListener(R.id.tv_zd)
                .addOnClickListener(R.id.tv_delete)
                .addOnClickListener(R.id.tv_xj)
    }
}