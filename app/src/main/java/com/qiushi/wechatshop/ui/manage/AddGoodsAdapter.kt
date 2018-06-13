package com.qiushi.wechatshop.ui.manage

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.ShopOrder
import com.qiushi.wechatshop.util.ImageHelper

class AddGoodsAdapter(data: List<ShopOrder>) : BaseQuickAdapter<ShopOrder, BaseViewHolder>(R.layout.addgoods_item, data) {
    override fun convert(helper: BaseViewHolder?, item: ShopOrder?) {
        when (item!!.id) {
            0 -> {
                helper?.getView<TextView>(R.id.tv_text)?.visibility = View.GONE
                helper?.getView<ImageView>(R.id.iv_img)?.visibility = View.VISIBLE
                ImageHelper.loadImage1(mContext, helper?.getView(R.id.iv_img)!!, item.cover, 327, 327, 10f)
            }
            1 -> {
                helper?.getView<TextView>(R.id.tv_text)?.visibility = View.VISIBLE
                helper?.getView<ImageView>(R.id.iv_img)?.visibility = View.GONE
                helper?.setText(R.id.tv_text,item.name)
            }
        }
    }
}