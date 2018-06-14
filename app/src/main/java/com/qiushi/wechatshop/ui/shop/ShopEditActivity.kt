package com.qiushi.wechatshop.ui.shop

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_shop_list.*

/**
 * Created by Rylynn on 2018-06-13.
 */
class ShopEditActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_edit)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        //状态栏透明和间距处理
        StatusBarUtil.darkMode(this, android.R.color.white, 1.toFloat())
        StatusBarUtil.setPaddingSmart(this, toolbar)
    }

}
