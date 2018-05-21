package com.qiushi.wechatshop.ui

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentTransaction
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.WActivity
import com.qiushi.wechatshop.ui.order.OrderFragment
import com.qiushi.wechatshop.ui.shop.ShopFragment
import com.qiushi.wechatshop.ui.user.UserFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : WActivity() {

    private var mShopFragment: ShopFragment? = null
    private var mOrderFragment: OrderFragment? = null
    private var mUserFragment: UserFragment? = null

    private var mIndex = 0 //默认起始位置

    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mIndex = savedInstanceState.getInt("currTabIndex")
        }
        super.onCreate(savedInstanceState)

        init()
    }

    private fun init() {
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = navigation.menu.getItem(mIndex).itemId
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                switchFragment(0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                switchFragment(1)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                switchFragment(2)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    /**
     * 切换Fragment
     * @param position 下标
     */
    private fun switchFragment(position: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragments(transaction)
        when (position) {
            0 // 店铺
            -> mShopFragment?.let {
                transaction.show(it)
            } ?: ShopFragment.getInstance("shop").let {
                mShopFragment = it
                transaction.add(R.id.fl_container, it, "shop")
            }
            1 // 订单
            -> mOrderFragment?.let {
                transaction.show(it)
            } ?: OrderFragment.getInstance("order").let {
                mOrderFragment = it
                transaction.add(R.id.fl_container, it, "order")
            }
            2 //我的
            -> mUserFragment?.let {
                transaction.show(it)
            } ?: UserFragment.getInstance("user").let {
                mUserFragment = it
                transaction.add(R.id.fl_container, it, "user")
            }
        }

        mIndex = position
        transaction.commitAllowingStateLoss()
    }

    /**
     * 隐藏所有的Fragment
     * @param transaction transaction
     */
    private fun hideFragments(transaction: FragmentTransaction) {
        mShopFragment?.let { transaction.hide(it) }
        mOrderFragment?.let { transaction.hide(it) }
        mUserFragment?.let { transaction.hide(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (navigation != null) {
            outState.putInt("currTabIndex", mIndex)
        }
    }
}