package com.qiushi.wechatshop.ui.user

import android.Manifest
import android.content.Intent
import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.*
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

    override fun layoutId(): Int {
        return R.layout.activity_setting
    }

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
        version.text = Utils.getVersionName()

        back.setOnClickListener(this)
        layout_info.setOnClickListener(this)
        layout_cache.setOnClickListener(this)
        layout_feedback.setOnClickListener(this)
        layout_about_us.setOnClickListener(this)
        logout.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.layout_info -> {//我的资料

            }
            R.id.layout_cache -> {
                ImageHelper.clear()
                ToastUtils.showMessage("已清理")
                cache.text = "0b"
            }
            R.id.layout_feedback -> startActivity(Intent(this@SettingActivity, FeedbackActivity::class.java))
            R.id.layout_about_us -> {

            }
            R.id.logout -> {

            }
        }
    }

    override fun getData() {
    }
}