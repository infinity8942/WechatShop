package com.qiushi.wechatshop.view.recyclerview

interface MultipleType<in T> {
    fun getLayoutId( position: Int): Int
}