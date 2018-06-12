package com.qiushi.wechatshop.ui.manage

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.model.Function
import com.qiushi.wechatshop.model.MyShop
import com.qiushi.wechatshop.model.ShopOrder
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_manage.*
import kotlinx.android.synthetic.main.manager_item_icon.view.*

/**
 * 我的店Fragment
 */
class ManageFragment : BaseFragment() {

    var mShop: MyShop? = null
    var mFunctionList = ArrayList<Function>()
    var mShopOrderList = ArrayList<ShopOrder>()

    /**
     * 整体recyclerview adapter
     */
    private val mAdapter by lazy {
        ManagerAdapter(mShopOrderList)
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
        GrideAdapter(mFunctionList)
    }

    private var page = 1

    override fun getLayoutId(): Int = R.layout.fragment_manage

    override fun initView() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(activity!!)
        StatusBarUtil.setPaddingSmart(context!!, toolbar)

        //RecyclerView
        var mFunction1 = Function(1, "待办事项")
        var mFunction2 = Function(2, "订单管理")
        var mFunction3 = Function(3, "素材管理")
        var mFunction4 = Function(4, "知识库")
        var mFunction5 = Function(5, "用户管理")
        var mFunction6 = Function(6, "更多")
        var mShopOrder = ShopOrder(1, "老虎商店", Constants.GOOD0, 1, false)
        var mShopOrder1 = ShopOrder(2, "测试老虎1", Constants.GOOD1, 89, false)
        var mShopOrder2 = ShopOrder(3, "测试老虎2", Constants.GOOD2, 89, false)
        var mShopOrder3 = ShopOrder(4, "测试老虎3", Constants.GOOD3, 89, false)
        var mShopOrder4 = ShopOrder(5, "测试老虎4", Constants.GOOD4, 89, false)
        var mShopOrder5 = ShopOrder(6, "测试老虎5", Constants.GOOD5, 89, false)
        var mShopOrder6 = ShopOrder(7, "测试老虎6", Constants.GOOD6, 89, false)
        var mShopOrder7 = ShopOrder(8, "测试老虎7", Constants.GOOD7, 89, false)

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
        ImageHelper.loadAvaer(activity!!, iv_avaver, Constants.GOOD0, 28, 28)
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mAdapter.addHeaderView(getHeadView())
        mRecyclerView.adapter = mAdapter

//        mRecyclerView.addOnScrollListener(  RecyclerView.OnScrollListener!)
        mAdapter.onItemChildClickListener = itemChildClickListener


        //Listener
        mRefreshLayout.setOnRefreshListener {
            //            isRefresh = true
            page = 1
            lazyLoad()
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
        //头布局数据
        view.findViewById<TextView>(R.id.cash_all).text = (mShop?.cash_all).toString()
        view.findViewById<TextView>(R.id.cash_flow).text = mShop?.cash_flow.toString()
        view.findViewById<TextView>(R.id.cash_forzen).text = mShop?.cash_forzen.toString()
        return view
    }

    override fun lazyLoad() {

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
     * 条目点击事件
     */
    private val itemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
        var data = adapter.getItem(position) as ShopOrder
        var dataList = adapter.data as List<ShopOrder>
        when (view.id) {
            R.id.iv_more -> {
                data.isCheck = !data.isCheck
                for (i in 0 until dataList.size) {
                    if (dataList[i].id != data.id) {
                        if (data.isCheck) {
                            if (dataList[i].isCheck) {
                                dataList[i].isCheck = false
                                adapter.notifyItemChanged(position + 1)
                                adapter.notifyItemChanged(i + 1)
                            } else {
                                adapter.notifyItemChanged(position + 1)
                            }
                        } else {
                            adapter.notifyItemChanged(position + 1)
                        }
                    }
                }
            }
        }
    }

    /**
     * 滑动监听 改变toolbar 颜色状态
     */
//    private val scrollListener=OnScrollListener{_,}
}