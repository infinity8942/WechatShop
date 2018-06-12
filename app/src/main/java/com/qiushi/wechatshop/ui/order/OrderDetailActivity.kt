package com.qiushi.wechatshop.ui.order

import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_order.*

/**
 * Created by Rylynn on 2018-06-12.
 *
 * 订单详情
 */
class OrderDetailActivity : BaseActivity() {

    private var orderID: Long = 0

    override fun layoutId(): Int {
        return R.layout.activity_order_detail
    }

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

    }

    override fun getData() {
    }
}