package com.qiushi.wechatshop.ui.shop

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.test.Beauty
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.view.recyclerview.MultipleType
import com.qiushi.wechatshop.view.recyclerview.ViewHolder
import com.qiushi.wechatshop.view.recyclerview.adapter.BaseAdapter
import com.qiushi.wechatshop.view.recyclerview.adapter.OnItemLongClickListener

/**
 * Created by Rylynn on 2018-06-08.
 *
 * 串门店铺Adapter
 */
class ShopAdapter(context: Context, data: ArrayList<Beauty>)
    : BaseAdapter<Beauty>(context, data ,object : MultipleType<Beauty>{
    override fun getLayoutId(position: Int): Int {
        return when (position) {
            0 -> R.layout.item_shop_header
            else -> R.layout.item_shop_goods
        }
    }

}) {
    override fun bindData(holder: ViewHolder, data: Beauty, position: Int) {
        when (position) {
            0 -> setHeaderData(data, holder)
            else -> setItemData(data, holder,position)
        }
    }

    private fun setHeaderData(data: Beauty, holder: ViewHolder) {
    }


    private fun setItemData(data: Beauty, holder: ViewHolder, position: Int) {
        bindData(holder,data,position)
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