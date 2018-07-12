package com.qiushi.wechatshop.ui.manage

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Entrance
import com.qiushi.wechatshop.util.ImageHelper

class EntranceAdapter(data: List<Entrance>) : BaseQuickAdapter<Entrance, BaseViewHolder>(R.layout.manager_item_gride, data) {
    @SuppressLint("CheckResult")
    override fun convert(helper: BaseViewHolder, item: Entrance) {

        ImageHelper.loadAvatar(mContext, helper.getView(R.id.icon), item.coin_url, 40)
        helper.setText(R.id.item_name, item.menu_name)

        //dots
        if (null != item.menu_id && item.menu_id == 1) {
            if (null != item.msg_count && item.msg_count > 0) {
                helper.setVisible(R.id.tv_count, true).setText(R.id.tv_count, item.msg_count.toString())
            } else {
                helper.setVisible(R.id.tv_count, false)
            }
        } else {
            helper.setVisible(R.id.tv_count, false)
        }
    }
}