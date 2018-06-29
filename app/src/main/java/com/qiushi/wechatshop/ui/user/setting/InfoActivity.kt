package com.qiushi.wechatshop.ui.user.setting

import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_info.*

/**
 * Created by Rylynn on 2018-06-25.
 *
 * 我的资料
 */
class InfoActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int = R.layout.activity_info

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)


        back.setOnClickListener(this)
    }

    override fun getData() {
    }
}