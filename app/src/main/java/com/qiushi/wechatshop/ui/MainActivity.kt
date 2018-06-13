package com.qiushi.wechatshop.ui

import android.support.v4.app.Fragment
import android.view.KeyEvent
import com.flyco.tablayout.listener.CustomTabEntity
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.ui.manage.ManageFragment
import com.qiushi.wechatshop.ui.shop.ShopListFragment
import com.qiushi.wechatshop.ui.user.UserFragment
import com.qiushi.wechatshop.util.TabEntity
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity() {

    private val mTabEntities = ArrayList<CustomTabEntity>()
    private val mFragments = ArrayList<Fragment>()

    override fun layoutId(): Int = R.layout.activity_main

    override fun init() {
        setSwipeBackEnable(false)
        mTabEntities.add(TabEntity("我的店", R.mipmap.ic_manage_selected, R.mipmap.ic_manage))
        mTabEntities.add(TabEntity("串门", R.mipmap.ic_shop_selected, R.mipmap.ic_shop))
        mTabEntities.add(TabEntity("我的", R.mipmap.ic_mine_selected, R.mipmap.ic_mine))
        mFragments.add(ManageFragment.getInstance())
        mFragments.add(ShopListFragment.getInstance())
        mFragments.add(UserFragment.getInstance())
        navigation.setTabData(mTabEntities, this, R.id.fl_container, mFragments)
        navigation.currentTab = 0
    }

    override fun getData() {
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