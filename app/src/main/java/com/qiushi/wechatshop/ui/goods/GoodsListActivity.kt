package com.qiushi.wechatshop.ui.goods

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.model.SelectOrder
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_order_list.*

/**
 * 商品列表页
 */
class GoodsListActivity : BaseActivity() {
    //    private lateinit var headerView: View
    private lateinit var notDataView: View
    private lateinit var errorView: View
    private var page = 1
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

//        headerView = layoutInflater.inflate(R.layout.orderlist_header, mRecyclerView.parent as ViewGroup, false)

        mRecyclerView.layoutManager = mGrideManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mRecyclerView.adapter = mGrideAdapter
//        mGrideAdapter.addHeaderView(headerView)

        //Listener
        back.setOnClickListener(this)

        mGrideAdapter.onItemChildClickListener = onItemchildListener

        mRefreshLayout.setOnRefreshListener {
            page = 1
            getData()
        }
        mRefreshLayout.setOnLoadMoreListener { getData() }
    }

    override fun getData() {
        val subscribeWith: BaseObserver<SelectOrder> = RetrofitManager.service.checkGoods(User.getCurrent().shop_id, page)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<SelectOrder>() {
                    override fun onHandleSuccess(t: SelectOrder) {
                        if (page == 1) {
                            mGrideAdapter.setNewData(t.goods_list)
                            mRefreshLayout.finishRefresh(true)
                        } else {
                            mGrideAdapter.addData(t.goods_list!!)
                            mRefreshLayout.finishRefresh(true)
                        }
                        if (t.goods_list != null) {
                            if(t.goods_list!!.size<Constants.PAGE_NUM){
                                mRefreshLayout.setNoMoreData(true)
                            }else{
                                mRefreshLayout.setNoMoreData(false)
                                page++
                            }
                        } else {
                            mRefreshLayout.setNoMoreData(true)
                        }
                    }

                    override fun onHandleError(error: Error) {
                        if (page == 1) {
                            mRefreshLayout.finishRefresh(false)
                        } else {
                            mRefreshLayout.finishRefresh(false)
                        }

                        if (error.code == -1001) {
                            mGrideAdapter.emptyView = notDataView
                        } else {
                            ToastUtils.showError(error.msg)
                        }
                    }
                })
        addSubscription(subscribeWith)
    }

    /**
     * 选中商品
     */
    fun selectGoods(goods: Goods) {
        val intent = Intent()
        intent.putExtra("goods", goods)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        fun startOrderListActivity(context: Activity) {
            context.startActivityForResult(Intent(context, GoodsListActivity::class.java), 1000)
        }
    }

    private val onItemchildListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
        when (view!!.id) {
            R.id.iv_add -> {
                selectGoods(adapter.getItem(position) as Goods)
            }
        }
    }
}