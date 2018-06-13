package com.qiushi.wechatshop.ui.manage

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Function

class GrideAdapter(data: List<Function>) : BaseQuickAdapter<Function, BaseViewHolder>(R.layout.manager_item_gride, data) {
    override fun convert(helper: BaseViewHolder, item: Function?) {
        helper.setText(R.id.item_name, item?.name)
        helper.addOnClickListener(R.id.item_name)
    }
}