package com.qiushi.wechatshop.ui.login

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.wechat.friends.Wechat
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.ui.MainActivity
import com.qiushi.wechatshop.util.Push
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.util.share.Callback
import com.qiushi.wechatshop.util.web.WebActivity
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_phone.*
import java.util.*
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
        StatusBarUtil.setPaddingSmart(this, toolbar)

        back.setOnClickListener(this)
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
            R.id.back -> finish()
            R.id.auth -> {
                if (canSendCode()) {
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

    private fun login() {
        if (TextUtils.isEmpty(password.text.toString().trim())) {
            ToastUtils.showMessage("请填写验证码")
            return
        }

        if (authCode != password.text.toString().trim()) {
            ToastUtils.showMessage("验证码不正确")
            return
        }
        val callback: Callback<User> = LoginCallback()
        callback.onStart()
        val disposable = RetrofitManager.service.loginPhone(phone.text.toString().trim(),
                password.text.toString().trim(), Push.getDeviceToken(), 1)
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
        val account = phone.text.toString().trim()
        if (TextUtils.isEmpty(account)) {
            ToastUtils.showMessage("请输入手机号")
            return false
        }
        if (!Pattern.matches(Constants.REGEX_MOBILE, phone.text.toString().trim())) {
            ToastUtils.showMessage("手机号输入不正确")
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
//            when (user.bind) {
//                "0" -> {
//                    //bind微信
//                }
//                "1" -> {
//                    dismissLoading()
//                    startActivity(Intent(this@PhoneActivity, MainActivity::class.java))
//                    finish()
//                }
//            }
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
                val platDB = platform.db
//                val params = HashMap<String, String>()
//                params["push"] = Push.getDeviceToken()
//                params["token"] = platDB.token
////                params["username"] = platDB.userName
//                params["uid"] = platDB.userId
//                params["brand"] = "2"
//                params["type"] = "weixin"

                val disposable = RetrofitManager.service.loginWX(platDB.token, platDB.userId, phone, Push.getDeviceToken(), 1)
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
