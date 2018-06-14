package com.qiushi.wechatshop.ui.shop

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
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

    override fun getLayoutId(): Int = R.layout.fragment_shop

    override fun initView() {
        //RecyclerView
        mRecyclerView.layoutManager = GridLayoutManager(context, 2)
        mAdapter = ShopAdapter()
        mAdapter.openLoadAnimation()
        mRecyclerView.addItemDecoration(GridSpaceItemDecoration(DensityUtils.dp2px(9.toFloat()), DensityUtils.dp2px(8.toFloat())))
        mRecyclerView.adapter = mAdapter

        notDataView = layoutInflater.inflate(R.layout.empty_view, mRecyclerView.parent as ViewGroup, false)
        notDataView.setOnClickListener { lazyLoad() }
        errorView = layoutInflater.inflate(R.layout.error_view, mRecyclerView.parent as ViewGroup, false)
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

        mAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
        }

        //TODO test
        ImageHelper.loadAvatar(context, headerView.findViewById(R.id.logo), Constants.AVATAR, 48)
        headerView.findViewById<TextView>(R.id.name).text = "尼萌手工甜品店"
        headerView.findViewById<TextView>(R.id.name).paint.isFakeBoldText = true
        headerView.findViewById<TextView>(R.id.des).text = "充满爱意的甜点更加美味"

//        val listCoupon = ArrayList<Coupon>()
//        for (i in 1..5) {
//            listCoupon.add(Coupon(50))
//        }
//        headerView.findViewById<RecyclerView>(R.id.rv_coupon).layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        mCouponAdapter = ShopCouponAdapter()
//        mCouponAdapter.openLoadAnimation()
//        headerView.findViewById<RecyclerView>(R.id.rv_coupon).addItemDecoration(SpaceItemDecoration(DensityUtils.dp2px(13.toFloat()), 0))
//        headerView.findViewById<RecyclerView>(R.id.rv_coupon).adapter = mCouponAdapter
//        mCouponAdapter.setNewData(listCoupon)

        val list = ArrayList<Goods>()
        for (i in 1..10) {
            list.add(Goods("商品" + i))
        }
        mAdapter.setNewData(list)
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
                        mRefreshLayout.setNoMoreData(t.goods.size < Constants.PAGE_NUM)
                        page++
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