package com.qiushi.wechatshop.ui.login

import android.Manifest
import android.content.Intent
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.ui.MainActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_login.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem

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
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.login -> loginWx()
            R.id.phone -> {//手机号登录
                startActivity(Intent(this, PhoneActivity::class.java))
            }
            R.id.protocol -> {//H5协议

            }
        }
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

    private fun loginWx() {//TODO 微信授权
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
