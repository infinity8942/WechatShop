package com.qiushi.wechatshop.ui

import android.Manifest
import android.content.Intent
import android.support.v4.content.res.ResourcesCompat
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_login.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem

class LoginActivity : BaseActivity() {

    override fun layoutId(): Int {
        return R.layout.activity_login
    }

    override fun init() {
        getPermission()

        login.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun getData() {
    }

    fun getPermission() {
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
}
