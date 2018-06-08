package com.qiushi.wechatshop.ui.manage

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Function
import com.qiushi.wechatshop.model.MyShop
import com.qiushi.wechatshop.model.ShopOrder
import com.qiushi.wechatshop.test.MainGridAdapter
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper

import com.qiushi.wechatshop.view.recyclerview.MultipleType
import com.qiushi.wechatshop.view.recyclerview.ViewHolder
import com.qiushi.wechatshop.view.recyclerview.adapter.BaseAdapter

class ManagerAdapter(context: Context, goods: ArrayList<ShopOrder>, data: MyShop)
    : BaseAdapter<ShopOrder>(context, goods, data, object : MultipleType<ShopOrder> {


    override fun getLayoutId(item: ShopOrder, position: Int): Int {
        return when (position) {
            0 -> R.layout.manager_item_head
            1 -> R.layout.manager_item_icon
            2 -> R.layout.manager_item_orther
            else -> 1
        }
    }


}) {
    override fun bindData(holder: ViewHolder, data: ShopOrder, position: Int) {
        when (position) {
            0 -> setHeadData(holder, (this!!.mModel as MyShop?)!!)
            1 -> setItemData(holder, (this!!.mModel as MyShop?)!!.function)
            2 -> setOrderData(holder, mData.get(position - 2))
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (position) {
            0 -> return 0
            1 -> return 1

        }
        return 2
    }

    /**
     * 商品条目
     */
    private fun setOrderData(holder: ViewHolder, data: ShopOrder) {
        ImageHelper.loadImage(mContext, holder.getView(R.id.iv_shop), data.cover, DensityUtils.dp2px(93f), DensityUtils.dp2px(94f), 10f)

    }

    private lateinit var mList: ArrayList<Function>

    /**
     * 列表表格
     */
    private fun setItemData(holder: ViewHolder, function: ArrayList<Function>) {
        this.mList = function
        var mRecyclerview = holder.getView<RecyclerView>(R.id.mRecyclerView)
        mRecyclerview.layoutManager = linearLayoutManager
        mRecyclerview.adapter = mAdapter
    }

    /**
     * 头部布局
     */
    private fun setHeadData(holder: ViewHolder, myShop: MyShop) {

    }

    override fun getItemCount(): Int {
        if (mData == null || mData.size == 0) {
            return 2
        } else {
            return mData.size + 2
        }
    }

    private val linearLayoutManager by lazy {
        GridLayoutManager(context, 4)
    }

    private val mAdapter by lazy {
        MainGridAdapter(context!!, mList)
    }

}