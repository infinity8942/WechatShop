package com.qiushi.wechatshop.ui.manage


import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.ShopOrder
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_add_goods.*
import kotlinx.android.synthetic.main.addgoods_header.*
import java.util.ArrayList


class AddGoodsActivity : BaseActivity() {
    override fun layoutId(): Int = R.layout.activity_add_goods
    private var mShopOrderList = ArrayList<ShopOrder>()
    /**
     * 整体recyclerview adapter
     */
    private val mAdapter by lazy {
        AddGoodsAdapter(mShopOrderList)
    }
    /**
     * 整体recyclerview manager
     */
    private val linearLayoutManager by lazy {
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }


    override fun init() {
        StatusBarUtil.immersive(this!!)
        StatusBarUtil.setPaddingSmart(this!!, toolbar)
        var mShopOrder1 = ShopOrder(0, "测试老虎7", Constants.GOOD7, 8, false)
        var mShopOrder2 = ShopOrder(1, "测试老虎7", Constants.GOOD7, 8, false)
        var mShopOrder3 = ShopOrder(1, "测试老虎7", Constants.GOOD7, 8, false)
//        var mShopOrder4 = ShopOrder(8, "测试老虎7", Constants.GOOD7, 8, false)
//        var mShopOrder5 = ShopOrder(8, "测试老虎7", Constants.GOOD7, 8, false)
        mShopOrderList.add(mShopOrder1)
        mShopOrderList.add(mShopOrder2)
        mShopOrderList.add(mShopOrder3)
//        mShopOrderList.add(mShopOrder4)
//        mShopOrderList.add(mShopOrder5)

        mRecyclerView.layoutManager = linearLayoutManager

        if (mShopOrderList != null && mShopOrderList.size > 0) {
            rl_layout.visibility = View.VISIBLE
            fl_addlayout.visibility = View.GONE
            foot_layout.visibility = View.VISIBLE
            mRecyclerView.visibility = View.VISIBLE
            mRecyclerView.adapter = mAdapter
        } else {
            rl_layout.visibility = View.GONE
            foot_layout.visibility = View.GONE
            fl_addlayout.visibility = View.VISIBLE
            mRecyclerView.visibility = View.GONE
        }
    }


    override fun getData() {
    }


    companion object {
        fun startAddGoodsActivity(context: Context) {
            val intent = Intent()
            //获取intent对象
            intent.setClass(context, AddGoodsActivity::class.java)
            // 获取class是使用::反射
            ContextCompat.startActivity(context, intent, null)
        }
    }
}
