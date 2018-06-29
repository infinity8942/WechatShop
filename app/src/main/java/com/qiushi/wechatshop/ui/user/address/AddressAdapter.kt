package com.qiushi.wechatshop.ui.user.address

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Buyer

/**
 * Created by Rylynn on 2018-06-29.
 *
 * 地址Adapter
 */
class AddressAdapter : BaseQuickAdapter<Buyer, BaseViewHolder>(R.layout.item_address, null) {
    override fun convert(helper: BaseViewHolder, address: Buyer) {
        helper.setText(R.id.name, address.receiver + "  " + address.mobile)
        if (address.is_default == 1)
            helper.setGone(R.id.default_address, true)
        else
            helper.setGone(R.id.default_address, false)
        helper.setText(R.id.address, address.address)
        helper.addOnClickListener(R.id.edit)
    }
}