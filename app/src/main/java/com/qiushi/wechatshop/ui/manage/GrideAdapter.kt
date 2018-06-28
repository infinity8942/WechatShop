package com.qiushi.wechatshop.ui.manage

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Function

class GrideAdapter(data: List<Function>) : BaseQuickAdapter<Function, BaseViewHolder>(R.layout.manager_item_gride, data) {
    override fun convert(helper: BaseViewHolder, item: Function?) {
        helper.setText(R.id.item_name, item?.menu_name)
        helper.addOnClickListener(R.id.item_name)
        if (item!!.menu_id != null && item.menu_id == 1) {
            if (item.msg_count != null && item.msg_count > 0) {
                helper.setText(R.id.tv_count, item.msg_count.toString())
                helper.setVisible(R.id.tv_count, true)
            } else {
                helper.setVisible(R.id.tv_count, false)
            }
        } else {
            helper.setVisible(R.id.tv_count, false)
        }
    }
}