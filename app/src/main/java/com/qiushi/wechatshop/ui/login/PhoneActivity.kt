package com.qiushi.wechatshop.ui.login

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.wechat.friends.Wechat
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.ui.MainActivity
import com.qiushi.wechatshop.util.CheckPhoneUtil
import com.qiushi.wechatshop.util.Push
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.util.share.Callback
import com.qiushi.wechatshop.util.web.WebActivity
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_phone.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

/**
 * 手机号验证码登录
 */
class PhoneActivity : BaseActivity(), View.OnClickListener {

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
                auth.isEnabled = true
                interval = INTERVAL
                auth.text = "重新发送"
            }
        }
    }

    override fun init() {
        setSwipeBackEnable(false)
        StatusBarUtil.immersive(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        back.setOnClickListener(this)
        auth.setOnClickListener(this)
        login.setOnClickListener(this)
        clear_phone.setOnClickListener(this)
        clear_pass.setOnClickListener(this)
        protocol.setOnClickListener(this)
        phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    setVisability(clear_phone, false)
                    auth.isEnabled = false
                    login.isEnabled = false
                } else {
                    setVisability(clear_phone, true)

                    if (phone.text.length == 11) {
                        auth.isEnabled = true

                        if (password.text.isNotEmpty()) {
                            login.isEnabled = true
                        }
                    } else {
                        auth.isEnabled = false
                        login.isEnabled = false
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    setVisability(clear_pass, false)
                    login.isEnabled = false
                } else {
                    setVisability(clear_pass, true)

                    if (phone.text.length == 11) {
                        login.isEnabled = true
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login()
            }
            false
        }
    }

    private fun setVisability(view: View, visibility: Boolean) {
        when (visibility) {
            true -> {
                if (view.visibility != View.VISIBLE) {
                    view.visibility = View.VISIBLE
                }
            }
            false -> {
                if (view.visibility != View.INVISIBLE) {
                    view.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.auth ->
                if (canSendCode())
                    getAuthCode()
            R.id.login -> login()
            R.id.protocol -> {//H5协议
                val intent = Intent(this, WebActivity::class.java)
                intent.putExtra(WebActivity.PARAM_TITLE, "协议")
                intent.putExtra(WebActivity.PARAM_URL, "http://www.top6000.com")
                startActivity(intent)
            }
            R.id.clear_phone -> {
                phone.setText("")
                phone.requestFocus()
                UIUtil.showKeyboard(this, phone)
            }
            R.id.clear_pass -> {
                password.setText("")
                password.requestFocus()
                UIUtil.showKeyboard(this, password)
            }
        }
    }

    private fun getAuthCode() {
        val phoneNumber = phone.text.toString().trim()
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.showMessage("请填写手机号")
            return
        }
        if (!CheckPhoneUtil.isPhone(phoneNumber)) {
            ToastUtils.showMessage("手机号格式不正确")
            return
        }
        password.setText("")
        auth.isEnabled = false
        val disposable = RetrofitManager.service.sendVerifyCode(phoneNumber)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<String>() {
                    override fun onHandleSuccess(t: String) {
                        ToastUtils.showMessage("发送成功")
                        tHandler.sendEmptyMessage(100)
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showMessage(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    private fun login() {
        val phoneNumber = phone.text.toString().trim()
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.showMessage("请填写手机号")
            return
        }
        if (!CheckPhoneUtil.isPhone(phoneNumber)) {
            ToastUtils.showMessage("手机号格式不正确")
            return
        }
        val pass = password.text.toString().trim()
        if (TextUtils.isEmpty(pass)) {
            ToastUtils.showMessage("请填写验证码")
            return
        }

        UIUtil.hideKeyboard(this)

        val callback: Callback<User> = LoginCallback()
        callback.onStart()
        val disposable = RetrofitManager.service.loginPhone(phoneNumber,
                pass, Push.getDeviceToken(), 1)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<User>() {
                    override fun onHandleSuccess(t: User) {
                        if (t != null && t.bind.isNotEmpty()) {
                            when (t.bind) {
                                "0" -> {
                                    //没绑定,拉起微信绑定
                                    loginWX(t.phone, callback)
                                }
                                "1" -> {
                                    //已经绑定
                                    User.setCurrent(t)
                                    callback.onSuccess(t)
                                }
                            }

                            MobclickAgent.onProfileSignIn(t.id.toString())
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showMessage(error.msg)
                        callback.onFail(error.msg)
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

    internal inner class LoginCallback : Callback.SimpleCallback<User>() {
        override fun onStart() {
            super.onStart()
            showLoading()
        }

        override fun onAfter() {
            super.onAfter()
            dismissLoading()
        }

        override fun onSuccess(user: User) {
            dismissLoading()
            startActivity(Intent(this@PhoneActivity, MainActivity::class.java))
            finish()
        }

        override fun onFail(error: String) {
            dismissLoading()
            ToastUtils.showMessage(error)
        }
    }

    private fun loginWX(phone: String, callback: Callback<User>) {
        val platform = ShareSDK.getPlatform(Wechat.NAME)
        if (platform.isAuthValid) {
            platform.removeAccount(true)
        }
        platform.SSOSetting(false)
        platform.platformActionListener = object : PlatformActionListener {
            override fun onComplete(platform: Platform, i: Int, hashMap: HashMap<String, Any>) {
                val disposable = RetrofitManager.service.loginWX(platform.db.token, platform.db.userId, phone, Push.getDeviceToken(), 1)
                        .compose(SchedulerUtils.ioToMain())
                        .subscribeWith(object : BaseObserver<User>() {
                            override fun onHandleSuccess(t: User) {
                                User.setCurrent(t)
                                callback.onSuccess(t)
                            }

                            override fun onHandleError(error: Error) {
                                callback.onFail(error.msg)
                            }
                        })
                addSubscription(disposable)
            }

            override fun onError(platform: Platform, i: Int, throwable: Throwable) {
                callback.onFail(throwable.message)
            }

            override fun onCancel(platform: Platform, i: Int) {
                callback.onAfter()
            }
        }
        platform.showUser(null)
    }
}
