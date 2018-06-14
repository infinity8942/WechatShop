package com.qiushi.wechatshop.ui.manage

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_edit_text.*


class EditTextActivity : BaseActivity() {
    var mText: String = ""
    override fun init() {
        StatusBarUtil.immersive(this!!)
        StatusBarUtil.setPaddingSmart(this!!, toolbar)

        if (!mText?.contentEquals("")) {
            et_text.setText(mText)
        }
    }

    override fun getData() {
    }

    override fun layoutId(): Int = R.layout.activity_edit_text


    override fun getParams(intent: Intent) {
        mText = intent?.getStringExtra("text")
    }


    companion object {
        fun startEditTextActivity(context: Context,text:String) {
            val intent = Intent()
            //获取intent对象
//            intent.putExtra()
            intent.setClass(context, AddGoodsActivity::class.java)
            // 获取class是使用::反射
            ContextCompat.startActivity(context, intent, null)
        }
    }

}
