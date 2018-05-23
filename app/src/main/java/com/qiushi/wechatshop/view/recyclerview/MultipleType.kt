package com.qiushi.wechatshop.view.recyclerview

interface MultipleType<in T> {
    fun getLayoutId(item: T, position: Int): Int
}