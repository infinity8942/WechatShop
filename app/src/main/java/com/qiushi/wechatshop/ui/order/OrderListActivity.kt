package com.qiushi.wechatshop.ui.order


import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.view.ViewGroup
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Goods

import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_order_list.*


class OrderListActivity : BaseActivity() {
    private lateinit var headerView: View
    private lateinit var notDataView: View
    private lateinit var errorView: View
    var mGoods: Goods? = null
    var mList: ArrayList<Goods>? = ArrayList()
    private val mGrideManager by lazy {
        GridLayoutManager(this, 2)
    }


    private val mGrideAdapter by lazy {
        OrderGrideAdapter(ArrayList())
    }

    override fun layoutId(): Int = R.layout.activity_order_list

    override fun init() {
        StatusBarUtil.immersive(this!!)
        StatusBarUtil.setPaddingSmart(this!!, toolbar)

        mGoods = Goods("小商店")
        mList!!.add(mGoods!!)


        notDataView = layoutInflater.inflate(R.layout.empty_view, mRecyclerView.parent as ViewGroup, false)
        notDataView.setOnClickListener { getData() }
        errorView = layoutInflater.inflate(R.layout.error_view, mRecyclerView.parent as ViewGroup, false)
        errorView.setOnClickListener { getData() }

        headerView = layoutInflater.inflate(R.layout.orderlist_header, mRecyclerView.parent as ViewGroup, false)

        mRecyclerView.layoutManager = mGrideManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mRecyclerView.adapter = mGrideAdapter
        mGrideAdapter.addHeaderView(headerView)
        mGrideAdapter.setNewData(mList)
    }

    override fun getData() {

    }


}
