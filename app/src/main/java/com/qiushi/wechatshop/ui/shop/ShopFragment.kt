package com.qiushi.wechatshop.ui.shop

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.net.exception.ErrorStatus
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.test.Beauty
import com.qiushi.wechatshop.test.TestAdapter
import com.qiushi.wechatshop.test.TestObserver
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_shop.*

/**
 * 店铺Fragment
 */
class ShopFragment : BaseFragment() {

    private var mTitle: String? = null
    private var mTestAdapter: TestAdapter? = null

    override fun getLayoutId(): Int = R.layout.fragment_shop

    override fun initView() {
        //状态栏透明和间距处理
        StatusBarUtil.darkMode(activity!!)
        StatusBarUtil.setPaddingSmart(context!!, toolbar)
        //设置状态布局
        mLayoutStatusView = multipleStatusView

        mRefreshLayout.setOnRefreshListener {
            isRefresh = true
            lazyLoad()
        }
    }

    override fun lazyLoad() {
        showLoadingView()
        val disposable = RetrofitManager.service.test("7f3a3cee57641229c1b392c2b3911bd8", 1, 15)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : TestObserver<ArrayList<Beauty>>() {
                    override fun onComplete() {
                        mRefreshLayout.finishRefresh()
                    }

                    override fun onHandleSuccess(t: ArrayList<Beauty>) {
                        mLayoutStatusView?.showContent()

                        mTestAdapter = TestAdapter(activity!!, t)
                        mRecyclerView.adapter = mTestAdapter
                        mRecyclerView.layoutManager = linearLayoutManager
                        mRecyclerView.itemAnimator = DefaultItemAnimator()
                    }

                    override fun onHandleError(error: Error) {
                        showErrorView(error)
                    }
                })
        addSubscription(disposable)
    }

    private val linearLayoutManager by lazy {
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
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
    }

    companion object {
        fun getInstance(title: String): ShopFragment {
            val fragment = ShopFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }
}