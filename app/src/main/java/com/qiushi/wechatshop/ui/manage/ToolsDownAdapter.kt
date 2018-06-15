package com.qiushi.wechatshop.ui.manage

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.MenuInfo
import com.qiushi.wechatshop.model.Tools

class ToolsDownAdapter(data: List<MenuInfo>) : BaseQuickAdapter<MenuInfo, BaseViewHolder>(R.layout.tools_item_gride, data) {
    var isEdit: Boolean = false
    override fun convert(helper: BaseViewHolder, item: MenuInfo) {
        helper.setText(R.id.item_name, item.menu_name)
        if(isEdit){
            helper.setBackgroundRes(R.id.item_name, R.drawable.tools_shape)
            helper.setVisible(R.id.iv_add,true)
        }else{
            helper.setBackgroundRes(R.id.item_name, 0)
            helper.setVisible(R.id.iv_add,false)
        }
    }


    fun setBackgroud(isEdit: Boolean) {
        this.isEdit = isEdit
        notifyDataSetChanged()
    }
}