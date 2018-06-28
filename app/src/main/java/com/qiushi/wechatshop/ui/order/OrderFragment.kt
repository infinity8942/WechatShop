package com.qiushi.wechatshop.ui.order

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.WAppContext
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.model.Order
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.net.exception.ErrorStatus
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.ui.MainActivity
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.util.web.WebActivity
import com.qiushi.wechatshop.view.SpaceItemDecoration
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
            page = 1
            lazyLoad()
        }
        mRefreshLayout.setOnLoadMoreListener { lazyLoad() }

        mAdapter.setOnItemClickListener { adapter, _, position ->
            goToOrderDetails((adapter.data[position] as Order).id)
        }

        mAdapter.setOnItemChildClickListener { adapter, view, position ->

            val order = adapter.data[position] as Order

            when (view.id) {
                R.id.layout_shop -> {
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.putExtra("jumpToShop", order.shop.id)
                    startActivity(intent)
                    (activity as OrderActivity).finish()
                }
                R.id.action ->
                    when (order.status) {
                        Constants.READY_TO_PAY ->
                            if ((activity as OrderActivity).isManage) {//提醒支付
                                notifyToPay(order.id, position)
                            }
                        Constants.PAYED ->
                            if ((activity as OrderActivity).isManage) {//标记发货
                                val numbers = (mAdapter.getViewByPosition(position, R.id.numbers) as EditText).text.toString().trim()
                                if (numbers.isEmpty()) {
                                    ToastUtils.showError("请填写运单号")
                                } else {
                                    markAsDeliver(order.id, numbers)
                                }
                            } else {//提醒发货
                                notifyToDeliver(order.id, position)
                            }
                        Constants.DELIVERED ->
                            if (!(activity as OrderActivity).isManage) {//确认收货
                                markAsDone(order.id)
                            }
                        Constants.DONE ->
                            if (!(activity as OrderActivity).isManage) {//再次购买

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
                        Constants.READY_TO_PAY ->
                            if ((activity as OrderActivity).isManage) {//删除订单
                                delOrder(order.id)
                            }
                        Constants.DONE ->
                            if (!(activity as OrderActivity).isManage) {//查看物流
                                goToExpress(order.id)
                            }
                    }
            }
        }
    }

    override fun lazyLoad() {
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
                            return
                        }

                        if (t.isNotEmpty()) {
                            if (t.size < Constants.PAGE_NUM) {
                                mRefreshLayout.setNoMoreData(true)
                            } else {
                                mRefreshLayout.setNoMoreData(false)
                                page++
                            }
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
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
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    /**
     * 提醒支付
     */
    private fun notifyToPay(order_id: Long, position: Int) {
        val disposable = RetrofitManager.service.notifyToPay(order_id)
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
                        ToastUtils.showError(error.msg)
                        if (error.code == 1004) {
                            (mAdapter.getViewByPosition(position, R.id.action) as TextView).text = "已提醒"
                            (mAdapter.getViewByPosition(position, R.id.action) as TextView).isEnabled = false
                        }
                    }
                })
        addSubscription(disposable)
    }

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
                        ToastUtils.showError(error.msg)
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
                                        getOrders()
                                    }
                                }

                                override fun onHandleError(error: Error) {
                                    ToastUtils.showError(error.msg)
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
        et.hint = "请输入价格"
        et.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL

        val dialog = AlertDialog.Builder(context!!).setView(et)
                .setPositiveButton("修改") { _, _ ->
                    val price = et.text.toString().trim()
                    if (price.isEmpty()) {
                        ToastUtils.showError("请填写更改后的价格")
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
                            getOrders()
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
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
                                        getOrders()
                                    }
                                }

                                override fun onHandleError(error: Error) {
                                    ToastUtils.showError(error.msg)
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
                                        getOrders()
                                    }
                                }

                                override fun onHandleError(error: Error) {
                                    ToastUtils.showError(error.msg)
                                }
                            })
                    addSubscription(disposable)
                }.setNegativeButton("取消", null).create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.color_more))
    }

    private fun goToOrderDetails(id: Long) {
        val intent = Intent(activity, OrderDetailActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("isManage", (activity as OrderActivity).isManage)
        startActivity(intent)
    }

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
}