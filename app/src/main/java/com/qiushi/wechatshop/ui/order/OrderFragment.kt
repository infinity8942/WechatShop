package com.qiushi.wechatshop.ui.order

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.model.Order
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
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

        notDataView = layoutInflater.inflate(R.layout.empty_view, mRecyclerView.parent as ViewGroup, false)
        notDataView.setOnClickListener { lazyLoad() }
        errorView = layoutInflater.inflate(R.layout.error_view, mRecyclerView.parent as ViewGroup, false)
        errorView.setOnClickListener { lazyLoad() }

        //Listener
        mRefreshLayout.setOnRefreshListener {
            page = 1
            lazyLoad()
        }
        mRefreshLayout.setOnLoadMoreListener { lazyLoad() }

        mAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            val intent = Intent(activity, OrderDetailActivity::class.java)
            intent.putExtra("id", (adapter.data[position] as Order).id)
            startActivity(intent)
        }

        //TODO test
        val list = ArrayList<Order>()
        for (i in 1..5) {
            list.add(Order())
        }
        mAdapter.setNewData(list)
    }

    override fun lazyLoad() {
        val disposable = RetrofitManager.service.orderList()//TODO 筛选
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<ArrayList<Order>>() {
                    override fun onHandleSuccess(t: ArrayList<Order>) {
                        for (i in t) {

                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
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