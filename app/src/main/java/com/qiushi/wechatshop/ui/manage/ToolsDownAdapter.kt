package com.qiushi.wechatshop.ui.manage

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.MenuInfo

class ToolsDownAdapter(data: List<MenuInfo>) : BaseQuickAdapter<MenuInfo, BaseViewHolder>(R.layout.tools_item_gride, data) {

    private var isEdit: Boolean = false
    private var isOnclick = true

    override fun convert(helper: BaseViewHolder, item: MenuInfo) {
        helper.setText(R.id.item_name, item.menu_name)
        if (isEdit) {
            helper.setBackgroundRes(R.id.item_name, R.drawable.tools_shape)
            helper.setVisible(R.id.iv_add, true)

            helper.getView<ImageView>(R.id.iv_add).isEnabled = isOnclick
            helper.addOnClickListener(R.id.iv_add)
        } else {
            helper.setBackgroundRes(R.id.item_name, 0)
            helper.setVisible(R.id.iv_add, false)
        }
    }

    fun setBackgroud(isEdit: Boolean) {
        this.isEdit = isEdit
        notifyDataSetChanged()
    }

    fun setIsOnclick(isOnclick: Boolean) {
        //点击删除后 应该在界面刷新的时候才能删除
        this.isOnclick = isOnclick
        notifyDataSetChanged()
    }
}