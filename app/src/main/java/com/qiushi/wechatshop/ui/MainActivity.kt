package com.qiushi.wechatshop.ui

import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.ui.order.OrderFragment
import com.qiushi.wechatshop.ui.shop.ShopFragment
import com.qiushi.wechatshop.ui.user.UserFragment
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private var mShopFragment: ShopFragment? = null
    private var mOrderFragment: OrderFragment? = null
    private var mUserFragment: UserFragment? = null

    override fun layoutId(): Int = R.layout.activity_main

    override fun init() {
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = navigation.menu.getItem(0).itemId
    }

    override fun getData() {
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
            } ?: ShopFragment.getInstance().let {
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
        transaction.commitAllowingStateLoss()
    }

    private fun hideFragments(transaction: FragmentTransaction) {
        mShopFragment?.let { transaction.hide(it) }
        mOrderFragment?.let { transaction.hide(it) }
        mUserFragment?.let { transaction.hide(it) }
    }

    private var mExitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis().minus(mExitTime) <= 2000) {
                finish()
            } else {
                mExitTime = System.currentTimeMillis()
                ToastUtils.showMessage("再按一次退出程序")
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}