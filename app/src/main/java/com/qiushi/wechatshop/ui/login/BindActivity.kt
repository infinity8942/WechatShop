package com.qiushi.wechatshop.ui.login

import android.os.Handler
import android.os.Looper
import android.os.Looper.*
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.R.id.auth
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_bind.*

import java.util.regex.Pattern

/**
 * 手机号绑定
 */
class BindActivity : BaseActivity(), View.OnClickListener {

    private var authCode = "" //验证码
    private val INTERVAL = 60//验证码倒计时
    private var interval = INTERVAL
    override fun layoutId(): Int = R.layout.activity_bind

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
        StatusBarUtil.immersive(this)

        back.setOnClickListener(this)
        auth.setOnClickListener(this)
        bind.setOnClickListener(this)
        password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                bind()
            }
            false
        }
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.auth -> {
                if (canSendCode()) {
                    getAuthCode()
                }
            }
            R.id.bind -> bind()
            R.id.back -> finish()
        }
    }

    private fun getAuthCode() {
        if (TextUtils.isEmpty(phone.text.toString().trim())) {
            ToastUtils.showWarning("请填写手机号")
            return
        }

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

    private fun bind() {
        if (TextUtils.isEmpty(password.text.toString().trim())) {
            ToastUtils.showWarning("请填写验证码")
            return
        }

        if (authCode != password.text.toString().trim()) {
            ToastUtils.showWarning("验证码不正确")
            return
        }

        val disposable = RetrofitManager.service.bindPhone(phone.text.toString().trim(),
                password.text.toString().trim())
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        if (t) {
                            ToastUtils.showMessage("绑定成功")
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