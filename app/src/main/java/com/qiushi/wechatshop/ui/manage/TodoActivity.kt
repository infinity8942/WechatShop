package com.qiushi.wechatshop.ui.manage

import android.content.Intent
import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Todo
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.ui.MainActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_todo.*

/**
 * Created by Rylynn on 2018-06-15.
 *
 * 待办事项
 */
class TodoActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int {
        return R.layout.activity_todo
    }

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        back.setOnClickListener(this)
        layout_deliver.setOnClickListener(this)
        layout_pay.setOnClickListener(this)
        layout_amount.setOnClickListener(this)
//        layout_chat.setOnClickListener(this)
//        layout_date.setOnClickListener(this)
//        layout_coupon_amount.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.layout_deliver -> {//待发货
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("jumpToOrder", 2)
                startActivity(intent)
                finish()
            }
            R.id.layout_pay -> {//待付款
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("jumpToOrder", 1)
                startActivity(intent)
                finish()
            }
            R.id.layout_amount -> {//库存量紧缺
                ManagerGoodsActivity.startManagerGoodsActivity(this, 0)
            }
//            R.id.layout_chat -> {//需沟通人员
//
//            }
//            R.id.layout_date -> {//优惠券即将过期
//
//            }
//            R.id.layout_coupon_amount -> {//优惠券剩余数量
//            }
            R.id.back -> finish()
        }
    }

    override fun getData() {
        val disposable = RetrofitManager.service.getToDo(
                User.getCurrent().shop_id)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Todo>() {
                    override fun onHandleSuccess(t: Todo) {
                        deliver.text = t.wait_send.toString()
                        pay.text = t.wait_pay.toString()
                        amount.text = t.num_notice.toString()
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
    }
}