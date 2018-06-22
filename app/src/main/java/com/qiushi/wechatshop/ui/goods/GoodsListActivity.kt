package com.qiushi.wechatshop.ui.goods

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.view.ViewGroup
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.ui.order.AddOrderActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_order_list.*

/**
 * 商品列表页
 */
class GoodsListActivity : BaseActivity() {
    private lateinit var headerView: View
    private lateinit var notDataView: View
    private lateinit var errorView: View

    var mList: ArrayList<Goods>? = ArrayList()
    private val mGrideManager by lazy {
        GridLayoutManager(this, 2)
    }

    private val mGrideAdapter by lazy {
        GoodsAdapter(ArrayList())
    }

    override fun layoutId(): Int = R.layout.activity_order_list

    override fun init() {
        StatusBarUtil.immersive(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        notDataView = layoutInflater.inflate(R.layout.empty_content_view, mRecyclerView.parent as ViewGroup, false)
        notDataView.setOnClickListener { getData() }
        errorView = layoutInflater.inflate(R.layout.empty_network_view, mRecyclerView.parent as ViewGroup, false)
        errorView.setOnClickListener { getData() }

        headerView = layoutInflater.inflate(R.layout.orderlist_header, mRecyclerView.parent as ViewGroup, false)

        mRecyclerView.layoutManager = mGrideManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mRecyclerView.adapter = mGrideAdapter
        mGrideAdapter.addHeaderView(headerView)

        //Listener
        back.setOnClickListener(this)
        mGrideAdapter.setOnItemChildClickListener { adapter, view, position ->

        }

        //TODO 测试数据
        mList!!.add(Goods("小商店"))
        mList!!.add(Goods("小商店1"))
        mList!!.add(Goods("小商店2"))
        mList!!.add(Goods("小商店3"))
        mGrideAdapter.setNewData(mList)
    }

    override fun getData() {

    }

    /**
     * 选中商品
     */
    fun selectGoods(goods: Goods) {
        val intent = Intent(this, AddOrderActivity::class.java)
        intent.putExtra("goods", goods)
        setResult(Activity.RESULT_OK, intent)
    }

    companion object {
        fun startOrderListActivity(context: Activity) {
            context.startActivityForResult(Intent(context, GoodsListActivity::class.java), 1000)
        }
    }
}