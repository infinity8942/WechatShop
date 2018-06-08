package com.qiushi.wechatshop.ui.shop

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.RelativeLayout
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.model.Shop
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
class ShopAdapter(context: Context, data: ArrayList<Goods>, shop: Shop)
    : BaseAdapter<Goods>(context, data, shop, object : MultipleType<Goods> {

    override fun getLayoutId(item: Goods, position: Int): Int {
        return when (position) {
            0 -> R.layout.item_shop_header
            else -> R.layout.item_shop_goods
        }
    }
}) {
    override fun bindHeaderData(holder: ViewHolder, data: Any?, position: Int) {
        setHeaderData(holder, data as Shop)
    }

    override fun bindData(holder: ViewHolder, data: Goods?, position: Int) {
        setItemData(data, holder)
    }

    private fun setHeaderData(holder: ViewHolder, header: Shop) {
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
        ImageHelper.loadAvatar(mContext, holder.getView(R.id.logo), "https://static.chiphell.com/forum/201806/06/111637jjxw8cwnj88xss8k.jpg", 48)
    }

    private fun setItemData(data: Goods?, holder: ViewHolder) {
        ImageHelper.loadImage(mContext, holder.getView(R.id.iv_cover_feed), Constants.IMAGE1)
        holder.setText(R.id.tv_title, "商品" + holder.adapterPosition)

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