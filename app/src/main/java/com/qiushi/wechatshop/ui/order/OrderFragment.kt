package com.qiushi.wechatshop.ui.order

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.WAppContext
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.model.Notifycation
import com.qiushi.wechatshop.model.Order
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.net.exception.ErrorStatus
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.PriceUtil
import com.qiushi.wechatshop.util.RxBus
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.util.web.WebActivity
import com.qiushi.wechatshop.view.SpaceItemDecoration
import com.umeng.analytics.MobclickAgent
import io.github.xudaojie.qrcodelib.CaptureActivity
import kotlinx.android.synthetic.main.fragment_order.*

/**
 * 订单Fragment
 */
class OrderFragment : BaseFragment() {

    private lateinit var mAdapter: OrderAdapter
    private lateinit var notDataView: View
    private lateinit var errorView: View

    private var status = 100
    private var page = 1

    private var mPositionScan = -1
    private var distance: Int = 0
    private var visible = true

    override fun getLayoutId(): Int = R.layout.fragment_order

    override fun initView() {
        //RecyclerView
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mAdapter = OrderAdapter((activity as OrderActivity).isManage)
        mAdapter.openLoadAnimation()
        mRecyclerView.addItemDecoration(SpaceItemDecoration(0, DensityUtils.dp2px(8.toFloat())))
        mAdapter.bindToRecyclerView(mRecyclerView)

        notDataView = layoutInflater.inflate(R.layout.empty_order_view, mRecyclerView.parent as ViewGroup, false)
        notDataView.setOnClickListener { lazyLoad() }
        errorView = layoutInflater.inflate(R.layout.empty_network_view, mRecyclerView.parent as ViewGroup, false)
        errorView.setOnClickListener { lazyLoad() }

        //Listener
        mRefreshLayout.setOnRefreshListener {
            lazyLoad()
        }
        mRefreshLayout.setOnLoadMoreListener { getOrders() }

        if ((activity as OrderActivity).isManage) {
            mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (distance < -ViewConfiguration.get(context).scaledTouchSlop && !visible) {
                        //显示fab
                        (activity as OrderActivity).toggleFilter(true)
                        distance = 0
                        visible = true
                    } else if (distance > ViewConfiguration.get(context).scaledTouchSlop && visible) {
                        //隐藏
                        (activity as OrderActivity).toggleFilter(false)
                        distance = 0
                        visible = false
                    }
                    if ((dy > 0 && visible) || (dy < 0 && !visible))
                        distance += dy
                }
            })
        }

        mAdapter.setOnItemClickListener { adapter, _, position ->
            goToOrderDetails((adapter.data[position] as Order).id)
        }

        mAdapter.setOnItemChildClickListener { adapter, view, position ->

            val order = adapter.data[position] as Order

            when (view.id) {
                R.id.layout_shop -> (activity as OrderActivity).returnToShop(order.shop.id)
                R.id.action ->
                    when (order.status) {
                        Constants.READY_TO_PAY ->
                            if (!(activity as OrderActivity).isManage) {//立即支付
                                goToOrderDetails(true, (adapter.data[position] as Order).id)
                            } else {//提醒支付
//                                if (order.remind_pay == 0) {
//                                    notifyToPay(order.id, position)
//                                } else {
//                                    ToastUtils.showMessage("已发出过提醒，请等待买家支付")
//                                }
                            }
                        Constants.PAYED ->
                            if ((activity as OrderActivity).isManage) {//标记发货
                                val numbers = (mAdapter.getViewByPosition(position, R.id.numbers) as EditText).text.toString().trim()
                                if (numbers.isEmpty()) {
                                    ToastUtils.showMessage("请填写运单号")
                                } else {
                                    markAsDeliver(order.id, numbers)
                                }
                            } else {//提醒发货
                                if (order.remind_send == 0) {
                                    notifyToDeliver(order.id, position)
                                } else {
                                    ToastUtils.showMessage("已发出过提醒，请等待卖家发货")
                                }
                            }
                        Constants.DELIVERED ->
                            if (!(activity as OrderActivity).isManage) {//确认收货
                                markAsDone(order.id)
                            }
                        Constants.DONE ->
                            if (!(activity as OrderActivity).isManage) {//再次购买
                                if (order.goods.size == 1) {
                                    goToGoodsDetails(order.goods[0])
                                } else {
                                    (activity as OrderActivity).returnToShop(order.shop.id)
                                }
                            }
                    }
                R.id.action1 ->
                    when (order.status) {
                        Constants.READY_TO_PAY ->
                            if ((activity as OrderActivity).isManage) {//修改价格
                                showEditPriceDialog(order.id)
                            } else {//取消订单
                                cancelOrder(order.id)
                            }
                        Constants.DELIVERED -> goToExpress(order.id)//查看物流
                        Constants.DONE ->
                            if ((activity as OrderActivity).isManage) {//删除订单
                                delOrder(order.id)
                            } else {//删除订单
                                delOrder(order.id)
                            }
                        Constants.CUSTOM -> delOrder(order.id)//自建订单，删除订单
                    }
                R.id.action2 ->
                    when (order.status) {
//                        Constants.READY_TO_PAY ->
//                            if ((activity as OrderActivity).isManage) {//删除订单
//                                delOrder(order.id)
//                            }
                        Constants.DONE ->
                            if (!(activity as OrderActivity).isManage) {//查看物流
                                goToExpress(order.id)
                            }
                    }
                R.id.scan -> {
                    mPositionScan = position
                    (activity as OrderActivity).startActivityForResult(Intent(activity, CaptureActivity::class.java), 3000)
                }
            }
        }
    }

    fun setNumbers(numbers: String) {
        if (mPositionScan != -1)
            (mAdapter.getViewByPosition(mPositionScan, R.id.numbers) as EditText).setText(numbers)
    }

    override fun lazyLoad() {
        page = 1
        getOrders()
    }

    /**
     * 获取订单列表（店铺、用户）
     */
    fun getOrders() {
        val disposable = RetrofitManager.service.getOrders(
                (activity as OrderActivity).shopID, (activity as OrderActivity).orderNumber,
                (activity as OrderActivity).pay_time, (activity as OrderActivity).keywords,
                (activity as OrderActivity).startTime, (activity as OrderActivity).endTime,
                (activity as OrderActivity).from, status,
                (page - 1) * Constants.PAGE_NUM, Constants.PAGE_NUM)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<ArrayList<Order>>() {
                    override fun onHandleSuccess(t: ArrayList<Order>) {
                        if (page == 1) {
                            mAdapter.setNewData(t)
                            mRefreshLayout.finishRefresh(true)
                        } else {
                            mAdapter.addData(t)
                            mRefreshLayout.finishLoadMore(true)
                        }

                        //more
                        if (mAdapter.itemCount == 0) {
                            mAdapter.emptyView = notDataView
                        }

                        if (t.isNotEmpty()) {
                            if (t.size < Constants.PAGE_NUM) {
                                mRefreshLayout.setEnableLoadMore(false)
                            } else {
                                mRefreshLayout.setEnableLoadMore(true)
                                page++
                            }
                        } else {
                            mRefreshLayout.setEnableLoadMore(false)
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showMessage(error.msg)
                        if (page == 1) {
                            mRefreshLayout.finishRefresh(false)
                        } else {
                            mRefreshLayout.finishLoadMore(false)
                        }
                        if (mAdapter.itemCount == 0) {
                            if (error.code == ErrorStatus.NETWORK_ERROR) {
                                mAdapter.emptyView = errorView
                            } else {
                                mAdapter.emptyView = notDataView
                            }
                        }
                    }
                })
        addSubscription(disposable)
    }

    /**
     * 标记发货
     */
    private fun markAsDeliver(order_id: Long, express_number: String) {
        val disposable = RetrofitManager.service.markAsDeliver(order_id, express_number)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        if (t) {
                            ToastUtils.showMessage("已发货")
                            getOrders()
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showMessage(error.msg)
                    }
                })
        addSubscription(disposable)
    }

//    /**
//     * 提醒支付
//     */
//    private fun notifyToPay(order_id: Long, position: Int) {
//        val disposable = RetrofitManager.service.notifyToPay(order_id)
//                .compose(SchedulerUtils.ioToMain())
//                .subscribeWith(object : BaseObserver<Boolean>() {
//                    override fun onHandleSuccess(t: Boolean) {
//                        if (t) {
//                            ToastUtils.showMessage("已发送提醒")
//                            (mAdapter.getViewByPosition(position, R.id.action) as TextView).text = "已提醒"
//                            (mAdapter.getViewByPosition(position, R.id.action) as TextView).isEnabled = false
//                        }
//                    }
//
//                    override fun onHandleError(error: Error) {
//                        ToastUtils.showMessage(error.msg)
//                        if (error.code == 1004) {
//                            (mAdapter.getViewByPosition(position, R.id.action) as TextView).text = "已提醒"
//                            (mAdapter.getViewByPosition(position, R.id.action) as TextView).isEnabled = false
//                        }
//                    }
//                })
//        addSubscription(disposable)
//    }

    /**
     * 提醒发货
     */
    private fun notifyToDeliver(order_id: Long, position: Int) {
        val disposable = RetrofitManager.service.notifyToDeliver(order_id)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        if (t) {
                            ToastUtils.showMessage("已发送提醒")
                            (mAdapter.getViewByPosition(position, R.id.action) as TextView).text = "已提醒"
                            (mAdapter.getViewByPosition(position, R.id.action) as TextView).isEnabled = false
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showMessage(error.msg)
                        if (error.code == 1004) {
                            (mAdapter.getViewByPosition(position, R.id.action) as TextView).text = "已提醒"
                            (mAdapter.getViewByPosition(position, R.id.action) as TextView).isEnabled = false
                        }
                    }
                })
        addSubscription(disposable)
    }

    /**
     * 确认收货
     */
    private fun markAsDone(order_id: Long) {
        val dialog = AlertDialog.Builder(context!!)
                .setMessage("您要确认收货吗？")
                .setPositiveButton("确认") { _, _ ->
                    val disposable = RetrofitManager.service.markAsDone(order_id)
                            .compose(SchedulerUtils.ioToMain())
                            .subscribeWith(object : BaseObserver<Boolean>() {
                                override fun onHandleSuccess(t: Boolean) {
                                    if (t) {
                                        ToastUtils.showMessage("交易完成")
                                        lazyLoad()
                                        RxBus.getInstance().post(Notifycation(Constants.MARKASDONE, order_id))
                                    }
                                }

                                override fun onHandleError(error: Error) {
                                    ToastUtils.showMessage(error.msg)
                                }
                            })
                    addSubscription(disposable)
                }.setNegativeButton("取消", null).create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.color_more))
    }

    private fun showEditPriceDialog(order_id: Long) {
        val et = EditText(context)
        et.gravity = Gravity.CENTER
        et.inputType = Constants.TYPE_NUMBER_FLAG_DECIMAL
        et.addTextChangedListener(PriceUtil.MoneyTextWatcher(et))

        val dialog = AlertDialog.Builder(context!!).setView(et).setTitle("修改价格")
                .setPositiveButton("修改") { _, _ ->
                    val price = et.text.toString().trim()
                    if (price.isEmpty()) {
                        ToastUtils.showMessage("请填写更改后的价格")
                    } else {
                        editOrderPrice(order_id, price.toDouble())
                    }
                }
                .setNegativeButton("取消", null).create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.color_more))
    }

    /**
     * 修改价格
     */
    private fun editOrderPrice(order_id: Long, price: Double) {
        val disposable = RetrofitManager.service.editOrderPrice(order_id, price)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        if (t) {
                            ToastUtils.showMessage("修改成功")
                            lazyLoad()
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showMessage(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    /**
     * 删除订单
     */
    private fun delOrder(order_id: Long) {
        val dialog = AlertDialog.Builder(context!!)
                .setMessage("您确定要删除该订单吗？")
                .setPositiveButton("删除") { _, _ ->
                    val disposable = RetrofitManager.service.delOrder(order_id)
                            .compose(SchedulerUtils.ioToMain())
                            .subscribeWith(object : BaseObserver<Boolean>() {
                                override fun onHandleSuccess(t: Boolean) {
                                    if (t) {
                                        ToastUtils.showMessage("删除成功")
                                        lazyLoad()
                                    }
                                }

                                override fun onHandleError(error: Error) {
                                    ToastUtils.showMessage(error.msg)
                                }
                            })
                    addSubscription(disposable)
                }.setNegativeButton("取消", null).create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.color_more))
    }

    /**
     * 取消订单
     */
    private fun cancelOrder(order_id: Long) {
        val dialog = AlertDialog.Builder(context!!)
                .setMessage("您确定要取消该订单吗？")
                .setPositiveButton("确定") { _, _ ->
                    val disposable = RetrofitManager.service.cancelOrder(order_id)
                            .compose(SchedulerUtils.ioToMain())
                            .subscribeWith(object : BaseObserver<Boolean>() {
                                override fun onHandleSuccess(t: Boolean) {
                                    if (t) {
                                        ToastUtils.showMessage("已取消")
                                        lazyLoad()
                                    }
                                }

                                override fun onHandleError(error: Error) {
                                    ToastUtils.showMessage(error.msg)
                                }
                            })
                    addSubscription(disposable)
                }.setNegativeButton("取消", null).create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.color_more))
    }

    /**
     * 跳转商品详情页
     */
    private fun goToGoodsDetails(goods: Goods) {
        val intent = Intent(activity, WebActivity::class.java)
        intent.putExtra(WebActivity.PARAM_TITLE, goods.name)
        intent.putExtra(WebActivity.PARAM_URL, Constants.GOODS_DETAIL + goods.id)
        startActivity(intent)
    }

    /**
     * 跳转订单详情页
     */
    private fun goToOrderDetails(id: Long) {
        goToOrderDetails(false, id)
    }

    /**
     * 跳转订单详情页
     */
    private fun goToOrderDetails(payNow: Boolean, id: Long) {
        val intent = Intent(activity, OrderDetailActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("payNow", payNow)
        intent.putExtra("isManage", (activity as OrderActivity).isManage)
        startActivityForResult(intent, 2000)
    }

    /**
     * 跳转物流H5页
     */
    private fun goToExpress(order_id: Long) {
        val intent = Intent(activity, WebActivity::class.java)
        intent.putExtra(WebActivity.PARAM_TITLE, "物流信息")
        intent.putExtra(WebActivity.PARAM_URL, Constants.EXPRESS + order_id)
        startActivity(intent)
    }

    companion object {
        fun getInstance(status: Int): OrderFragment {
            val fragment = OrderFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.status = status
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("OrderFragment")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("OrderFragment")
    }
}