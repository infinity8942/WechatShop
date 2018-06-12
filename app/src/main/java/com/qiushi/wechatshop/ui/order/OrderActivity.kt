package com.qiushi.wechatshop.ui.order

import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.base.BaseFragmentAdapter
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.TabEntity
import kotlinx.android.synthetic.main.activity_order.*

/**
 * Created by Rylynn on 2018-06-12.
 *
 * 订单管理
 */
class OrderActivity : BaseActivity() {

    private val tabList = ArrayList<String>()
    private val mTabEntities = ArrayList<CustomTabEntity>()
    private val fragments = ArrayList<Fragment>()

    override fun layoutId(): Int {
        return R.layout.activity_order
    }

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        tabList.add("全部")
        tabList.add("代付款")
        tabList.add("待发货")
        tabList.add("已发货")
        tabList.add("已完成")
        mTabEntities.add(TabEntity("全部", 0, 0))
        mTabEntities.add(TabEntity("代付款", 0, 0))
        mTabEntities.add(TabEntity("待发货", 0, 0))
        mTabEntities.add(TabEntity("已发货", 0, 0))
        mTabEntities.add(TabEntity("已完成", 0, 0))
        fragments.add(OrderFragment.getInstance(1))
        fragments.add(OrderFragment.getInstance(2))
        fragments.add(OrderFragment.getInstance(3))
        fragments.add(OrderFragment.getInstance(4))
        fragments.add(OrderFragment.getInstance(5))

        viewpager.adapter = BaseFragmentAdapter(supportFragmentManager, fragments, tabList)
        tab.setTabData(mTabEntities)

        //Listener
        tab.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                viewpager.currentItem = position
            }

            override fun onTabReselect(position: Int) {
            }
        })
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                tab.currentTab = position
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
        })
        viewpager.currentItem = 0
    }

    override fun getData() {
    }
}