package com.qiushi.wechatshop.ui.shop

import android.os.Bundle
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.LazyLoadFragment
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.ExceptionHandle
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.ToastUtils.showError
import com.scwang.smartrefresh.header.MaterialHeader
import kotlinx.android.synthetic.main.fragment_shop.*

/**
 * 店铺Fragment
 */
class ShopFragment : LazyLoadFragment() {

    private var mTitle: String? = null
    private var mMaterialHeader: MaterialHeader? = null
//    private var mHomeAdapter: HomeAdapter? = null

    override fun layoutId(): Int {
        return R.layout.fragment_shop
    }

    override fun getParams(savedInstanceState: Bundle?) {
        arguments?.let {
        }
    }

    override fun initView() {
        //内容跟随偏移
        mRefreshLayout.setEnableHeaderTranslationContent(true)
        mRefreshLayout.setOnRefreshListener {
            getData()
        }
        mMaterialHeader = mRefreshLayout.refreshHeader as MaterialHeader?
        //打开下拉刷新区域块背景:
        mMaterialHeader?.setShowBezierWave(true)
        //设置下拉刷新主题颜色
        mRefreshLayout.setPrimaryColorsId(R.color.color_light_black, R.color.color_title_bg)
    }

    private fun getData() {
        val disposable = RetrofitManager.service.getHotWord()
                .compose(SchedulerUtils.ioToMain())
                .subscribe({ string ->

                }, { throwable ->
                    showError(ExceptionHandle.handleException(throwable), ExceptionHandle.errorCode)
                })

        addSubscription(disposable)
    }

    override fun lazyLoad() {
    }

    companion object {
        fun getInstance(title: String): ShopFragment {
            val fragment = ShopFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }
}