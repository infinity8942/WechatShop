package com.qiushi.wechatshop.test

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.view.recyclerview.MultipleType
import com.qiushi.wechatshop.view.recyclerview.ViewHolder
import com.qiushi.wechatshop.view.recyclerview.adapter.BaseAdapter
import com.qiushi.wechatshop.view.recyclerview.adapter.OnItemLongClickListener

class TestAdapter(context: Context, data: ArrayList<Beauty>, mList: ArrayList<String>)
    : BaseAdapter<Beauty>(context, data, R.layout.item_test) {

    private var mInflater: LayoutInflater? = null
    var mList = mList
    val MAX_HEAD = 1
    val MAX_ICON = 2
    val MAX_ITEM = 3
    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return MAX_HEAD
        }
        if (position == 1) {
            return MAX_ICON
        }
        return MAX_ITEM
    }

    override fun getItemCount(): Int {
        if (mData == null || mData.size == 0) {
            return 2
        } else {
            return mData.size + 2
        }
    }

    init {
        mInflater = LayoutInflater.from(mContext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View? = null
        when (viewType) {
            MAX_HEAD -> view = mInflater?.inflate(R.layout.manager_item_head, parent, false)
            MAX_ICON -> view = mInflater?.inflate(R.layout.manager_item_icon, parent, false)
            MAX_ITEM -> view = mInflater?.inflate(R.layout.manager_item_orther, parent, false)
        }
        return ViewHolder(view!!)
    }



    override fun bindData(holder: ViewHolder, data: Beauty, position: Int) {
        if (getItemViewType(position) == MAX_HEAD) {

        } else if (getItemViewType(position) == MAX_ITEM) {

            ImageHelper.loadImage(mContext, holder.getView(R.id.iv_shop), mData.get(position -2).picUrl, DensityUtils.dp2px(93f),DensityUtils.dp2px(94f),10f)
//            holder.setText(R.id.tv_title, mData.get(position - 2).title)

        } else {
            //设置 列表结构
            var mRecyclerview = holder.getView<RecyclerView>(R.id.mRecyclerView)
            mRecyclerview.layoutManager = linearLayoutManager
            mRecyclerview.adapter =mAdapter
        }

    }


    private val linearLayoutManager by lazy {
        GridLayoutManager(context, 4)
    }

    private val mAdapter by lazy {
        MainGridAdapter(context!!, mList)
    }

}