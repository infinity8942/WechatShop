package com.qiushi.wechatshop.ui.shop

import android.os.Bundle
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment

/**
 * 串门Fragment
 */
class ShopFragment : BaseFragment() {

    override fun getLayoutId(): Int = R.layout.fragment_shop

    override fun initView() {
    }

    override fun lazyLoad() {
    }

    companion object {
        fun getInstance(title: String): ShopFragment {
            val fragment = ShopFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }
}