package com.qiushi.wechatshop.ui.login

import android.Manifest
import android.content.Intent
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.wechat.friends.Wechat
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Notifycation
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.ui.MainActivity
import com.qiushi.wechatshop.util.Push
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.util.permission.HiPermission
import com.qiushi.wechatshop.util.permission.PermissionCallback
import com.qiushi.wechatshop.util.permission.PermissionItem
import com.qiushi.wechatshop.util.share.Callback
import com.qiushi.wechatshop.util.web.WebActivity
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_login.*
import java.util.HashMap
import kotlin.collections.ArrayList

/**
 * 微信登录
 */
class LoginActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int = R.layout.activity_login

    override fun init() {
        setSwipeBackEnable(false)
        StatusBarUtil.immersive(this)

        getPermission()

        login.setOnClickListener(this)
        phone.setOnClickListener(this)
        protocol.setOnClickListener(this)

        if (User.getCurrent() != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.login -> loginWX()
            R.id.phone -> startActivity(Intent(this, PhoneActivity::class.java))
            R.id.protocol -> {//H5协议
                val intent = Intent(this, WebActivity::class.java)
                intent.putExtra(WebActivity.PARAM_TITLE, "协议")
                intent.putExtra(WebActivity.PARAM_URL, Constants.PROTOCOL)
                startActivity(intent)
            }
        }
    }

    private fun getPermission() {
        val permissionItems = ArrayList<PermissionItem>()
        permissionItems.add(PermissionItem(Manifest.permission.READ_PHONE_STATE, "读取手机", R.drawable.permission_ic_phone))
        permissionItems.add(PermissionItem(Manifest.permission.CAMERA, "照相机", R.drawable.permission_ic_camera))
        permissionItems.add(PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储", R.drawable.permission_ic_storage))
        permissionItems.add(PermissionItem(Manifest.permission.ACCESS_FINE_LOCATION, "定位", R.drawable.permission_ic_location))
        HiPermission.create(this).permissions(permissionItems).title("权限申请")
                .filterColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, theme))
                .checkMutiPermission(object : PermissionCallback {
                    override fun onGuarantee(permission: String?, position: Int) {
                    }

                    override fun onDeny(permission: String?, position: Int) {
                    }

                    override fun onClose() {
                    }

                    override fun onFinish() {
                    }
                })
    }

    private fun loginWX() {
        val callback: Callback<User> = LoginCallback()
        callback.onStart()

        val platform = ShareSDK.getPlatform(Wechat.NAME)
        if (platform.isAuthValid) {
            platform.removeAccount(true)
        }
        platform.SSOSetting(false)
        platform.platformActionListener = object : PlatformActionListener {
            override fun onComplete(platform: Platform, i: Int, hashMap: HashMap<String, Any>) {
                val disposable = RetrofitManager.service.loginWX(platform.db.token, platform.db.userId, "", Push.getDeviceToken(), 1)
                        .compose(SchedulerUtils.ioToMain())
                        .subscribeWith(object : BaseObserver<User>() {
                            override fun onHandleSuccess(t: User) {
                                User.setCurrent(t)
                                MobclickAgent.onProfileSignIn(t.id.toString())
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
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

        override fun onFail(error: String) {
            dismissLoading()
            ToastUtils.showMessage(error)
        }
    }

    override fun accept(t: Notifycation?) {
        super.accept(t)
        when (t!!.code) {
            Constants.LOGIN_PHONE_SUCESS -> {
                finish()
            }
        }
    }
}