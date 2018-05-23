package com.qiushi.wechatshop.ui

import android.content.Intent
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    override fun layoutId(): Int {
        return R.layout.activity_login
    }

    override fun init() {
        login.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun getData() {
    }
}
