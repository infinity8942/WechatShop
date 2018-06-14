package com.qiushi.wechatshop.ui.manage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.View
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_edit_text.*


class EditTextActivity : BaseActivity() {
    private var mText: String = ""
    override fun init() {
        StatusBarUtil.immersive(this!!)
        StatusBarUtil.setPaddingSmart(this!!, toolbar)

        if (!mText?.contentEquals("")) {
            et_text.setText(mText)
        }

        commit?.setOnClickListener(View.OnClickListener { v: View? ->
            if (et_text.text.equals("")) {
                ToastUtils.showError("请编辑")
            } else {
                var intent = Intent()
                intent.putExtra("text", mText)
                setResult(1, Intent(intent))
            }
        })

    }

    override fun getData() {

    }

    override fun layoutId(): Int = R.layout.activity_edit_text


    override fun getParams(intent: Intent) {
        mText = intent?.getStringExtra("text")
    }


    companion object {
        fun startEditTextActivity(context: Activity, text: String) {
            val intent = Intent()
            //获取intent对象
            intent.putExtra("text", text)
            intent.setClass(context, EditTextActivity::class.java)
            // 获取class是使用::反射
            context.startActivityForResult(intent, Constants.EDIT_TEXT_REQUEST)

        }
    }

}
