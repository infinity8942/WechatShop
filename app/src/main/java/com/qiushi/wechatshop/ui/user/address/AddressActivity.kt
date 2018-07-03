package com.qiushi.wechatshop.ui.user.address

import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.WAppContext
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
                R.id.edit -> goToEditActivity(true, adapter.data[position] as Buyer)
                R.id.del -> delAddress(position)
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
            R.id.layout_add -> goToEditActivity(false, null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1000 ->
                if (resultCode == RESULT_OK) {
                    getData()
                }
        }
    }

    /**
     * 删除地址
     */
    private fun delAddress(position: Int) {
        val dialog = AlertDialog.Builder(this)
                .setMessage(if (mAdapter.data.size == 1) "您确定要删除最后一条地址吗？" else "您确定要删除该地址吗？")
                .setPositiveButton("删除") { _, _ ->
                    val disposable = RetrofitManager.service.delAddress((mAdapter.data[position] as Buyer).id)
                            .compose(SchedulerUtils.ioToMain())
                            .subscribeWith(object : BaseObserver<Boolean>() {
                                override fun onHandleSuccess(t: Boolean) {
                                    if (t) {
                                        ToastUtils.showMessage("删除成功")
                                        mAdapter.remove(position)
                                    }
                                }

                                override fun onHandleError(error: Error) {
                                    ToastUtils.showError(error.msg)
                                }
                            })
                    addSubscription(disposable)
                }.setNegativeButton("取消", null).create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.color_more))
    }

    private fun goToEditActivity(isEdit: Boolean, address: Buyer?) {
        val intent = Intent(this, AddressEditActivity::class.java)
        intent.putExtra("isEdit", isEdit)
        if (isEdit)
            intent.putExtra("address", address)
        startActivityForResult(intent, 1000)
    }
}