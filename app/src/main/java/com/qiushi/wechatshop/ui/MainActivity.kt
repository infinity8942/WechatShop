package com.qiushi.wechatshop.ui

import android.content.Intent
import android.support.v4.app.Fragment
import android.view.KeyEvent
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Notifycation
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.ui.login.LoginActivity
import com.qiushi.wechatshop.ui.manage.ManageFragment
import com.qiushi.wechatshop.ui.shop.ShopListFragment
import com.qiushi.wechatshop.ui.user.UserFragment
import com.qiushi.wechatshop.util.CleanLeakUtils
import com.qiushi.wechatshop.util.Push
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
        mTabEntities.add(TabEntity(resources.getString(R.string.title_manage), R.mipmap.ic_manage_selected, R.mipmap.ic_manage))
        mTabEntities.add(TabEntity(resources.getString(R.string.title_shop), R.mipmap.ic_shop_selected, R.mipmap.ic_shop))
        mTabEntities.add(TabEntity(resources.getString(R.string.title_user), R.mipmap.ic_mine_selected, R.mipmap.ic_mine))
        mFragments.add(ManageFragment.getInstance())
        mFragments.add(ShopListFragment.getInstance())
        mFragments.add(UserFragment.getInstance())
        navigation.setTabData(mTabEntities, this, R.id.fl_container, mFragments)
        navigation.currentTab = 0

        Push.enable()
    }

    override fun getData() {
    }

    private var mExitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (navigation.currentTab != 0) {
                navigation.currentTab = 0
            } else {
                if (System.currentTimeMillis().minus(mExitTime) <= 2000) {
                    finish()
                } else {
                    mExitTime = System.currentTimeMillis()
                    ToastUtils.showMessage("再按一次退出程序")
                }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onNewIntent(intent: Intent?) {
        if (null != intent) {
            if (intent.hasExtra("jumpToShop")) {//跳转“串门”店铺页
                if (navigation.currentTab != 1)
                    navigation.currentTab = 1
                (mFragments[1] as ShopListFragment).selectShop(
                        intent.getLongExtra("jumpToShop", -1)
                )
            }
            if (intent.hasExtra("logout")) {//帐号登出
                val isLogout = intent.getBooleanExtra("logout", false)
                if (isLogout) {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }

    fun updateCover(img: String) {
        (mFragments[1] as ShopListFragment).updateCover(img)
    }

    fun setBackgroundColor(color: Int) {
        (mFragments[1] as ShopListFragment).setBackgroundColor(color)
    }

    override fun accept(t: Notifycation?) {
        super.accept(t)
        when (t!!.code) {
            Constants.T_LOGIN -> {
                ToastUtils.showMessage("您的账号已在其它设备登录，请重新登录")
                startActivityForResult(Intent(this, LoginActivity::class.java), 100)
                User.logout()
                finish()
            }
        }
    }

    override fun onDestroy() {
        CleanLeakUtils.fixInputMethodManagerLeak(this@MainActivity)
        super.onDestroy()
    }
}