package com.qiushi.wechatshop.ui.moments

import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Moment
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_moments_create.*

/**
 * Created by Rylynn on 2018-06-14.
 *
 * 创建、编辑素材
 */
class CreateMomentsActivity : BaseActivity() {

    private var type: Int = 1 //1产品、2鸡汤、3海报
    private var moment: Moment? = null//编辑

    override fun layoutId(): Int {
        return R.layout.activity_moments_create
    }

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        type = intent.getIntExtra("type", 1)
        if (intent.hasExtra("moment")) {
            tv_title.text = "编辑素材"
            moment = intent.getParcelableExtra("moment") as Moment
            setData()
        }

        //Listener
        back.setOnClickListener(this)
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
        }
    }

    private fun setData() {

    }

    private fun addMoments() {
//        val disposable = RetrofitManager.service.addMoments(id)
//                .compose(SchedulerUtils.ioToMain())
//                .subscribeWith(object : BaseObserver<Boolean>() {
//                    override fun onHandleSuccess(t: Boolean) {
//                        if (t) {
//                            ToastUtils.showMessage("添加成功")
//                            finish()
//                        }
//                    }
//
//                    override fun onHandleError(error: Error) {
//                        ToastUtils.showError(error.msg)
//                    }
//                })
//        addSubscription(disposable)
    }

    private fun editMoments() {
//        val disposable = RetrofitManager.service.editMoments(id)
//                .compose(SchedulerUtils.ioToMain())
//                .subscribeWith(object : BaseObserver<Boolean>() {
//                    override fun onHandleSuccess(t: Boolean) {
//                        if (t) {
//                            ToastUtils.showMessage("编辑成功")
//                            finish()
//                        }
//                    }
//
//                    override fun onHandleError(error: Error) {
//                        ToastUtils.showError(error.msg)
//                    }
//                })
//        addSubscription(disposable)
    }
}