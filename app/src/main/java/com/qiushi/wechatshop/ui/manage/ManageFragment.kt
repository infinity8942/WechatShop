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
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.net.exception.ErrorStatus
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_manage.*

/**
 * 我的店Fragment
 */
class ManageFragment : BaseFragment() {


    var mShop: MyShop? = null
    var mFunctionList = ArrayList<Function>()
    var mShopOrderList = ArrayList<ShopOrder>()


    private val mAdapter by lazy {
        ManagerAdapter(activity!!, ArrayList(), this!!.mShop!!)
    }
    private val linearLayoutManager by lazy {
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    private var page = 1

    override fun getLayoutId(): Int = R.layout.fragment_manage

    override fun initView() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(activity!!)
        StatusBarUtil.setPaddingSmart(context!!, toolbar)


        //设置状态布局
        mLayoutStatusView = multipleStatusView

        //RecyclerView
        var mFunction1 = Function(1, "待办事项")
        var mFunction2 = Function(2, "订单管理")
        var mFunction3 = Function(3, "素材管理")
        var mFunction4 = Function(4, "知识库")
        var mFunction5 = Function(5, "用户管理")
        var mFunction6 = Function(6, "更多")
        var mShopOrder = ShopOrder(1,"老虎商店","https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1528683174&di=b8a8dae0a7b1f984bc4deb8358b7f812&src=http://dl.ppt123.net/pptbj/201203/2012032518021342.jpg",1)
        var mShopOrder1=ShopOrder(2,"小孩商店","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1528696398065&di=0019a60bd91406628866a1fbd0399b68&imgtype=0&src=http%3A%2F%2Fi1.hdslb.com%2Fbfs%2Farchive%2Fc04adaac9ac46896ab6d4c759385bdbc1ca2aa86.jpg",89)
        mShopOrderList.add(mShopOrder)
        mShopOrderList.add(mShopOrder1)

        mFunctionList.add(mFunction1)
        mFunctionList.add(mFunction2)
        mFunctionList.add(mFunction3)
        mFunctionList.add(mFunction4)
        mFunctionList.add(mFunction5)
        mFunctionList.add(mFunction6)

        mShop = MyShop(mFunctionList, mShopOrderList)
        mRecyclerView.adapter = mAdapter
        mAdapter.setData(mShopOrderList)
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
        fun getInstance(): ManageFragment {
            val fragment = ManageFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }
}