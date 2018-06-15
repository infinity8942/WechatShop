package com.qiushi.wechatshop.ui.manage

import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_todo.*

/**
 * Created by Rylynn on 2018-06-15.
 *
 * 待办事项
 */
class TodoActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int {
        return R.layout.activity_todo
    }

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        back.setOnClickListener(this)
        layout_deliver.setOnClickListener(this)
        layout_pay.setOnClickListener(this)
        layout_amount.setOnClickListener(this)
        layout_chat.setOnClickListener(this)
        layout_date.setOnClickListener(this)
        layout_coupon_amount.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.layout_deliver -> {//待发货

            }
            R.id.layout_pay -> {//待支付

            }
            R.id.layout_amount -> {//库存量紧缺

            }
            R.id.layout_chat -> {//需沟通人员

            }
            R.id.layout_date -> {//优惠券即将过期

            }
            R.id.layout_coupon_amount -> {//优惠券剩余数量
            }
            R.id.back -> finish()
        }
    }

    override fun getData() {
    }
}