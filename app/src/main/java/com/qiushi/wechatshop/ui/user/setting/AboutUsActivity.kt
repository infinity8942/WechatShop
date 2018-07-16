package com.qiushi.wechatshop.ui.user.setting

import android.view.View
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.Utils
import com.sunfusheng.FirUpdater
import kotlinx.android.synthetic.main.activity_about_us.*

/**
 * Created by Rylynn on 2018-06-25.
 *
 * 关于我们
 */
class AboutUsActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int = R.layout.activity_about_us

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        version.text = "当前版本号：v" + Utils.getVersionName()

        back.setOnClickListener(this)
        if (Constants.IS_DEVELOPER)
            logo.setOnClickListener(this)
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.logo -> FirUpdater(this, Constants.FIR_APPTOKEN, Constants.FIR_APPID).checkVersion()
        }
    }
}