package com.qiushi.wechatshop.ui.order

import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_add_order.*

/**
 * 添加订单
 */
class AddOrderActivity : BaseActivity() {

    override fun layoutId(): Int = R.layout.activity_add_order

    override fun init() {
        StatusBarUtil.immersive(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        back.setOnClickListener(clickListener)
        add.setOnClickListener(clickListener)
    }

    override fun getData() {
    }

    private val clickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.back -> finish()
            R.id.add -> addOrder()
        }
    }

    private fun addOrder() {//TODO 添加订单接口

    }
}
