package com.qiushi.wechatshop.ui.moments

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jaeger.ninegridimageview.NineGridImageView
import com.jaeger.ninegridimageview.NineGridImageViewAdapter
import com.previewlibrary.GPreviewBuilder
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Moment
import com.qiushi.wechatshop.model.NineImage
import com.qiushi.wechatshop.util.ImageHelper

/**
 * Created by Rylynn on 2018-06-14.
 *
 * 素材Adapter
 */
class MomentsAdapter : BaseQuickAdapter<Moment, BaseViewHolder>(R.layout.item_moment, null) {
    override fun convert(helper: BaseViewHolder, moment: Moment) {
        ImageHelper.loadAvatar(mContext, helper.getView(R.id.logo), Constants.AVATAR, 42)
        helper.setText(R.id.name, "咪蒙韩国代购" + helper.adapterPosition)
        helper.setText(R.id.content, "YSL圣罗兰纯口红方管豆沙色梅子姨妈色正红色1橘色13裸色官方正品")

        val nineGrid = helper.getView<NineGridImageView<NineImage>>(R.id.nine_grid)
        nineGrid.setAdapter(object : NineGridImageViewAdapter<NineImage>() {
            override fun onDisplayImage(context: Context, imageView: ImageView, t: NineImage) {
                ImageHelper.loadImage(context, imageView, t.img_url)
            }
        })
        nineGrid.setImagesData(moment.images)

        nineGrid.setItemImageClickListener { context, _, index, list ->
            computeBoundsBackward(nineGrid, list)//组成数据
            GPreviewBuilder.from(context as Activity)
                    .setData(list)
                    .setCurrentIndex(index)
                    .setType(GPreviewBuilder.IndicatorType.Dot)
                    .start()
        }
    }

    /**
     * 查找信息
     * @param list 图片集合
     */
    private fun computeBoundsBackward(view: NineGridImageView<NineImage>, list: List<NineImage>) {
        for (i in 0 until view.childCount) {
            val itemView = view.getChildAt(i)
            val bounds = Rect()
            if (itemView != null) {
                val thumbView = itemView as ImageView
                thumbView.getGlobalVisibleRect(bounds)
            }
            list[i].mBounds = bounds
            list[i].img_url = list[i].url
        }
    }
}