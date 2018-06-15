package com.qiushi.wechatshop.ui.login

import android.content.Intent
import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.ui.MainActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_login.*

/**
 * 手机号绑定
 */
class BindActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int {
        return R.layout.activity_login
    }

    override fun init() {
        setSwipeBackEnable(false)
        StatusBarUtil.immersive(this)

        login.setOnClickListener(this)
        login.setOnClickListener(this)
        protocol.setOnClickListener(this)
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.login -> {//获取验证码
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            R.id.login -> {//登录

            }
            R.id.protocol -> {//H5协议

            }
        }
    }
}
