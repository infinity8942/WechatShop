package com.qiushi.wechatshop.ui.manage

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.model.Function
import com.qiushi.wechatshop.model.MyShop
import com.qiushi.wechatshop.model.ShopOrder
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_manage.*

/**
 * 我的店Fragment
 */
class ManageFragment : BaseFragment() {

    var mShop: MyShop? = null
    var mFunctionList = ArrayList<Function>()
    var mShopOrderList = ArrayList<ShopOrder>()

    //    private val mAdapter by lazy {
//        ManagerAdapter(activity!!, ArrayList(), this!!.mShop!!)
//    }
    private val linearLayoutManager by lazy {
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    private var page = 1

    override fun getLayoutId(): Int = R.layout.fragment_manage

    override fun initView() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(activity!!)
        StatusBarUtil.setPaddingSmart(context!!, toolbar)

        //RecyclerView
        var mFunction1 = Function()
        var mFunction2 = Function()
        var mFunction3 = Function()
        var mFunction4 = Function()
        var mFunction5 = Function()
        var mShopOrder = ShopOrder()
        mShopOrderList.add(mShopOrder)
        mFunctionList.add(mFunction1)
        mFunctionList.add(mFunction2)
        mFunctionList.add(mFunction3)
        mFunctionList.add(mFunction4)
        mFunctionList.add(mFunction5)

        mShop = MyShop(mFunctionList, mShopOrderList)
        Log.e("tag", "myShop" + mShop!!.function.size)
//        mRecyclerView.adapter = mAdapter
//        mAdapter.setData(mShopOrderList)
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

    }

//    override fun lazyLoad() {
//        showLoadingView()
//        val disposable = RetrofitManager.service.test("7f3a3cee57641229c1b392c2b3911bd8", page, Constants.PAGE_NUM)
//                .compose(SchedulerUtils.ioToMain())
//                .subscribeWith(object : TestObserver<ArrayList<Beauty>>() {
//                    override fun onHandleSuccess(t: ArrayList<Beauty>) {
//                        mLayoutStatusView?.showContent()
//
//                        if (page == 1) {
//                            mAdapter.setData(t)
//                            mRefreshLayout.finishRefresh(true)
//                        } else {
//                            mAdapter.addData(t)
//                            mRefreshLayout.finishLoadMore(true)
//                        }
//
//                        //more
//                        mRefreshLayout.setNoMoreData(t.size < Constants.PAGE_NUM)
//                        page++
//                    }
//
//                    override fun onHandleError(error: Error) {
//                        ToastUtils.showError(error.msg)
//                        if (page == 1) {
//                            mRefreshLayout.finishRefresh(false)
//                        } else {
//                            mRefreshLayout.finishLoadMore(false)
//                        }
//                        if (mAdapter.mData.isEmpty()) {
//                            showErrorView(error)
//                        }
//                    }
//                })
//        addSubscription(disposable)
//    }

    companion object {
        fun getInstance(): ManageFragment {
            val fragment = ManageFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }
}