package com.qiushi.wechatshop.ui.moments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
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
import com.umeng.analytics.MobclickAgent
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

//                    PosterXQImgCache.getInstance().removeImgCache() //先清空路径缓存
//                    ImgFileUtils.deleteDir()//删除本地缓存的图片
                    if (moment.images != null && moment.images!!.size > 0) {

//                        for (item in moment.images!!) {
//                            GlideApp.with(this)
//                                    .asBitmap()
//                                    .load(item.oss_url)
//                                    .into(object : SimpleTarget<Bitmap>() {
//                                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                                            ImgFileUtils.saveBitmap(context, resource, DateUtil.getTimeString())
//                                        }
//
//                                    })
//                        }
//
//                        mHandler.postDelayed(Runnable {
//                            var imgCache = PosterXQImgCache.getInstance().imgCache
//                            val uris = arrayOfNulls<Uri>(imgCache.size)
//                            for (i in 0 until imgCache.size) {
//                                uris[i] = Uri.fromFile(File(imgCache[i]))
//                            }
//                            var shareUtils = ShareUtils(context, "")
//                            shareUtils.shareweipyqSomeImg(context, uris)
//
//                        }, 500)

                        if (moment.type != 3) {
                            ToastUtils.showMessage("素材内容已复制到剪贴板，可以在微信中进行粘贴")
                            val cm: ClipboardManager = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.primaryClip = ClipData.newPlainText("Label", moment.content)
                        }

                        val imgArrayList = arrayOfNulls<String>(moment.images!!.size)
                        for (i in 0 until moment.images!!.size) {
                            imgArrayList[i] = moment.images!![i].oss_url
                        }
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
                        }

                        if (t.isNotEmpty()) {
                            if (t.size < Constants.PAGE_NUM) {
                                mRefreshLayout.setEnableLoadMore(false)
                            } else {
                                mRefreshLayout.setEnableLoadMore(true)
                                page++
                            }
                        } else {
                            mRefreshLayout.setEnableLoadMore(false)
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showMessage(error.msg)
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
                            if (mAdapter.itemCount == 0) {
                                mAdapter.emptyView = notDataView
                            }
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showMessage(error.msg)
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
        }

        override fun onCancel(p0: Platform?, p1: Int) {
        }

        override fun onError(p0: Platform?, p1: Int, p2: Throwable?) {
        }
    }

    override fun onStop() {
        super.onStop()
        dismissLoading()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.e("tag", "hiden$hidden")
        if (!hidden) {
            dismissLoading()
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("MomentsFragment")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("MomentsFragment")
    }
}