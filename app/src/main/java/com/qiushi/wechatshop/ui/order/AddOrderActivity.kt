package com.qiushi.wechatshop.ui.order

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_add_order.*


class AddOrderActivity : BaseActivity() {
    override fun init() {
        StatusBarUtil.immersive(this!!)
        StatusBarUtil.setPaddingSmart(this!!, toolbar)
    }

    override fun getData() {

    }

    override fun layoutId(): Int = R.layout.activity_add_order


    companion object {
        fun startAddOrderActivity(context: Activity) {
            var intent = Intent()
            intent.setClass(context, AddOrderActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
        }
    }
}
