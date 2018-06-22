package com.qiushi.wechatshop.ui.order

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
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
import com.qiushi.wechatshop.view.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_order.*

/**
 * 订单Fragment
 */
class OrderFragment : BaseFragment() {

    private lateinit var mAdapter: OrderAdapter
    private lateinit var notDataView: View
    private lateinit var errorView: View

    private var status = 1
    private var page = 1

    override fun getLayoutId(): Int = R.layout.fragment_order

    override fun initView() {
        //RecyclerView
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mAdapter = OrderAdapter()
        mAdapter.openLoadAnimation()
        mRecyclerView.addItemDecoration(SpaceItemDecoration(0, DensityUtils.dp2px(8.toFloat())))
        mRecyclerView.adapter = mAdapter

        notDataView = layoutInflater.inflate(R.layout.empty_order_view, mRecyclerView.parent as ViewGroup, false)
        notDataView.setOnClickListener { lazyLoad() }
        errorView = layoutInflater.inflate(R.layout.error_view, mRecyclerView.parent as ViewGroup, false)
        errorView.setOnClickListener { lazyLoad() }

        //Listener
        mRefreshLayout.setOnRefreshListener {
            page = 1
            lazyLoad()
        }
        mRefreshLayout.setOnLoadMoreListener { lazyLoad() }

        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.layout_shop -> {
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.putExtra("jumpToShop", (adapter.data[position] as Order).shop.id)
                    startActivity(intent)
                    (activity as OrderActivity).finish()
                }
                else -> {
                    val intent = Intent(activity, OrderDetailActivity::class.java)
                    intent.putExtra("id", (adapter.data[position] as Order).id)
                    startActivity(intent)
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
                mAdapter.itemCount, Constants.PAGE_NUM)
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
                            mRefreshLayout.setNoMoreData(t.size < Constants.PAGE_NUM)
                            page++
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