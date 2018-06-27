package com.qiushi.wechatshop.ui.login

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.ui.MainActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.util.web.WebActivity

import kotlinx.android.synthetic.main.activity_phone.*
import java.util.regex.Pattern

/**
 * 手机号验证码登录
 */
class PhoneActivity : BaseActivity(), View.OnClickListener {

    private var authCode = "" //验证码
    private val INTERVAL = 60//验证码倒计时
    private var interval = INTERVAL
    override fun layoutId(): Int = R.layout.activity_phone
    val tHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (interval > 0) {
                interval--
                auth.text = String.format("%sS", interval)
                sendEmptyMessageDelayed(100, 1000)
            } else {
                interval = INTERVAL
                auth.text = "重新发送"
            }
        }
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
            R.id.auth -> {
                if (canSendCode()){
                    getAuthCode()
                }
            }
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
        password.setText("")
        val disposable = RetrofitManager.service.sendVerifyCode(phone.text.toString().trim())
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<String>() {
                    override fun onHandleSuccess(t: String) {
                        ToastUtils.showMessage("发送成功")
                        authCode = t
                        tHandler.sendEmptyMessage(100)
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    private fun login() {
        if (TextUtils.isEmpty(password.text.toString().trim())) {
            ToastUtils.showWarning("请填写验证码")
            return
        }

        if (authCode != password.text.toString().trim()) {
            ToastUtils.showWarning("验证码不正确")
            return
        }

        //TODO 手机号登录接口
        val disposable = RetrofitManager.service.loginPhone(phone.text.toString().trim(),
                password.text.toString().trim())
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        if (t) {
                            startActivity(Intent(this@PhoneActivity, MainActivity::class.java))
                            finish()
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    /**
     * 是否可发送验证码
     */
    private fun canSendCode(): Boolean {
        if (interval != INTERVAL) {
            ToastUtils.showWarning(String.format("%sS后可重新发送验证码", interval))
            return false
        }
        val account = phone.text.toString().trim()
        if (TextUtils.isEmpty(account)) {
            ToastUtils.showWarning("请输入手机号")
            return false
        }
        if (!Pattern.matches(Constants.REGEX_MOBILE, phone.text.toString().trim())) {
            ToastUtils.showWarning("手机号输入不正确")
            return false
        }
        return true
    }
}
