package com.qiushi.wechatshop.ui.user

import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_feedback.*

/**
 * Created by Rylynn on 2018-06-15.
 *
 * 用户反馈
 */
class FeedbackActivity : BaseActivity(), View.OnClickListener {

    private var type = 1

    override fun layoutId(): Int = R.layout.activity_feedback

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        radiogroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb1 -> type = 1
                R.id.rb2 -> type = 2
                R.id.rb3 -> type = 3
            }
        }
        commit.setOnClickListener(this)
        back.setOnClickListener(this)

        phone.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                feedback()
            }
            false
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.commit -> feedback()
        }
    }

    private fun feedback() {
        if (TextUtils.isEmpty(content.text.toString().trim())) {
            ToastUtils.showWarning("请填写您的宝贵意见")
            return
        }
        val disposable = RetrofitManager.service.feedback(type,
                content.text.toString().trim(), phone.text.toString().trim())
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        if (t) {
                            ToastUtils.showMessage("反馈成功")
                            this@FeedbackActivity.finish()
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    override fun getData() {
    }
}