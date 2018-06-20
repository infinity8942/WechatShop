package com.qiushi.wechatshop.ui

import android.content.Intent
import android.support.v4.app.Fragment
import android.view.KeyEvent
import com.orhanobut.logger.Logger
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.ui.manage.ManageFragment
import com.qiushi.wechatshop.ui.shop.ShopListFragment
import com.qiushi.wechatshop.ui.user.UserFragment
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.view.tab.listener.CustomTabEntity
import com.qiushi.wechatshop.view.tab.listener.TabEntity
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

    override fun onNewIntent(intent: Intent?) {
        if (null != intent) {
            if (intent.hasExtra("jumpToShop")) {//跳转“串门”店铺页
                Logger.e("跳转店铺 -> " + intent.getLongExtra("jumpToShop", -1))
                if (navigation.currentTab != 1)
                    navigation.currentTab = 1
                (mFragments[1] as ShopListFragment).selectShop(
                        10088 //TODO 测试数据
//                        intent.getLongExtra("jumpToShop", -1)
                )
            }
            //
        }
    }
}