package com.qiushi.wechatshop.ui.manage


import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
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
        Log.e("tag","isCheck"+item.isCheck)
//        if (item.isCheck) {
//            helper.getView<RelativeLayout>(R.id.layout_shape).visibility = View.VISIBLE
//        } else {
//            helper.getView<RelativeLayout>(R.id.layout_shape).visibility = View.GONE
//        }
    }


}
