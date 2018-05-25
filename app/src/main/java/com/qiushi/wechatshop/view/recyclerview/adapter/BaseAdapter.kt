package com.qiushi.wechatshop.view.recyclerview.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.qiushi.wechatshop.view.recyclerview.MultipleType
import com.qiushi.wechatshop.view.recyclerview.ViewHolder

abstract class BaseAdapter<T>(var mContext: Context, var mData: ArrayList<T>,
                              private var mLayoutId: Int) : RecyclerView.Adapter<ViewHolder>() {
    private var mInflater: LayoutInflater? = null
    private var mTypeSupport: MultipleType<T>? = null

    private var mItemClickListener: OnItemClickListener? = null
    private var mItemLongClickListener: OnItemLongClickListener? = null

    init {
        mInflater = LayoutInflater.from(mContext)
    }

    //多布局
    constructor(context: Context, data: ArrayList<T>, typeSupport: MultipleType<T>) : this(context, data, -1) {
        this.mTypeSupport = typeSupport
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mTypeSupport != null) {
            mLayoutId = viewType
        }
        val view = mInflater?.inflate(mLayoutId, parent, false)
        return ViewHolder(view!!)
    }

    override fun getItemViewType(position: Int): Int {
        return mTypeSupport?.getLayoutId(mData[position], position)
                ?: super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindData(holder, mData[position], position)

        mItemClickListener?.let {
            holder.itemView.setOnClickListener { mItemClickListener!!.onItemClick(mData[position], position) }
        }
        mItemLongClickListener?.let {
            holder.itemView.setOnLongClickListener { mItemLongClickListener!!.onItemLongClick(mData[position], position) }
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.mItemClickListener = itemClickListener
    }

    fun setOnItemLongClickListener(itemLongClickListener: OnItemLongClickListener) {
        this.mItemLongClickListener = itemLongClickListener
    }

    //绑定数据
    protected abstract fun bindData(holder: ViewHolder, data: T, position: Int)

    //填充数据
    fun setData(itemList: ArrayList<T>) {
        mData.clear()
        mData = itemList
        notifyDataSetChanged()
    }

    //追加数据
    fun addData(itemList: ArrayList<T>) {
        this.mData.addAll(itemList)
        notifyItemRangeChanged(mData.size - itemList.size, itemList.size)
    }
}