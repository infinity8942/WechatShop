package com.qiushi.wechatshop.ui.manage

import android.app.Activity
import android.content.Intent
import android.text.InputFilter
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_edit_text.*

class EditTextActivity : BaseActivity() {

    private var mText: String = ""

    override fun layoutId(): Int = R.layout.activity_edit_text

    override fun init() {
        StatusBarUtil.immersive(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)
        et_text.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(100))
        if (!mText.contentEquals("")) {
            et_text.setText(mText)
        }

        commit.setOnClickListener {
            if (et_text.text.toString().isEmpty()) {
                ToastUtils.showMessage("请编辑")
            } else {
                val intent = Intent()
                intent.putExtra("text", et_text.text.toString())
                setResult(1, Intent(intent))
                finish()
            }
        }
        back.setOnClickListener(this)
    }

    override fun getData() {
    }

    override fun getParams(intent: Intent) {
        mText = intent.getStringExtra("text")
    }

    companion object {
        fun startEditTextActivity(context: Activity, text: String) {
            val intent = Intent(context, EditTextActivity::class.java)
            intent.putExtra("text", text)
            context.startActivityForResult(intent, Constants.EDIT_TEXT_REQUEST)
        }
    }
}