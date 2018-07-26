package com.qiushi.wechatshop.ui.user.setting

import android.Manifest
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.WAppContext
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.ui.MainActivity
import com.qiushi.wechatshop.util.*
import com.qiushi.wechatshop.util.permission.HiPermission
import com.qiushi.wechatshop.util.permission.PermissionCallback
import com.qiushi.wechatshop.view.search.MaterialSearchView
import kotlinx.android.synthetic.main.activity_setting.*
import java.io.File

/**
 * Created by Rylynn on 2018-06-15.
 *
 * 设置
 */
class SettingActivity : BaseActivity(), View.OnClickListener {
    private var mCacheFolder: File? = null

    override fun layoutId(): Int = R.layout.activity_setting

    override fun init() {
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        mCacheFolder = File(FileUtil.getRootFile(), "cache")
        HiPermission.create(this).checkSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, object : PermissionCallback {
            override fun onGuarantee(permission: String?, position: Int) {
                cache.text = FileUtil.FormatFileSize(mCacheFolder)
            }

            override fun onDeny(permission: String?, position: Int) {
            }

            override fun onClose() {
            }

            override fun onFinish() {
            }
        })
        version.text = "v" + Utils.getVersionName()
        back.setOnClickListener(this)
//        layout_info.setOnClickListener(this)
        layout_cache.setOnClickListener(this)
        layout_feedback.setOnClickListener(this)
        layout_about_us.setOnClickListener(this)
        logout.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
//            R.id.layout_info -> {//我的资料
//
//            }
            R.id.layout_cache -> {
                ImageHelper.clear()
                ToastUtils.showMessage("已清理")
                cache.text = "0b"
                MaterialSearchView(this).clearAll()
            }
            R.id.layout_feedback -> startActivity(Intent(this@SettingActivity, FeedbackActivity::class.java))
            R.id.layout_about_us -> startActivity(Intent(this@SettingActivity, AboutUsActivity::class.java))
            R.id.logout -> {
                val dialog = AlertDialog.Builder(this@SettingActivity)
                        .setMessage("您确定要登出当前账户吗？")
                        .setPositiveButton("退出") { _, _ ->
                            User.logout()
                            val intent = Intent(this@SettingActivity, MainActivity::class.java)
                            intent.putExtra("logout", true)
                            startActivity(intent)
                            finish()
                        }.setNegativeButton("取消", null).create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.colorAccent))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.color_more))
            }
        }
    }

    override fun getData() {
    }
}