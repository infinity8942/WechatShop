package com.qiushi.wechatshop.ui.shop

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.base.BaseFragmentAdapter
import com.qiushi.wechatshop.model.Shop
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_shop_list.*

/**
 * 串门Fragment
 */
class ShopListFragment : BaseFragment(), View.OnClickListener {

    private var shopList = ArrayList<Shop>()
    private val tabList = ArrayList<String>()
    private val fragments = ArrayList<Fragment>()

    override fun getLayoutId(): Int = R.layout.fragment_shop_list

    override fun initView() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(activity!!)
        StatusBarUtil.setPaddingSmart(context!!, toolbar)

        val lpCover = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                (DensityUtils.getScreenWidth() * 0.48).toInt())
        cover.layoutParams = lpCover
        mask.layoutParams = lpCover
        viewpager.offscreenPageLimit = 5

        //Listener
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (!shopList.isEmpty() && position < shopList.size)
                    updateCover(shopList[position].cover)
            }
        })
        btn_edit.setOnClickListener(this)
        empty_view.findViewById<TextView>(R.id.title).text = "您还没有关注任何店铺呦～"
        empty_view.findViewById<Button>(R.id.empty_view_tv).text = "去关注"
        empty_view.findViewById<Button>(R.id.empty_view_tv).setOnClickListener(this)
    }

    override fun lazyLoad() {
        val disposable = RetrofitManager.service.shopList()
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<ArrayList<Shop>>() {
                    override fun onHandleSuccess(t: ArrayList<Shop>) {
                        shopList = t
                        setDatas()
                    }

                    override fun onHandleError(error: Error) {
                        if (error.code == -1001) {//无店铺时
                            StatusBarUtil.setPaddingSmart(context!!, empty_view)
                            empty_view.visibility = View.VISIBLE
                        } else {
                            ToastUtils.showError(error.msg)
                        }
                    }
                })
        addSubscription(disposable)
    }

    private fun setDatas() {
        tabList.clear()
        fragments.clear()
        for (shop in shopList) {
            tabList.add(shop.name)
            fragments.add(ShopFragment.getInstance(shop.id))
        }

        viewpager.adapter = BaseFragmentAdapter(childFragmentManager, fragments, tabList)
        tab.setViewPager(viewpager)
        if (shopList.isNotEmpty()) {
            if (null != shopList[0].cover)
                updateCover(shopList[0].cover)
            empty_view.visibility = View.GONE
        } else {
            empty_view.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.empty_view_tv,
            R.id.btn_edit -> {
                val intent = Intent(activity, ShopEditActivity::class.java)
                intent.putExtra("shops", shopList)
                startActivityForResult(intent, 1000)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1000 -> //编辑店铺返回
                if (null != data) {
                    shopList = data.getSerializableExtra("shops") as ArrayList<Shop>
                    tabList.clear()
                    fragments.clear()
                    setDatas()
                }
        }
    }

    companion object {
        fun getInstance(): ShopListFragment {
            return ShopListFragment()
        }
    }

    /**
     * 更新店铺背景图
     */
    fun updateCover(img: String) {
        ImageHelper.loadImage(context, cover, img)
    }

    /**
     * 跳转置顶店铺页面
     */
    fun selectShop(shopID: Long) {
        shopList.indices
                .filter { shopID == shopList[it].id }
                .forEach { viewpager.currentItem = it }
    }
}