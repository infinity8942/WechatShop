package com.qiushi.wechatshop.ui.shop

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.model.Shop
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.net.exception.ErrorStatus
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.util.web.WebActivity
import com.qiushi.wechatshop.view.GridSpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_shop.*

/**
 * 店铺Fragment
 */
class ShopFragment : BaseFragment() {

    private lateinit var mAdapter: ShopAdapter
    //    private lateinit var mCouponAdapter: ShopCouponAdapter
    private lateinit var headerView: View
    private lateinit var notDataView: View
    private lateinit var errorView: View

    private var shopID: Long = 0
    private var page = 1
    private var isFirst = true

    override fun getLayoutId(): Int = R.layout.fragment_shop

    override fun initView() {
        //RecyclerView
        mRecyclerView.layoutManager = GridLayoutManager(context, 2)
        mAdapter = ShopAdapter()
        mAdapter.openLoadAnimation()
        mRecyclerView.addItemDecoration(GridSpaceItemDecoration(DensityUtils.dp2px(9.toFloat()), DensityUtils.dp2px(8.toFloat())))
        mRecyclerView.adapter = mAdapter

        notDataView = layoutInflater.inflate(R.layout.empty_content_view, mRecyclerView.parent as ViewGroup, false)
        notDataView.setOnClickListener { lazyLoad() }
        errorView = layoutInflater.inflate(R.layout.empty_network_view, mRecyclerView.parent as ViewGroup, false)
        errorView.setOnClickListener { lazyLoad() }

        //Header
        headerView = layoutInflater.inflate(R.layout.item_shop_header, mRecyclerView.parent as ViewGroup, false)
        mAdapter.addHeaderView(headerView)

        //Listener
        mRefreshLayout.setOnRefreshListener {
            page = 1
            lazyLoad()
        }
        mRefreshLayout.setOnLoadMoreListener { lazyLoad() }

        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.cart -> {
                    addToShopCart((adapter.data[position] as Goods).id)
                }
            }
        }
        mAdapter.setOnItemClickListener { adapter, _, position ->
            val intent = Intent(activity, WebActivity::class.java)
            intent.putExtra(WebActivity.PARAM_TITLE, (adapter.data[position] as Goods).name)
            intent.putExtra(WebActivity.PARAM_URL, Constants.GOODS_DETAIL + (adapter.data[position] as Goods).id)
            startActivity(intent)
        }
    }

    override fun lazyLoad() {
        val disposable = RetrofitManager.service.shopDetail(shopID)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Shop>() {
                    override fun onHandleSuccess(t: Shop) {
                        if (page == 1) {
                            setHeaderData(t)
                            mAdapter.setNewData(t.goods)
                            mRefreshLayout.finishRefresh(true)
                        } else {
                            mAdapter.addData(t.goods)
                            mRefreshLayout.finishLoadMore(true)
                        }

                        //more
                        if (mAdapter.itemCount == 0) {
                            mAdapter.emptyView = notDataView
                            return
                        }

                        if (t.goods.isNotEmpty()) {
                            if (t.goods.size < Constants.PAGE_NUM) {
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

    private fun setHeaderData(shop: Shop) {
        ImageHelper.loadAvatar(context, headerView.findViewById(R.id.logo), shop.logo, 48)
        headerView.findViewById<TextView>(R.id.name).text = shop.name
        headerView.findViewById<TextView>(R.id.name).paint.isFakeBoldText = true
        headerView.findViewById<TextView>(R.id.des).text = shop.des

        //优惠券
//        headerView.findViewById<RecyclerView>(R.id.rv_coupon).layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        mCouponAdapter = ShopCouponAdapter()
//        mCouponAdapter.openLoadAnimation()
//        headerView.findViewById<RecyclerView>(R.id.rv_coupon).addItemDecoration(SpaceItemDecoration(DensityUtils.dp2px(13.toFloat()), 0))
//        headerView.findViewById<RecyclerView>(R.id.rv_coupon).adapter = mCouponAdapter
//        mCouponAdapter.setNewData(listCoupon)
    }

    /**
     * 添加到购物车
     */
    private fun addToShopCart(goods_id: Long) {
        val disposable = RetrofitManager.service.addToShopCart(shopID, goods_id, 1)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        if (t) {
                            ToastUtils.showMessage(if (isFirst) "添加成功，请到我的购物车查看" else "添加成功")
                            isFirst = false
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
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