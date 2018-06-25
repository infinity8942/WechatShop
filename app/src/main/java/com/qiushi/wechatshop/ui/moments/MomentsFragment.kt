package com.qiushi.wechatshop.ui.moments

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.model.Moment

import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.net.exception.ErrorStatus
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.view.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_moments.*
import java.util.*

/**
 * 素材Fragment
 */
class MomentsFragment : BaseFragment() {

    private lateinit var mAdapter: MomentsAdapter
    private lateinit var notDataView: View
    private lateinit var errorView: View

    private var status = 1
    private var page = 1

    override fun getLayoutId(): Int = R.layout.fragment_moments

    override fun initView() {
        //RecyclerView
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mAdapter = MomentsAdapter()
        mAdapter.openLoadAnimation()
        mRecyclerView.addItemDecoration(SpaceItemDecoration(0, DensityUtils.dp2px(3.toFloat())))
        mRecyclerView.adapter = mAdapter

        notDataView = layoutInflater.inflate(R.layout.empty_content_view, mRecyclerView.parent as ViewGroup, false)
        notDataView.setOnClickListener { lazyLoad() }
        errorView = layoutInflater.inflate(R.layout.empty_network_view, mRecyclerView.parent as ViewGroup, false)
        errorView.setOnClickListener { lazyLoad() }

        //Listener
        mRefreshLayout.setOnRefreshListener {
            page = 1
            lazyLoad()
        }
        mRefreshLayout.setOnLoadMoreListener { lazyLoad() }

        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.edit -> goToEditMoments(adapter.data[position] as Moment)
                R.id.del -> {
                    android.support.v7.app.AlertDialog.Builder(context!!)
                            .setMessage("您确定要删除该素材吗？")
                            .setPositiveButton("删除") { _, _ ->
                                mAdapter.remove(position)
                                delMoment((adapter.data[position] as Moment).id)
                            }
                            .setNegativeButton("取消", null).show()
                }
            }
        }


    }

    override fun lazyLoad() {
        val disposable = RetrofitManager.service.getMoments(
                10091  //TODO 测试数据
                , status, mAdapter.itemCount, Constants.PAGE_NUM)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<ArrayList<Moment>>() {
                    override fun onHandleSuccess(t: ArrayList<Moment>) {
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
                            mRefreshLayout.setNoMoreData(t.size < Constants.PAGE_NUM)
                            page++
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

    private fun delMoment(id: Long) {
        val disposable = RetrofitManager.service.delMoments(id)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        if (t) {
                            ToastUtils.showMessage("删除成功")
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    /**
     * 跳转创建素材页
     */
    private fun goToEditMoments(moment: Moment) {
        val intent = Intent(activity, CreateMomentsActivity::class.java)
        intent.putExtra("id", moment.id)
        intent.putExtra("type", moment.type)
        startActivity(intent)
    }

    companion object {
        fun getInstance(status: Int): MomentsFragment {
            val fragment = MomentsFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.status = status
            return fragment
        }
    }
}