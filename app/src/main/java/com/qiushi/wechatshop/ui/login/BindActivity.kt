package com.qiushi.wechatshop.ui.login

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
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

    private val INTERVAL = 60//验证码倒计时
    private var interval = INTERVAL

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

    override fun layoutId(): Int = R.layout.activity_bind

    override fun init() {
        StatusBarUtil.immersive(this)
        StatusBarUtil.setPadding(this, toolbar)

        back.setOnClickListener(this)
        auth.setOnClickListener(this)
        bind.setOnClickListener(this)
        clear_phone.setOnClickListener(this)
        clear_pass.setOnClickListener(this)
        phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    setVisability(clear_phone, false)
                    auth.isEnabled = false
                    bind.isEnabled = false
                } else {
                    setVisability(clear_phone, true)

                    if (phone.text.length == 11) {
                        auth.isEnabled = true

                        if (password.text.isNotEmpty()) {
                            bind.isEnabled = true
                        }
                    } else {
                        auth.isEnabled = false
                        bind.isEnabled = false
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
                    bind.isEnabled = false
                } else {
                    setVisability(clear_pass, true)

                    if (phone.text.length == 11) {
                        bind.isEnabled = true
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
                bindPhone()
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
            R.id.auth ->
                if (canSendCode())
                    getAuthCode()
            R.id.bind -> bindPhone()
            R.id.back -> finish()
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
        val disposable = RetrofitManager.service.sendVerifyCode(phone.text.toString().trim())
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

    private fun bindPhone() {
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

        val disposable = RetrofitManager.service.bindPhone(
                phoneNumber, pass)
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