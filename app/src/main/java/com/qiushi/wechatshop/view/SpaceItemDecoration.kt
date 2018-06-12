package com.qiushi.wechatshop.view

import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Rylynn on 2018-06-11.
 *
 * LinearLayoutManager间距
 */
internal class SpaceItemDecoration(private var leftRight: Int, private var topBottom: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager: LinearLayoutManager = parent.layoutManager as LinearLayoutManager
        //竖直方向的
        if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
            if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {//最后一项需要 bottom
                outRect.bottom = topBottom
            }
            outRect.top = topBottom
            outRect.left = leftRight
            outRect.right = leftRight
        } else {
            if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {//最后一项需要right
                outRect.right = leftRight
            }
            outRect.top = topBottom
            outRect.left = leftRight
            outRect.bottom = topBottom
        }
    }
}