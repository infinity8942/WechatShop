package com.qiushi.wechatshop.ui.manage

import android.text.TextUtils
import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_decorate.*

/**
 * Created by Rylynn on 2018-06-15.
 *
 * 店铺装修
 */
class DecorateActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int {
        return R.layout.activity_decorate
    }

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        back.setOnClickListener(this)
        logo.setOnClickListener(this)
        cover.setOnClickListener(this)
        commit.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.logo -> {//TODO 店铺装修选图、接口

            }
            R.id.cover -> {

            }
            R.id.commit -> commit()
        }
    }

    override fun getData() {
    }

    private fun commit() {
        val name = name.text.toString().trim()
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showWarning("店铺名不能为空")
            return
        }
        if (name == "我的店" || name == "我的店铺" || name == "My Shop" || name == "MyShop") {
            ToastUtils.showWarning("店铺名不合法，请重新输入")
            return
        }
    }
}