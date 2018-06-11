package com.qiushi.wechatshop.view

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Rylynn on 2018-06-11.
 */
internal class SpaceItemDecoration(private var mSpace: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = mSpace
        outRect.right = mSpace
        outRect.bottom = mSpace
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = mSpace
        }
    }
}