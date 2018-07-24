package com.qiushi.wechatshop.ui.manage

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.MenuInfo

class ToolsAlwayAdapter : BaseQuickAdapter<MenuInfo, BaseViewHolder>(R.layout.tools_item_gride_remove) {

    var isEdit: Boolean = false
    var isOnclick = true

    override fun convert(helper: BaseViewHolder, item: MenuInfo) {
        helper.setText(R.id.item_name, item.menu_name)
        if (item.is_mark.isNotEmpty() && item.is_mark == "0") {
            if (isEdit) {
                helper.setBackgroundRes(R.id.item_name, R.drawable.tools_shape)
                helper.setVisible(R.id.iv_remove, true)
                helper.getView<ImageView>(R.id.iv_remove).isEnabled = isOnclick
                helper.addOnClickListener(R.id.iv_remove)
            } else {
                helper.setBackgroundRes(R.id.item_name, 0)
                helper.setVisible(R.id.iv_remove, false)
            }
        } else {
            helper.setBackgroundRes(R.id.item_name, 0)
            helper.setVisible(R.id.iv_remove, false)
        }
    }

    fun setBackground(isEdit: Boolean) {
        this.isEdit = isEdit
        notifyDataSetChanged()
    }

    fun setIsOnclick(isOnclick: Boolean) {
        //点击删除后 应该在界面刷新的时候才能删除
        this.isOnclick = isOnclick
        notifyDataSetChanged()
    }
}