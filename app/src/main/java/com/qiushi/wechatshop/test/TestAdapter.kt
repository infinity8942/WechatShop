package com.qiushi.wechatshop.test

import android.content.Context
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.view.recyclerview.ViewHolder
import com.qiushi.wechatshop.view.recyclerview.adapter.CommonAdapter

class TestAdapter(context: Context, data: ArrayList<Beauty>)
    : CommonAdapter<Beauty>(context, data, R.layout.item_test) {

    override fun bindData(holder: ViewHolder, data: Beauty, position: Int) {
        ImageHelper.loadImage(mContext, holder.getView(R.id.iv_cover_feed), data.picUrl)
        holder.setText(R.id.tv_title, data.title)
    }
}