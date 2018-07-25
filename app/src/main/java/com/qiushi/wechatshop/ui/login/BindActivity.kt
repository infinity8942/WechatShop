package com.qiushi.wechatshop.ui.login

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.ui.manage.DecorateActivity
import com.qiushi.wechatshop.util.CheckPhoneUtil
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_bind.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

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
        StatusBarUtil.setPadding(this, toolbar)

        back.setOnClickListener(this)
        auth.setOnClickListener(this)
        bind.setOnClickListener(this)
        password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                UIUtil.hideKeyboard(this)
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
            R.id.bind -> bindPhone()
            R.id.back -> finish()
        }
    }

    private fun getAuthCode() {
        if (TextUtils.isEmpty(phone.text.toString().trim())) {
            ToastUtils.showMessage("请填写手机号")
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
                        ToastUtils.showMessage(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    private fun bindPhone() {
        if (TextUtils.isEmpty(password.text.toString().trim())) {
            ToastUtils.showMessage("请填写验证码")
            return
        }

        val disposable = RetrofitManager.service.bindPhone(phone.text.toString().trim(),
                password.text.toString().trim())
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<String>() {
                    override fun onHandleSuccess(t: String) {
                        if (t.isNotEmpty()) {
                            ToastUtils.showMessage("绑定成功")
                            User.editCurrent { u ->
                                u!!.phone = t
                            }
                            DecorateActivity.startDecorateActivity(this@BindActivity, "", "", "")
                            finish()
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showMessage(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    /**
     * 是否可发送验证码
     */
    private fun canSendCode(): Boolean {
        if (interval != INTERVAL) {
            ToastUtils.showMessage(String.format("%sS后可重新发送验证码", interval))
            return false
        }
        val phoneNumber = phone.text.toString().trim()
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.showMessage("请输入手机号")
            return false
        }
        if (!CheckPhoneUtil.isPhone(phoneNumber)) {
            ToastUtils.showMessage("手机号格式不正确")
            return false
        }
        return true
    }
}