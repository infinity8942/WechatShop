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
import com.qiushi.wechatshop.ui.order.OrderActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_todo.*

/**
 * Created by Rylynn on 2018-06-15.
 *
 * 待办事项
 */
class TodoActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int = R.layout.activity_todo

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        mRefreshLayout.setEnableLoadMore(false)
        mRefreshLayout.setOnRefreshListener {
            getData()
        }
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
            R.id.back -> finish()
            R.id.layout_deliver -> goToOrderActivity(2)//待发货
            R.id.layout_pay -> goToOrderActivity(1)//待付款
            R.id.layout_amount -> goToManagerGoodsActivity(6)//库存量紧缺
//            R.id.layout_chat -> {//需沟通人员
//
//            }
//            R.id.layout_date -> {//优惠券即将过期
//
//            }
//            R.id.layout_coupon_amount -> {//优惠券剩余数量
//            }
        }
    }

    override fun getData() {
        val disposable = RetrofitManager.service.getToDo(
                User.getCurrent().shop_id)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Todo>() {
                    override fun onHandleSuccess(t: Todo) {
                        mRefreshLayout.finishRefresh(true)
                        deliver.text = t.wait_send.toString()
                        pay.text = t.wait_pay.toString()
                        amount.text = t.num_notice.toString()
                    }

                    override fun onHandleError(error: Error) {
                        mRefreshLayout.finishRefresh(false)
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    /**
     * 跳转订单列表页
     */
    private fun goToOrderActivity(type: Int) {
        val intent = Intent(this, OrderActivity::class.java)
        intent.putExtra("isManage", true)
        intent.putExtra("type", type)
        startActivity(intent)
    }

    /**
     * 跳转产品管理页
     */
    private fun goToManagerGoodsActivity(status: Int) {
        ManagerGoodsActivity.startManagerGoodsActivity(this, status)
    }
}