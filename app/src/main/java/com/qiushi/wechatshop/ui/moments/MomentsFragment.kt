package com.qiushi.wechatshop.ui.moments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.wechat.moments.WechatMoments
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.WAppContext
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.model.Moment
import com.qiushi.wechatshop.model.Notifycation
import com.qiushi.wechatshop.model.User
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
    private var mPlatform: Platform? = null
    private var status = 1
    private var page = 1

    override fun getLayoutId(): Int = R.layout.fragment_moments

    override fun initView() {
        //RecyclerView
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mAdapter = MomentsAdapter()
        mAdapter.openLoadAnimation()
        mRecyclerView.addItemDecoration(SpaceItemDecoration(0, DensityUtils.dp2px(3.toFloat())))
        mAdapter.bindToRecyclerView(mRecyclerView)

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
            val moment = adapter.data[position] as Moment
            when (view.id) {
                R.id.edit -> goToEditMoments(adapter.data[position] as Moment)
                R.id.del -> {
                    val dialog = AlertDialog.Builder(activity!!)
                            .setMessage("您确定要删除该素材吗？")
                            .setPositiveButton("删除") { _, _ ->
                                delMoment(position)
                            }.setNegativeButton("取消", null).create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.colorAccent))
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.color_more))
                }
                R.id.share -> {
                    //分享图片
                    showLoading("正在分享中....")
                    if (moment.images != null && moment.images!!.size > 0) {
                        val imgArrayList = arrayOfNulls<String>(moment.images!!.size)
                        for (i in 0 until moment.images!!.size) {
                            imgArrayList[i] = moment.images!![i].oss_url
                        }
                        dismissLoading()
                        this@MomentsFragment.mPlatform = ShareSDK.getPlatform(WechatMoments.NAME)
                        this@MomentsFragment.mPlatform!!.platformActionListener = platListener
                        this@MomentsFragment.mPlatform!!.share(getShareParams(imgArrayList as Array<String>))
                    } else {
                        dismissLoading()
                    }
                }
            }
        }
    }

    override fun lazyLoad() {
        val disposable = RetrofitManager.service.getMoments(
                User.getCurrent().shop_id, status,
                (page - 1) * Constants.PAGE_NUM, Constants.PAGE_NUM)
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

    private fun delMoment(position: Int) {
        val disposable = RetrofitManager.service.delMoments((mAdapter.data[position] as Moment).id)
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

    override fun accept(t: Notifycation?) {
        super.accept(t)
        when (t!!.code) {
            Constants.MOMENT_FRESH -> {
                page = 1
                lazyLoad()
            }
        }
    }

    private fun getShareParams(imgArrayList: Array<String>): Platform.ShareParams {
        val sp = Platform.ShareParams()
        sp.shareType = Platform.SHARE_IMAGE
        sp.imageArray = imgArrayList
//        sp.imageUrl=Constants.GOODS_DETAIL
//        sp.title = "df"
        return sp
    }

    private val platListener = object : PlatformActionListener {
        override fun onComplete(p0: Platform?, p1: Int, p2: HashMap<String, Any>?) {
            mHandler.sendEmptyMessage(1)
        }

        override fun onCancel(p0: Platform?, p1: Int) {
            mHandler.sendEmptyMessage(2)
        }

        override fun onError(p0: Platform?, p1: Int, p2: Throwable?) {
            mHandler.sendEmptyMessage(3)
        }
    }

    private var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg!!.what) {
                0 -> {
                    //错误
                    dismissLoading()
                }
                1 -> {
                    //成功
                    dismissLoading()
                }
                2 -> {
                    //取消
                    dismissLoading()
                }
            }
        }
    }
}