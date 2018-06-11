package com.qiushi.wechatshop.ui.shop

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
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
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_manage.*

/**
 * 店铺Fragment
 */
class ShopFragment : BaseFragment() {

    private lateinit var mAdapter: ShopAdapter
    private lateinit var headerView: View
    private lateinit var notDataView: View
    private lateinit var errorView: View

    private var shopID: Long = 0
    private var page = 1

    override fun getLayoutId(): Int = R.layout.fragment_shop

    override fun initView() {
        //RecyclerView
        mRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mAdapter = ShopAdapter()
        mAdapter.openLoadAnimation()
        mRecyclerView.adapter = mAdapter

        notDataView = layoutInflater.inflate(R.layout.empty_view, mRecyclerView.parent as ViewGroup, false)
        notDataView.setOnClickListener { lazyLoad() }
        errorView = layoutInflater.inflate(R.layout.error_view, mRecyclerView.parent as ViewGroup, false)
        errorView.setOnClickListener { lazyLoad() }

        //Header
        headerView = layoutInflater.inflate(R.layout.item_shop_header, mRecyclerView.parent as ViewGroup, false)
        mAdapter.addHeaderView(headerView)
        val lpCover = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                (DensityUtils.getScreenWidth() * 0.48).toInt())
        headerView.findViewById<ImageView>(R.id.cover).layoutParams = lpCover
        headerView.findViewById<ImageView>(R.id.mask).layoutParams = lpCover
        val layoutParams: RelativeLayout.LayoutParams = headerView.findViewById<RelativeLayout>(R.id.layout_shop_info).layoutParams as RelativeLayout.LayoutParams
        layoutParams.setMargins(DensityUtils.dp2px(16.toFloat()),
                StatusBarUtil.getStatusBarHeight(context!!) + activity!!.resources.getDimension(R.dimen.padding_tab_layout_top).toInt(),
                DensityUtils.dp2px(16.toFloat()),
                DensityUtils.dp2px(20.toFloat())
        )

//        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                return if (position == 0) gridLayoutManager.spanCount else 1
//            }
//        }

        //Listener
        mRefreshLayout.setOnRefreshListener {
            isRefresh = true
            page = 1
            lazyLoad()
        }
        mRefreshLayout.setOnLoadMoreListener { lazyLoad() }

        mAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->

        }

        //TODO test
        ImageHelper.loadImage(context, headerView.findViewById(R.id.cover), "https://static.chiphell.com/portal/201803/08/190549vw5xfonuw4wzfuxu.jpg")
        ImageHelper.loadAvatar(context, headerView.findViewById(R.id.logo), "https://static.chiphell.com/forum/201806/06/111637jjxw8cwnj88xss8k.jpg", 48)
        val list = ArrayList<Goods>()
        for (i in 1..8) {
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
                            isRefresh = false
                        }
                    }
                })
        addSubscription(disposable)
    }

    private fun setHeaderData(shop: Shop) {
        ImageHelper.loadImage(context, headerView.findViewById(R.id.cover), "https://static.chiphell.com/portal/201803/08/190549vw5xfonuw4wzfuxu.jpg")
        ImageHelper.loadAvatar(context, headerView.findViewById(R.id.logo), "https://static.chiphell.com/forum/201806/06/111637jjxw8cwnj88xss8k.jpg", 48)
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