package com.qiushi.wechatshop.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.classic.common.MultipleStatusView
import com.qiushi.wechatshop.view.LoadingDialog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment : Fragment() {

    /**
     * 视图是否加载完毕
     */
    private var isViewPrepare = false
    /**
     * 数据是否加载过了
     */
    private var hasLoadData = false
    /**
     * 多种状态的 View 的切换
     */
    protected var mLayoutStatusView: MultipleStatusView? = null
    protected var isRefresh = false

    protected var loadingDialog: LoadingDialog? = null
    protected var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), null)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            lazyLoadDataIfPrepared()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewPrepare = true
        initView()
        lazyLoadDataIfPrepared()
        mLayoutStatusView?.setOnClickListener(mRetryClickListener)
    }

    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && isViewPrepare && !hasLoadData) {
            lazyLoad()
            hasLoadData = true
        }
    }

    open val mRetryClickListener: View.OnClickListener = View.OnClickListener {
        lazyLoad()
    }

    /**
     * 加载布局
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * 初始化 View
     */
    abstract fun initView()

    /**
     * 懒加载
     */
    abstract fun lazyLoad()

    fun addSubscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    protected fun showLoading() {
        showLoading("正在加载...")
    }

    protected fun showLoading(msg: String) {
        if (context == null) return
        if (null == loadingDialog) {
            loadingDialog = LoadingDialog(context!!, msg)
        } else {
            loadingDialog!!.setText(msg)
        }
        if (!loadingDialog!!.isShowing)
            loadingDialog!!.show()
    }

    protected fun dismissLoading() {
        if (!loadingDialog!!.isShowing)
            loadingDialog!!.dismiss()
    }

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
        dismissLoading()
        loadingDialog = null
        super.onDestroy()
    }
}
