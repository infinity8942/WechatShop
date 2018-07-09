package com.qiushi.wechatshop.ui.manage

import android.animation.ArgbEvaluator
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.WAppContext
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.model.*
import com.qiushi.wechatshop.model.Function
import com.qiushi.wechatshop.net.BaseResponse
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.net.exception.ErrorStatus
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.ui.login.BindActivity
import com.qiushi.wechatshop.ui.moments.MomentsActivity
import com.qiushi.wechatshop.ui.order.OrderActivity
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.PriceUtil
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.util.web.WebActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_manage.*
import kotlinx.android.synthetic.main.manager_item_icon.view.*

/**
 * 我的店Fragment
 */
class ManageFragment : BaseFragment(), View.OnClickListener {

    var mItemPosition: Int = -1
    var distance: Int = 0
    var argbEvaluator = ArgbEvaluator()
    var color1 = 0
    var color2 = 0
    var color0 = 0

    var type = 0
    private val TYPE_ZD = 1// 置顶
    private val TYPE_DELETE = 2//删除
    private val TYPE_XJ = 3//下架

    private lateinit var headerView: View
    private lateinit var notDataView: View
    private lateinit var errorView: View
    private lateinit var notShop: View //没有店铺的情况
    var mShop: Shop? = null
    /**
     * 整体recyclerview adapter
     */
    private val mAdapter by lazy {
        ManagerAdapter(ArrayList())
    }
    /**
     * 整体recyclerview manager
     */
    private val linearLayoutManager by lazy {
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    /**
     * 头布局列表 manager
     */
    private val mGrideManager by lazy {
        GridLayoutManager(activity, 4)
    }

    private val mGrideAdapter by lazy {
        GrideAdapter(ArrayList())
    }

    private var page = 1

    override fun getLayoutId(): Int = R.layout.fragment_manage

    override fun initView() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(activity!!)
        StatusBarUtil.setPaddingSmart(context!!, toolbar)
        color1 = ContextCompat.getColor(WAppContext.context, R.color.translate)
        color2 = ContextCompat.getColor(WAppContext.context, R.color.colorPrimaryDark)
        //RecyclerView

        notShop = layoutInflater.inflate(R.layout.empty_shop_view, mRecyclerView.parent as ViewGroup, false)
        StatusBarUtil.setPaddingSmart(context!!, notShop)
        notShop.findViewById<Button>(R.id.empty_view_tv).setOnClickListener {
            if (User.getCurrent() != null && User.getCurrent().phone.isNotEmpty()) {
                DecorateActivity.startDecorateActivity(context!!, "", "", "")
            } else {
                startActivity(Intent(context, BindActivity::class.java))
            }

        }
        notDataView = layoutInflater.inflate(R.layout.empty_content_view, mRecyclerView.parent as ViewGroup, false)
        notDataView.setOnClickListener { lazyLoad() }
        errorView = layoutInflater.inflate(R.layout.empty_network_view, mRecyclerView.parent as ViewGroup, false)
        errorView.setOnClickListener { lazyLoad() }

        headerView = layoutInflater.inflate(R.layout.manager_item_head, mRecyclerView.parent as ViewGroup, false)

        //设置name,头像
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mRecyclerView.adapter = mAdapter

        headerView.mRecyclerView.layoutManager = mGrideManager
        headerView.mRecyclerView.adapter = mGrideAdapter
        mGrideAdapter.onItemChildClickListener = mGrideItemClickListener

        mAdapter.onItemChildClickListener = itemChildClickListener
        mAdapter.setOnItemClickListener { adapter, _, position ->
            goToGoodsDetails(adapter.getItem(position) as Goods)
        }
        mRecyclerView.addOnScrollListener(scrollListener)
//        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(false)
        mRefreshLayout.setOnRefreshListener {
            page = 1
            lazyLoad()
        }
        mRefreshLayout.setOnLoadMoreListener { lazyLoad() }

        iv_avaver.setOnClickListener(this)
        tv_header_title.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_avaver, R.id.tv_header_title -> {
                if (mShop?.cover == null) {
                    mShop?.cover = ""
                }
                DecorateActivity.startDecorateActivity(context!!, mShop?.name!!, mShop!!.logo, mShop!!.cover)
            }
        }
    }

    /**
     * 头布局 header
     */
    private fun getHeadView(t: Shop) {
        headerView.findViewById<TextView>(R.id.cash_all).text = PriceUtil.doubleTrans(t.cash_all)
        headerView.findViewById<TextView>(R.id.cash_flow).text = PriceUtil.doubleTrans(t.cash_flow)
        headerView.findViewById<TextView>(R.id.cash_forzen).text = PriceUtil.doubleTrans(t.cash_frozen)
        headerView.shop_more.setOnClickListener({ _: View? ->
            //跳转 产品管理
            ManagerGoodsActivity.startManagerGoodsActivity(this.context!!, 0)
        })
    }

    override fun lazyLoad() {
        mItemPosition = -1
        val subscribeWith: BaseObserver<Shop> = RetrofitManager.service.getMyshop(page)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Shop>() {
                    override fun onHandleSuccess(t: Shop) {
                        if (t != null) {
                            if (mAdapter.emptyView != null) {
                                (mAdapter.emptyView as ViewGroup).removeAllViews()
                            }

                            mShop = t
                            mAdapter.removeAllHeaderView()
                            mAdapter.addHeaderView(headerView)
                            if (t.menu_list != null && t.menu_list.size > 0) {
                                mGrideAdapter.setNewData(t.menu_list)
                            }
                            if (page == 1) {
                                ImageHelper.loadAvatar(activity!!, iv_avaver, t.logo, 28)
                                tv_header_title.text = t.name
                                getHeadView(t)
                                mAdapter.setNewData(t.goods)
                                mRefreshLayout.finishRefresh(true)
                            } else {
                                mAdapter.addData(t.goods)
                                mRefreshLayout.finishRefresh(true)
                            }
                            if (t.goods != null) {
                                if (t.goods.size < Constants.PAGE_NUM) {
                                    mRefreshLayout.setNoMoreData(true)
                                } else {
                                    mRefreshLayout.setNoMoreData(false)
                                    page++
                                }
                            } else {
                                mRefreshLayout.setNoMoreData(true)
                            }
                        }
                    }

                    override fun onHandleError(error: Error) {
                        if (page == 1) {
                            mRefreshLayout.finishRefresh(false)
                        } else {
                            mRefreshLayout.finishRefresh(false)
                        }
                        if (mAdapter.itemCount == 0) {
                            if (error.code == ErrorStatus.NETWORK_ERROR) {
                                mAdapter.emptyView = errorView
                            } else {
                                if (User.getCurrent() != null && (User.getCurrent().phone.isEmpty() || (User.getCurrent().shop_id == null || User.getCurrent().shop_id == 0L))) {
                                    mAdapter.emptyView = notShop
                                } else {
                                    mAdapter.emptyView = notDataView
                                }
                            }
                        }
                    }
                })
        addSubscription(subscribeWith)
    }

    companion object {
        fun getInstance(): ManageFragment {
            val fragment = ManageFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * 商品条目点击事件
     */
    private val itemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
        val mData = adapter.getItem(position) as Goods
        when (view.id) {
            R.id.iv_more -> {
                val item = adapter.getViewByPosition(mRecyclerView, position + 1, R.id.layout_shape)
                if (mItemPosition == -1) {
                    if (item!!.visibility == View.VISIBLE) {
                        item.visibility = View.GONE
                    } else {
                        item.visibility = View.VISIBLE
                    }
                    mItemPosition = position + 1
                } else {
                    if (mItemPosition == position + 1) {
                        if (item!!.visibility == View.VISIBLE) {
                            item.visibility = View.GONE
                        } else {
                            item.visibility = View.VISIBLE
                        }
                        mItemPosition = position + 1
                    } else {
                        adapter.getViewByPosition(mRecyclerView, mItemPosition, R.id.layout_shape)!!.visibility = View.GONE
                        if (item!!.visibility == View.VISIBLE) {
                            item.visibility = View.GONE
                        } else {
                            item.visibility = View.VISIBLE
                        }
                        mItemPosition = position + 1
                    }
                }
            }
            R.id.tv_delete -> {
                type = TYPE_DELETE
                setTop(mData.id, type, position + 1)
                adapter.getViewByPosition(mRecyclerView, position + 1, R.id.layout_shape)?.visibility = View.GONE
            }
            R.id.tv_xj -> {
                type = TYPE_XJ
                setTop(mData.id, type, position + 1)
                adapter.getViewByPosition(mRecyclerView, position + 1, R.id.layout_shape)?.visibility = View.GONE
            }
            R.id.tv_zd -> {
                type = TYPE_ZD
                setTop(mData.id, type, position + 1)
                adapter.getViewByPosition(mRecyclerView, position + 1, R.id.layout_shape)?.visibility = View.GONE
            }
            R.id.iv_edit -> {
                AddGoodsActivity.startAddGoodsActivity(this.context!!, mData.id)
            }
        }
    }

    /**
     * 更多管理 条目点击事件
     */
    private val mGrideItemClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
        val data = adapter.getItem(position) as Function
        when (view.id) {
            R.id.item_name -> {
                when (data.menu_id) {
                    1 -> startActivity(Intent(activity, TodoActivity::class.java))
                    2 -> goToOrderActivity(0)
                    3 -> startActivity(Intent(activity, MomentsActivity::class.java))
                    13 -> {
                        ManagerMoreActivity.startManagerMoreActivity(this.context!!)
                        if (mItemPosition != -1) {
                            mAdapter.getViewByPosition(mRecyclerView, mItemPosition, R.id.layout_shape)!!.visibility = View.GONE
                        }
                    }
                    5 -> {
                        //店铺装修
                        if (mShop?.cover == null) {
                            mShop?.cover = ""
                        }
                        DecorateActivity.startDecorateActivity(context!!, mShop?.name!!, mShop!!.logo, mShop!!.cover)
                    }
                    else -> {//TODO
                        ToastUtils.showError("敬请期待")
                    }
                }
            }
        }
    }

    /**
     * recyclerview滑动事件
     */
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            distance = mRecyclerView.computeVerticalScrollOffset()
            if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                if (distance == 0) {
                    color0 = context!!.resources.getColor(R.color.translate)
                }
                color0 = if (distance < 50) {
                    if (dy > 0) {
                        //往上滑动  、、渐变
                        argbEvaluator.evaluate(Math.abs(distance / 500).toFloat(), color2, color1) as Int
                    } else {
                        //往下滑动
                        argbEvaluator.evaluate(Math.abs(distance / 500).toFloat(), color1, color2) as Int
                    }
                } else {
                    color2
                }
            }
            toolbar.setBackgroundColor(color0)
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (mItemPosition != -1) {
                if (mAdapter.getViewByPosition(mRecyclerView, mItemPosition, R.id.layout_shape) != null) {
                    mAdapter.getViewByPosition(mRecyclerView, mItemPosition, R.id.layout_shape)!!.visibility = View.GONE
                    mItemPosition = -1
                }
            }
        }
    }

    /**
     * 置顶 下架 删除
     */
    private fun setTop(goods_id: Long, type: Int, i: Int) {

        val observable: Observable<BaseResponse<Boolean>> = when (type) {
            TYPE_ZD -> RetrofitManager.service.setTop(goods_id)
            TYPE_XJ -> RetrofitManager.service.upShop(goods_id)
            else -> RetrofitManager.service.deleteShop(goods_id)
        }
        val disposable = observable
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        when (type) {
                            TYPE_ZD -> {
                                if (t) {
                                    ToastUtils.showSuccess("置顶成功")
                                } else {
                                    ToastUtils.showSuccess("取消置顶")
                                }
                                page = 1
                                lazyLoad()
                            }
                            TYPE_XJ -> {
                                if (t) {
                                    ToastUtils.showSuccess("下架成功")
                                } else {
                                    ToastUtils.showSuccess("上架成功")
                                }
                                page = 1
                                lazyLoad()
                            }
                            else -> {
                                if (t) {
                                    ToastUtils.showSuccess("删除成功")
                                } else {
                                    ToastUtils.showSuccess("删除失败")
                                }
                                page = 1
                                lazyLoad()
                            }
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    /**
     * 跳转商品详情页
     */
    private fun goToGoodsDetails(goods: Goods) {
        val intent = Intent(activity, WebActivity::class.java)
        intent.putExtra(WebActivity.PARAM_TITLE, goods.name)
        intent.putExtra(WebActivity.PARAM_URL, Constants.GOODS_DETAIL + goods.id)
        startActivity(intent)
    }

    fun goToOrderActivity(type: Int) {
        val intent = Intent(activity, OrderActivity::class.java)
        intent.putExtra("isManage", true)
        intent.putExtra("type", type)
        startActivity(intent)
    }

    override fun accept(t: Notifycation?) {
        super.accept(t)
        when (t!!.code) {
            Constants.ADD_IMG_REFRESH -> {
                page = 1
                lazyLoad()
            }
            Constants.OPEN_SHOP, Constants.ZX_SHOP -> {
                page = 1
                lazyLoad()
                //开店或者装修回调
            }
            Constants.MANAGER_GOODS -> {
                page = 1
                lazyLoad()
            }
        }
    }
}