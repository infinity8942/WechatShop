package com.qiushi.wechatshop.ui.user

import android.Manifest
import android.content.Intent
import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.util.*
import com.qiushi.wechatshop.view.search.MaterialSearchView
import kotlinx.android.synthetic.main.activity_setting.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
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
        //状态栏透明和间距处理
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
                android.support.v7.app.AlertDialog.Builder(this)
                        .setMessage("您确定要登出当前账户吗？")
                        .setPositiveButton("退出") { _, _ ->
                            //TODO 登出 clear user
                            User.logout()
                            finish()
                        }
                        .setNegativeButton("取消", null).show()
            }
        }
    }

    override fun getData() {
    }
}