package com.qiushi.wechatshop.ui.shop

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.RelativeLayout
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.test.Beauty
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.StatusBarUtil
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
    : BaseAdapter<Beauty>(context, data, object : MultipleType<Beauty> {

    override fun getLayoutId(item: Beauty, position: Int): Int {
        return when (position) {
            0 -> R.layout.item_shop_header
            else -> R.layout.item_shop_goods
        }
    }
}) {
    override fun bindData(holder: ViewHolder, data: Beauty, position: Int) {
        when (position) {
            0 -> setHeaderData(data, holder)
            else -> setItemData(data, holder)
        }
    }

    private fun setHeaderData(data: Beauty, holder: ViewHolder) {
        val lpCover = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                (DensityUtils.getScreenWidth() * 0.48).toInt())
        holder.getView<ImageView>(R.id.cover).layoutParams = lpCover
        holder.getView<ImageView>(R.id.mask).layoutParams = lpCover
        val layoutParams: RelativeLayout.LayoutParams = holder.getView<RelativeLayout>(R.id.layout_shop_info).layoutParams as RelativeLayout.LayoutParams
        layoutParams.setMargins(DensityUtils.dp2px(16.toFloat()),
                StatusBarUtil.getStatusBarHeight(mContext) + mContext.resources.getDimension(R.dimen.padding_tab_layout_top).toInt(),
                DensityUtils.dp2px(16.toFloat()),
                DensityUtils.dp2px(20.toFloat())
        )

        ImageHelper.loadImage(mContext, holder.getView(R.id.cover), "https://static.chiphell.com/portal/201803/08/190549vw5xfonuw4wzfuxu.jpg")
        ImageHelper.loadImage(mContext, holder.getView(R.id.logo), "https://static.chiphell.com/forum/201806/06/111637jjxw8cwnj88xss8k.jpg")
    }

    private fun setItemData(data: Beauty, holder: ViewHolder) {
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