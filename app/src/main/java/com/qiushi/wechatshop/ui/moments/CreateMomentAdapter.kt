package com.qiushi.wechatshop.ui.moments

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.R.id.iv_add
import com.qiushi.wechatshop.model.NineImage
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class CreateMomentAdapter(data: List<NineImage>) : BaseMultiItemQuickAdapter<NineImage, BaseViewHolder>(data) {
    override fun convert(helper: BaseViewHolder?, item: NineImage?) {
        when (helper!!.itemViewType) {
            -1 -> helper.addOnClickListener(R.id.foot)
            1 -> {
                ImageHelper.loadImageWithCorner(mContext, helper.getView(iv_add), item!!.url, 100, 100,
                        RoundedCornersTransformation(DensityUtils.dp2px(1.toFloat()), 0, RoundedCornersTransformation.CornerType.ALL))
                helper.addOnClickListener(R.id.iv_remove)
            }
        }
    }

    init {
        addItemType(-1, R.layout.add_moments_layout)
        addItemType(1, R.layout.add_moments_img_layout)
    }
}