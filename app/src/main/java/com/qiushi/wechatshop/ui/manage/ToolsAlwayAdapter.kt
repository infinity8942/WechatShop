package com.qiushi.wechatshop.ui.manage

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Tools

class ToolsAlwayAdapter(data: List<Tools>) : BaseQuickAdapter<Tools, BaseViewHolder>(R.layout.tools_item_gride, data) {
    override fun convert(helper: BaseViewHolder, item: Tools) {
        helper.setText(R.id.item_name, item.name)
    }
}