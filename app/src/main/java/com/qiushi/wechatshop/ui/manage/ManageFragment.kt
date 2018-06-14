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
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.model.Function
import com.qiushi.wechatshop.model.MyShop
import com.qiushi.wechatshop.model.Shop
import com.qiushi.wechatshop.model.ShopOrder
import com.qiushi.wechatshop.net.BaseResponse
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.ui.moments.MomentsActivity
import com.qiushi.wechatshop.ui.order.OrderActivity
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_manage.*
import kotlinx.android.synthetic.main.manager_item_icon.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * 我的店Fragment
 */
class ManageFragment : BaseFragment() {


    private var mShop: MyShop? = null
    private var mFunctionList = ArrayList<Function>()
    private var mShopOrderList = ArrayList<ShopOrder>()
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
        color1 = ContextCompat.getColor(context!!, R.color.translate)
        color2 = ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        //RecyclerView
        val mFunction1 = Function(1, "待办事项", "", "")
        val mFunction2 = Function(2, "订单管理", "", "")
        val mFunction3 = Function(3, "素材管理", "", "")
        val mFunction4 = Function(4, "知识库", "", "")
        val mFunction5 = Function(5, "用户管理", "", "")
        val mFunction6 = Function(6, "更多", "", "")
        val mShopOrder = ShopOrder(1, "老虎商店", Constants.GOOD0, 1, false)
        val mShopOrder1 = ShopOrder(2, "测试老虎1", Constants.GOOD1, 89, false)
        val mShopOrder2 = ShopOrder(3, "测试老虎2", Constants.GOOD2, 89, false)
        val mShopOrder3 = ShopOrder(4, "测试老虎3", Constants.GOOD3, 89, false)
        val mShopOrder4 = ShopOrder(5, "测试老虎4", Constants.GOOD4, 89, false)
        val mShopOrder5 = ShopOrder(6, "测试老虎5", Constants.GOOD5, 89, false)
        val mShopOrder6 = ShopOrder(7, "测试老虎6", Constants.GOOD6, 89, false)
        val mShopOrder7 = ShopOrder(8, "测试老虎7", Constants.GOOD7, 89, false)

        mShopOrderList.add(mShopOrder)
        mShopOrderList.add(mShopOrder1)
        mShopOrderList.add(mShopOrder2)
        mShopOrderList.add(mShopOrder3)
        mShopOrderList.add(mShopOrder4)
        mShopOrderList.add(mShopOrder5)
        mShopOrderList.add(mShopOrder6)
        mShopOrderList.add(mShopOrder7)

        mFunctionList.add(mFunction1)
        mFunctionList.add(mFunction2)
        mFunctionList.add(mFunction3)
        mFunctionList.add(mFunction4)
        mFunctionList.add(mFunction5)
        mFunctionList.add(mFunction6)

        mShop = MyShop(mFunctionList, mShopOrderList)


        //设置name,头像
        tv_header_title.text = mShop?.name
        ImageHelper.loadAvatar(activity!!, iv_avaver, Constants.GOOD0, 28)
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mAdapter.addHeaderView(getHeadView())
        mRecyclerView.adapter = mAdapter
        mAdapter.onItemChildClickListener = itemChildClickListener
        mRecyclerView.addOnScrollListener(scrollListener)
        //Listener
        mRefreshLayout.setOnRefreshListener {
            //            isRefresh = true
            page = 1

        }
        mRefreshLayout.setOnLoadMoreListener { lazyLoad() }
    }

    /**
     * 头布局 header
     */
    private fun getHeadView(): View {
        val view = View.inflate(activity, R.layout.manager_item_head, null)
        view.mRecyclerView.layoutManager = mGrideManager
        view.mRecyclerView.adapter = mGrideAdapter
        mGrideAdapter.onItemChildClickListener = mGrideItemClickListener
        //头布局数据
        view.findViewById<TextView>(R.id.cash_all).text = (mShop?.cash_all).toString()
        view.findViewById<TextView>(R.id.cash_flow).text = mShop?.cash_flow.toString()
        view.findViewById<TextView>(R.id.cash_forzen).text = mShop?.cash_forzen.toString()
        view.shop_more.setOnClickListener(View.OnClickListener { v: View? ->
            //跳转 产品管理
            ManagerGoodsActivity.startManagerGoodsActivity(this!!.context!!)
        })
        return view
    }

    override fun lazyLoad() {
        val subscribeWith: BaseObserver<Shop> = RetrofitManager.service.getMyshop()
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Shop>() {
                    override fun onHandleSuccess(t: Shop) {
                        if (t?.menu_list != null && t.menu_list.size > 0) {
                            mGrideAdapter.setNewData(t.menu_list)
                        }
                        if (page == 1) {
                            if (t != null) {
                                if (t.goods != null && t.goods.size != 0) {
                                    mAdapter.setNewData(t.goods)
                                }
                            }
                        } else {
                            if (t != null) {
                                if (t.goods != null && t.goods.size != 0) {
                                    mAdapter.addData(t.goods)
                                }
                            }
                        }

                    }

                    override fun onHandleError(error: Error) {

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
        val mData = adapter.getItem(position) as ShopOrder
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
                setTop(mData.id.toLong(), type)
                ToastUtils.showError("删除")
                adapter.getViewByPosition(mRecyclerView, position + 1, R.id.layout_shape)?.visibility = View.GONE
            }
            R.id.tv_xj -> {
                type = TYPE_XJ
                setTop(mData.id.toLong(), type)
                ToastUtils.showError("下架")
                adapter.getViewByPosition(mRecyclerView, position + 1, R.id.layout_shape)?.visibility = View.GONE
            }
            R.id.tv_zd -> {
                type = TYPE_ZD
                ToastUtils.showError("置顶")
                setTop(mData.id.toLong(), type)
                adapter.getViewByPosition(mRecyclerView, position + 1, R.id.layout_shape)?.visibility = View.GONE
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
                    2 -> startActivity(Intent(activity, OrderActivity::class.java))
                    3 -> startActivity(Intent(activity, MomentsActivity::class.java))
                    6 -> {
                        ManagerMoreActivity.startManagerMoreActivity(this.context!!)
                        if (mItemPosition != -1) {
                            mAdapter.getViewByPosition(mRecyclerView, mItemPosition, R.id.layout_shape)!!.visibility = View.GONE
                        }
                    }
                    else -> ToastUtils.showError("其他未做")
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
                mAdapter.getViewByPosition(mRecyclerView, mItemPosition, R.id.layout_shape)!!.visibility = View.GONE
                mItemPosition = -1
            }
        }
    }

    /**
     * 置顶 下架 删除
     */
    private fun setTop(goods_id: Long, type: Int) {

        val observable: Observable<BaseResponse<Boolean>> = when (type) {
            TYPE_ZD -> RetrofitManager.service.setTop(goods_id)
            TYPE_XJ -> RetrofitManager.service.upShop(goods_id)
//            TYPE_DELETE -> RetrofitManager.service.deleteShop(goods_id)
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
                            }
                            TYPE_XJ -> {
                                if (t) {
                                    ToastUtils.showSuccess("下架成功")
                                } else {
                                    ToastUtils.showSuccess("上架成功")
                                }
                            }
                            else -> {
                                if (t) {
                                    ToastUtils.showSuccess("删除成功")
                                } else {
                                    ToastUtils.showSuccess("删除失败")
                                }
                            }
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
    }
}