package com.qiushi.wechatshop.ui.shop

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.widget.RelativeLayout
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
class ShopListFragment : BaseFragment() {

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
                    ImageHelper.loadImage(context, cover, shopList[position].cover)
            }
        })
        btn_edit.setOnClickListener {
            val intent = Intent(activity, ShopEditActivity::class.java)
            intent.putExtra("shops", shopList)
            startActivityForResult(intent, 1000)
        }
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
                        if (error.code == 111) {//无店铺时

                        } else {
                            ToastUtils.showError(error.msg)
                        }
                    }
                })
        addSubscription(disposable)
    }

    private fun setDatas() {
        for (shop in shopList) {
            tabList.add(shop.name)
            fragments.add(ShopFragment.getInstance(shop.id))
        }

        viewpager.adapter = BaseFragmentAdapter(childFragmentManager, fragments, tabList)
        tab.setViewPager(viewpager)
        ImageHelper.loadImage(context, cover, shopList[0].cover)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1000 -> {//编辑店铺返回
                if (null != data) {
                    shopList = data.getSerializableExtra("shops") as ArrayList<Shop>
                    tabList.clear()
                    fragments.clear()
                    setDatas()
                }
            }
        }
    }

    companion object {
        fun getInstance(): ShopListFragment {
            return ShopListFragment()
        }
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