package com.qiushi.wechatshop.ui.login

import android.content.Intent
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
import com.qiushi.wechatshop.ui.MainActivity
import com.qiushi.wechatshop.util.Push
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_phone_edit.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

/**
 * 修改手机号
 */
class EditPhoneActivity : BaseActivity(), View.OnClickListener {

    private var authCode = "" //验证码

    override fun layoutId(): Int = R.layout.activity_phone_edit

    override fun init() {
        setSwipeBackEnable(false)
        StatusBarUtil.immersive(this)

        auth.setOnClickListener(this)
        login.setOnClickListener(this)
        protocol.setOnClickListener(this)
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
            R.id.auth -> getAuthCode()
            R.id.login -> loginPhone()
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
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    private fun loginPhone() {
        if (TextUtils.isEmpty(password.text.toString().trim())) {
            ToastUtils.showWarning("请填写验证码")
            return
        }

        //TODO 修改手机号接口
        val disposable = RetrofitManager.service.loginPhone(phone.text.toString().trim(),
                password.text.toString().trim(),Push.getDeviceToken(),1)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<User>() {
                    override fun onHandleSuccess(t: User) {
                        if (t != null) {
                            startActivity(Intent(this@EditPhoneActivity, MainActivity::class.java))
                            finish()
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
    }
}