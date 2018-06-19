package com.qiushi.wechatshop.ui.login

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.util.web.WebActivity
import kotlinx.android.synthetic.main.activity_phone.*

/**
 * 手机号验证码登录
 */
class PhoneActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int {
        return R.layout.activity_phone
    }

    override fun init() {
        setSwipeBackEnable(false)
        StatusBarUtil.immersive(this)

        auth.setOnClickListener(this)
        login.setOnClickListener(this)
        protocol.setOnClickListener(this)
        password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login()
            }
            false
        }
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.auth -> getAuthCode()
            R.id.login -> login()
            R.id.protocol -> {//H5协议
                val intent = Intent(this, WebActivity::class.java)
                intent.putExtra(WebActivity.PARAM_TITLE, "协议")
                intent.putExtra(WebActivity.PARAM_URL, "http://www.top6000.com")
                startActivity(intent)
            }
        }
    }

    private fun getAuthCode() {
        if (TextUtils.isEmpty(phone.text.toString().trim())) {
            ToastUtils.showWarning("请填写手机号")
            return
        }
    }

    private fun login() {
        if (TextUtils.isEmpty(password.text.toString().trim())) {
            ToastUtils.showWarning("请填写验证码")
            return
        }

        //TODO 手机号登录接口
    }
}
