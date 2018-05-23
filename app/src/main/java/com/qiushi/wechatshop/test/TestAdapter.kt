package com.qiushi.wechatshop.test

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.view.recyclerview.ViewHolder
import com.qiushi.wechatshop.view.recyclerview.adapter.CommonAdapter

class TestAdapter(context: Context, data: ArrayList<Beauty>)
    : CommonAdapter<Beauty>(context, data, R.layout.item_test) {

    override fun bindData(holder: ViewHolder, data: Beauty, position: Int) {
        Glide.with(mContext)
                .load(data.picUrl)
                .transition(DrawableTransitionOptions().crossFade())
                .into(holder.getView(R.id.iv_cover_feed))
        holder.setText(R.id.tv_title, data.title)
    }
}