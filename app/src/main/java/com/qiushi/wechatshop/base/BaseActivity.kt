package com.qiushi.wechatshop.base

import android.content.Intent
import android.os.Bundle
import com.qiushi.wechatshop.view.LoadingDialog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.imid.swipebacklayout.lib.app.SwipeBackActivity

abstract class BaseActivity : SwipeBackActivity() {

    var loadingDialog: LoadingDialog? = null

    var compositeDisposable = CompositeDisposable()//订阅集合

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        getParams(intent)
        init()
        getData()
    }

    /**
     * 加载布局
     */
    abstract fun layoutId(): Int

    /**
     * 初始化
     */
    abstract fun init()

    /**
     * 开始请求
     */
    abstract fun getData()

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
        dismissLoading()
        loadingDialog = null
        super.onDestroy()
    }

    @JvmOverloads
    protected fun showLoading(msg: String = "正在加载...") {
        if (null == loadingDialog) {
            loadingDialog = LoadingDialog(this, msg)
        } else {
            loadingDialog!!.setText(msg)
        }
        if (!loadingDialog!!.isShowing) {
            loadingDialog!!.show()
        }
    }

    protected fun dismissLoading() {
        if (loadingDialog != null && loadingDialog!!.isShowing) {
            loadingDialog!!.dismiss()
        }
    }

    fun addSubscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    open fun getParams(intent: Intent) {

    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        getParams(intent!!)
        setIntent(intent)
    }
}