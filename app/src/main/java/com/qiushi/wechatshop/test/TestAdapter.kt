package com.qiushi.wechatshop.test

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.view.recyclerview.ViewHolder
import com.qiushi.wechatshop.view.recyclerview.adapter.BaseAdapter
import com.qiushi.wechatshop.view.recyclerview.adapter.OnItemLongClickListener

class TestAdapter(context: Context, data: ArrayList<Beauty>)
    : BaseAdapter<Beauty>(context, data, R.layout.item_test) {

    override fun bindData(holder: ViewHolder, data: Beauty, position: Int) {
        ImageHelper.loadImage(mContext, holder.getView(R.id.iv_cover_feed), data.picUrl)
        holder.setText(R.id.tv_title, data.title)

        setOnItemLongClickListener(object : OnItemLongClickListener {
            override fun onItemLongClick(obj: Any?, position: Int): Boolean {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                val contentUrl = Uri.parse((obj as Beauty).url)
                intent.data = contentUrl
                mContext.startActivity(intent)
                return true
            }
        })
    }
}