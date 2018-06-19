package com.qiushi.wechatshop.ui.manage

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_add_goods_next.*


class AddGoodsNextActivity : BaseActivity() {
    var isSwitch: Boolean = false
    override fun getData() {
    }

    override fun init() {
        StatusBarUtil.immersive(this!!)
        StatusBarUtil.setPaddingSmart(this!!, toolbar)
        if (isSwitch) {
            iv_switch.setImageResource(R.mipmap.ic_goods_open)
        } else {
            iv_switch.setImageResource(R.mipmap.ic_goods_close)
        }
        iv_switch.setOnClickListener(onclickListener)
    }

    override fun layoutId(): Int = R.layout.activity_add_goods_next


    companion object {
        fun startAddGoodsNextActivity(context: Context) {
            val intent = Intent()
            //获取intent对象
            intent.setClass(context, AddGoodsNextActivity::class.java)
            // 获取class是使用::反射
            ContextCompat.startActivity(context, intent, null)
        }
    }

    private val onclickListener = View.OnClickListener { v ->
        when (v!!.id) {
            R.id.iv_switch -> {
                isSwitch=!isSwitch
                if (isSwitch) {
                    iv_switch.setImageResource(R.mipmap.ic_goods_open)
                } else {
                    iv_switch.setImageResource(R.mipmap.ic_goods_close)
                }
            }
        }
    }
}
