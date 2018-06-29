package com.qiushi.wechatshop.ui.moments

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.base.BaseFragmentAdapter
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.view.tab.listener.CustomTabEntity
import com.qiushi.wechatshop.view.tab.listener.OnTabSelectListener
import com.qiushi.wechatshop.view.tab.listener.TabEntity
import kotlinx.android.synthetic.main.activity_moments.*

/**
 * Created by Rylynn on 2018-06-14.
 *
 * 素材管理
 */
class MomentsActivity : BaseActivity(), View.OnClickListener {

    private val tabList = ArrayList<String>()
    private val mTabEntities = ArrayList<CustomTabEntity>()
    private val fragments = ArrayList<Fragment>()

    override fun layoutId(): Int = R.layout.activity_moments

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        tabList.add("产品素材")
        tabList.add("鸡汤素材")
        tabList.add("海报素材")
        mTabEntities.add(TabEntity("产品素材", 0, 0))
        mTabEntities.add(TabEntity("鸡汤素材", 0, 0))
        mTabEntities.add(TabEntity("海报素材", 0, 0))
        fragments.add(MomentsFragment.getInstance(1))
        fragments.add(MomentsFragment.getInstance(2))
        fragments.add(MomentsFragment.getInstance(3))

        viewpager.offscreenPageLimit = 3
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

        back.setOnClickListener(this)
        add.setOnClickListener(this)
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.add -> startActivity(Intent(this, MomentsTypeActivity::class.java))
        }
    }
}