package com.qiushi.wechatshop.ui.order

import android.support.v7.widget.LinearLayoutManager
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Order
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.DateUtil
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_order_detail.*

/**
 * Created by Rylynn on 2018-06-12.
 *
 * 订单详情
 */
class OrderDetailActivity : BaseActivity() {

    private var orderID: Long = 0
    private lateinit var mAdapter: OrderGoodsAdapter

    override fun layoutId(): Int {
        return R.layout.activity_order_detail
    }

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        orderID = intent.getLongExtra("id", -1)

        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = OrderGoodsAdapter()
        mRecyclerView.adapter = mAdapter

        back.setOnClickListener(this)
    }

    override fun getData() {
        val disposable = RetrofitManager.service.getOrderDetail(10091,//TODO 测试数据
                orderID)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Order>() {
                    override fun onHandleSuccess(t: Order) {
                        ImageHelper.loadAvatar(this@OrderDetailActivity, logo, t.shop.logo, 24)
                        shop.text = t.shop.name

                        mAdapter.setNewData(t.goods)

                        amount.text = "共计" + t.num + "件商品"
                        price.text = "￥" + t.price
                        when (t.status) {
                            0 -> status.text = "等待买家付款"
                            1 -> status.text = "买家已付款"
                            2 -> status.text = "等待买家收货"
                            3 -> status.text = "已完成"
                        }
                        message.text = "买家留言：" + t.content
                        number.text = "订单编号：" + t.number
                        create_time.text = "创建时间：" + DateUtil.getMillon(t.create_time)
                        pay_time.text = "付款时间：" + DateUtil.getMillon(t.pay_time)
                        when (t.payment) {
                            1 -> payment.text = "支付方式：微信"
                            2 -> payment.text = "支付方式：支付宝"
                        }

                        deliver_time.text = "发货时间：" + DateUtil.getMillon(t.deliver_time)
                        achieve_time.text = "成交时间：" + DateUtil.getMillon(t.achieve_time)
                    }

                    override fun onHandleError(error: com.qiushi.wechatshop.net.exception.Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
    }
}