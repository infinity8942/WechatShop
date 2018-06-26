package com.qiushi.wechatshop.ui.pay

import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_pay_result.*

/**
 * 支付结果页
 */
class PayResultActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int = R.layout.activity_pay_result

    override fun init() {
        StatusBarUtil.immersive(this)

        back.setOnClickListener(this)
    }

    override fun getData() {
    }

}
