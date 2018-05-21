package com.qiushi.wechatshop.ui

import android.content.Intent
import android.os.Bundle
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.WActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : WActivity() {
    override fun layoutId(): Int {
        return R.layout.activity_login
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        login.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
