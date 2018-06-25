package com.qiushi.wechatshop.ui.user

import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.Utils
import kotlinx.android.synthetic.main.activity_about_us.*

/**
 * Created by Rylynn on 2018-06-25.
 *
 * 关于我们
 */
class AboutUsActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int {
        return R.layout.activity_about_us
    }

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        version.text = "当前版本号：v" + Utils.getVersionName()

        back.setOnClickListener(this)
    }

    override fun getData() {
    }
}