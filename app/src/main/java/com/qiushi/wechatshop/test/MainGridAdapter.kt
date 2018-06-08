package com.qiushi.wechatshop.test

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Function
import com.qiushi.wechatshop.view.recyclerview.ViewHolder

class MainGridAdapter(context: Context, mList: ArrayList<Function>) : RecyclerView.Adapter<ViewHolder>() {
    var mList = mList
    private var mInflater: LayoutInflater? = null

    init {
        mInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View? = mInflater?.inflate(R.layout.manager_item_gride, parent, false)
        return ViewHolder(view!!)
    }

    override fun getItemCount(): Int {
        if (mList != null) {
            return mList.size
        } else {
            return 0
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }
}