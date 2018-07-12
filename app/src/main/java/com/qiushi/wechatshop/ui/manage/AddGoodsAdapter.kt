package com.qiushi.wechatshop.ui.manage

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Content
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class AddGoodsAdapter : BaseQuickAdapter<Content, BaseViewHolder>(R.layout.addgoods_item) {
    override fun convert(helper: BaseViewHolder, item: Content?) {

        if (item!!.content.isNotEmpty()) {
            helper.getView<TextView>(R.id.tv_text)?.visibility = View.VISIBLE
            helper.getView<ImageView>(R.id.iv_img)?.visibility = View.GONE
            helper.setText(R.id.tv_text, item.content)
            helper.addOnClickListener(R.id.iv_remove)
        } else {
            helper.getView<TextView>(R.id.tv_text)?.visibility = View.GONE
            helper.getView<ImageView>(R.id.iv_img)?.visibility = View.VISIBLE
            ImageHelper.loadImageWithCorner(mContext, helper.getView(R.id.iv_img)!!, item.img, 327, 327,
                    RoundedCornersTransformation(DensityUtils.dp2px(10.toFloat()), 0, RoundedCornersTransformation.CornerType.LEFT))
            helper.addOnClickListener(R.id.iv_remove)
        }
    }
}