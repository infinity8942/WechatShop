package com.qiushi.wechatshop.ui.order


import android.app.Activity
import android.content.Intent
import android.support.v4.content.ContextCompat
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

       var mGoods1 = Goods("小商店")
        var mGoods2 = Goods("小商店1")
        var mGoods3 = Goods("小商店2")
        var mGoods4 = Goods("小商店3")
        mList!!.add(mGoods1!!)
        mList!!.add(mGoods2!!)
        mList!!.add(mGoods3!!)
        mList!!.add(mGoods4!!)


        notDataView = layoutInflater.inflate(R.layout.empty_content_view, mRecyclerView.parent as ViewGroup, false)
        notDataView.setOnClickListener { getData() }
        errorView = layoutInflater.inflate(R.layout.empty_network_view, mRecyclerView.parent as ViewGroup, false)
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


    companion object {
        fun startOrderListActivity(context: Activity) {
            var intent = Intent()
            intent.setClass(context, OrderListActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
        }
    }

}
