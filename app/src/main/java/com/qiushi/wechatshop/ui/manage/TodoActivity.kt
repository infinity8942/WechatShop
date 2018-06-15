package com.qiushi.wechatshop.ui.manage

import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_todo.*

/**
 * Created by Rylynn on 2018-06-15.
 *
 * 待办事项
 */
class TodoActivity : BaseActivity() {

    override fun layoutId(): Int {
        return R.layout.activity_todo
    }

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)
        back.setOnClickListener(this)
    }

    override fun getData() {
    }
}