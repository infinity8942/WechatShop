package com.qiushi.wechatshop.ui.shop

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.net.exception.ErrorStatus
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.test.Beauty
import com.qiushi.wechatshop.test.TestObserver
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_manage.*

/**
 * 店铺Fragment
 */
class ShopFragment : BaseFragment() {

    private val mAdapter by lazy { ShopAdapter(activity!!, ArrayList()) }
    private val linearLayoutManager by lazy {
        GridLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    private var shopID: Long = 0
    private var page = 1

    override fun getLayoutId(): Int = R.layout.fragment_shop

    override fun initView() {
        //设置状态布局
        mLayoutStatusView = multipleStatusView

        //RecyclerView
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()

        //Listener
        mRefreshLayout.setOnRefreshListener {
            isRefresh = true
            page = 1
            lazyLoad()
        }
        mRefreshLayout.setOnLoadMoreListener { lazyLoad() }
    }

    override fun lazyLoad() {
        showLoadingView()
        val disposable = RetrofitManager.service.test("7f3a3cee57641229c1b392c2b3911bd8", page, Constants.PAGE_NUM)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : TestObserver<ArrayList<Beauty>>() {
                    override fun onHandleSuccess(t: ArrayList<Beauty>) {
                        mLayoutStatusView?.showContent()

                        if (page == 1) {
                            mAdapter.setData(t)
                            mRefreshLayout.finishRefresh(true)
                        } else {
                            mAdapter.addData(t)
                            mRefreshLayout.finishLoadMore(true)
                        }

                        //more
                        mRefreshLayout.setNoMoreData(t.size < Constants.PAGE_NUM)
                        page++
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
                        if (page == 1) {
                            mRefreshLayout.finishRefresh(false)
                        } else {
                            mRefreshLayout.finishLoadMore(false)
                        }
                        if (mAdapter.mData.isEmpty()) {
                            showErrorView(error)
                        }
                    }
                })
        addSubscription(disposable)
    }

    /**
     * 显示 Loading （下拉刷新的时候不需要显示 Loading）
     */
    private fun showLoadingView() {
        if (!isRefresh) {
            mLayoutStatusView?.showLoading()
        }
    }

    private fun showErrorView(error: Error) {
        if (error.code == ErrorStatus.NETWORK_ERROR) {
            mLayoutStatusView?.showNoNetwork()
        } else {
            mLayoutStatusView?.showError()
        }
        isRefresh = false
    }

    companion object {
        fun getInstance(shopID: Long): ShopFragment {
            val fragment = ShopFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.shopID = shopID
            return fragment
        }
    }
}