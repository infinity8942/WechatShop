package com.qiushi.wechatshop.ui.manage


import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.ShopOrder
import com.qiushi.wechatshop.util.ImageHelper

class ManagerAdapter(data: List<ShopOrder>) : BaseQuickAdapter<ShopOrder, BaseViewHolder>(R.layout.manager_item_orther, data) {
    override fun convert(helper: BaseViewHolder?, item: ShopOrder?) {
        val view = helper?.getView<ImageView>(R.id.iv_shop)
        ImageHelper.loadImage1(mContext, view!!, item?.cover!!, 93, 94, 10f)
        helper.addOnClickListener(R.id.iv_more)
                .addOnClickListener(R.id.tv_zd)
                .addOnClickListener(R.id.tv_delete)
                .addOnClickListener(R.id.tv_xj)
    }


}
