package com.qiushi.wechatshop.ui.login

import android.Manifest
import android.content.Intent
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.wechat.friends.Wechat
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.WAppLike
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.ui.MainActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.util.share.Callback
import com.qiushi.wechatshop.util.web.WebActivity
import com.tencent.mm.opensdk.modelmsg.SendAuth
import kotlinx.android.synthetic.main.activity_login.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem
import java.util.HashMap
import kotlin.collections.ArrayList

/**
 * 微信登录
 */
class LoginActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int {
        return R.layout.activity_login
    }

    override fun init() {
        setSwipeBackEnable(false)
        StatusBarUtil.immersive(this)

        getPermission()

        login.setOnClickListener(this)
        phone.setOnClickListener(this)
        protocol.setOnClickListener(this)
        if(User.getCurrent()!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.login -> {
                //TODO 测试，直接进入首页
//                startActivity(Intent(this, MainActivity::class.java))
//                finish()
                loginWX()
//                loginWXX()
            }
            R.id.phone -> {//手机号登录
                startActivity(Intent(this, PhoneActivity::class.java))
            }
            R.id.protocol -> {//H5协议
                val intent = Intent(this, WebActivity::class.java)
                intent.putExtra(WebActivity.PARAM_TITLE, "协议")
                intent.putExtra(WebActivity.PARAM_URL, "http://www.top6000.com")
                startActivity(intent)
            }
        }
    }

    private fun loginWXX() {

        if (!WAppLike.mWxApi.isWXAppInstalled) {
            ToastUtils.showError("您还未安装微信客户端")
            return
        }
        var req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.state = "diandi_wx_login"
        WAppLike.mWxApi.sendReq(req)

    }

    private fun getPermission() {
        val permissionItems = ArrayList<PermissionItem>()
        permissionItems.add(PermissionItem(Manifest.permission.CAMERA, "照相机", R.drawable.permission_ic_camera))
        permissionItems.add(PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储", R.drawable.permission_ic_storage))
        permissionItems.add(PermissionItem(Manifest.permission.ACCESS_FINE_LOCATION, "定位", R.drawable.permission_ic_location))
        HiPermission.create(this).permissions(permissionItems).title("权限申请")
                .filterColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, theme))
                .checkMutiPermission(object : PermissionCallback {
                    override fun onGuarantee(permission: String?, position: Int) {
                    }

                    override fun onDeny(permission: String?, position: Int) {
                        ToastUtils.showError("拒绝权限")
                    }

                    override fun onClose() {
                        ToastUtils.showError("拒绝权限")
                    }

                    override fun onFinish() {
                    }
                })
    }

    private fun loginWX() {//TODO 微信授权注册、登录

        val callback: Callback<User> = LoginCallback()
        callback.onStart()

//        Platform plat = ShareSDK.getPlatform(QQ.NAME)
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

                val disposable = RetrofitManager.service.loginWX(platDB.token, platDB.userId)
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
            //TODO
            //请求 用户信息
            dismissLoading()
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

        override fun onFail(error: String) {
            dismissLoading()
            ToastUtils.showError(error)
        }
    }
}
