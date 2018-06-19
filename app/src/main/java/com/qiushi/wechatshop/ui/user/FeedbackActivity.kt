package com.qiushi.wechatshop.ui.user

import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_feedback.*

/**
 * Created by Rylynn on 2018-06-15.
 *
 * 用户反馈
 */
class FeedbackActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int {
        return R.layout.activity_feedback
    }

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        radiogroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb1 -> {
                }
                R.id.rb2 -> {
                }
                R.id.rb3 -> {
                }
            }
        }
        commit.setOnClickListener(this)
        back.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.commit -> {
            }
        }
    }

    override fun getData() {
    }
}