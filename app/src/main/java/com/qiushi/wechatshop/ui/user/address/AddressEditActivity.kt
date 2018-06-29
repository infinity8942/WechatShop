package com.qiushi.wechatshop.ui.user.address

import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_about_us.*

/**
 * Created by Rylynn on 2018-06-29.
 *
 * 编辑地址
 */
class AddressEditActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int = R.layout.activity_address_edit

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        back.setOnClickListener(this)
    }

    override fun getData() {
    }
}