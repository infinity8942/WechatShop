package com.qiushi.wechatshop.view

import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Rylynn on 2018-06-11.
 */
class GridSpaceItemDecoration(private val leftRight: Int, private val topBottom: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val layoutManager: GridLayoutManager = parent.layoutManager as GridLayoutManager
        val lp: GridLayoutManager.LayoutParams = view.layoutParams as GridLayoutManager.LayoutParams
        val childPosition = parent.getChildAdapterPosition(view)
        val spanCount = layoutManager.spanCount

        if (layoutManager.orientation == GridLayoutManager.VERTICAL) {
            if (layoutManager.spanSizeLookup.getSpanGroupIndex(childPosition, spanCount) == 0) {//判断是否在第一排
                outRect.top = topBottom//第一排的需要上面
            }
            outRect.bottom = topBottom;
            //忽略和合并项的问题，只考虑占满和单一的问题
            if (lp.spanSize == spanCount) {//占满
                outRect.left = leftRight
                outRect.right = leftRight
            } else {
                outRect.left = (((spanCount - lp.spanIndex).toFloat()) / spanCount * leftRight).toInt()
                outRect.right = ((leftRight * (spanCount + 1) / spanCount).toFloat() - outRect.left).toInt()
            }
        } else {
            if (layoutManager.spanSizeLookup.getSpanGroupIndex(childPosition, spanCount) == 0) {//第一排的需要left
                outRect.left = leftRight
            }
            outRect.right = leftRight
            //忽略和合并项的问题，只考虑占满和单一的问题
            if (lp.spanSize == spanCount) {//占满
                outRect.top = topBottom
                outRect.bottom = topBottom
            } else {
                outRect.top = (((spanCount - lp.spanIndex).toFloat()) / spanCount * topBottom).toInt()
                outRect.bottom = ((topBottom * (spanCount + 1) / spanCount).toFloat() - outRect.top).toInt()
            }
        }
    }
}