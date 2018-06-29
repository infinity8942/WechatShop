package com.qiushi.wechatshop.ui.user.address

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Buyer
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.net.exception.ErrorStatus
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.view.SpaceItemDecoration
import kotlinx.android.synthetic.main.activity_address.*
import java.util.*

/**
 * Created by Rylynn on 2018-06-29.
 *
 * 我的地址
 */
class AddressActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mAdapter: AddressAdapter
    private lateinit var notDataView: View
    private lateinit var errorView: View
    private var page = 1

    override fun layoutId(): Int = R.layout.activity_address

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        //RecyclerView
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = AddressAdapter()
        mAdapter.openLoadAnimation()
        mRecyclerView.addItemDecoration(SpaceItemDecoration(0, DensityUtils.dp2px(8.toFloat())))
        mAdapter.bindToRecyclerView(mRecyclerView)

        notDataView = layoutInflater.inflate(R.layout.empty_content_view, mRecyclerView.parent as ViewGroup, false)
        notDataView.setOnClickListener { getData() }
        errorView = layoutInflater.inflate(R.layout.empty_network_view, mRecyclerView.parent as ViewGroup, false)
        errorView.setOnClickListener { getData() }

        //Listener
        mRefreshLayout.setOnRefreshListener {
            page = 1
            getData()
        }
        mRefreshLayout.setOnLoadMoreListener { getData() }

        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.edit -> goToEditActivity()
            }
        }

        back.setOnClickListener(this)
        layout_add.setOnClickListener(this)
    }

    override fun getData() {
        val disposable = RetrofitManager.service.getAddress()
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<ArrayList<Buyer>>() {
                    override fun onHandleSuccess(t: ArrayList<Buyer>) {
                        if (page == 1) {
                            mAdapter.setNewData(t)
                            mRefreshLayout.finishRefresh(true)
                        } else {
                            mAdapter.addData(t)
                            mRefreshLayout.finishLoadMore(true)
                        }

                        //more
                        if (mAdapter.itemCount == 0) {
                            mAdapter.emptyView = notDataView
                            return
                        }

                        if (t.isNotEmpty()) {
                            if (t.size < Constants.PAGE_NUM) {
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.layout_add -> goToEditActivity()
        }
    }

    private fun goToEditActivity() {
        startActivity(Intent(this, AddressEditActivity::class.java))
    }
}