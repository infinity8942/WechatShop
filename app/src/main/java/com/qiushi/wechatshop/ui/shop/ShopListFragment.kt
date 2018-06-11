package com.qiushi.wechatshop.ui.shop

import android.support.v4.app.Fragment
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.base.BaseFragmentAdapter
import com.qiushi.wechatshop.model.Shop
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_shop_list.*

/**
 * 串门Fragment
 */
class ShopListFragment : BaseFragment() {

    private val tabList = ArrayList<String>()
    private val fragments = ArrayList<Fragment>()

    override fun getLayoutId(): Int = R.layout.fragment_shop_list

    override fun initView() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(activity!!)
        StatusBarUtil.setPaddingSmart(context!!, toolbar)

        //test
        tabList.add("我的店")
        tabList.add("店铺1")
        tabList.add("店铺2")
        fragments.add(ShopFragment.getInstance(1))
        fragments.add(ShopFragment.getInstance(2))
        fragments.add(ShopFragment.getInstance(3))

        viewpager.adapter = BaseFragmentAdapter(childFragmentManager, fragments, tabList)
        tab.setViewPager(viewpager)
    }

    override fun lazyLoad() {
        val disposable = RetrofitManager.service.shopList()
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<ArrayList<Shop>>() {
                    override fun onHandleSuccess(t: ArrayList<Shop>) {
                        for (i in t) {

                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    companion object {
        fun getInstance(): ShopListFragment {
            return ShopListFragment()
        }
    }
}