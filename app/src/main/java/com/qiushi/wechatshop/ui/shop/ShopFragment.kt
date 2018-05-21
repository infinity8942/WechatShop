package com.qiushi.wechatshop.ui.shop

import android.os.Bundle
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.LazyLoadFragment
import kotlinx.android.synthetic.main.fragment_order.*

/**
 * 店铺Fragment
 */
class ShopFragment : LazyLoadFragment() {

    private var mTitle: String? = null

    override fun layoutId(): Int {
        return R.layout.fragment_shop
    }

    override fun getParams(savedInstanceState: Bundle?) {
        arguments?.let {
        }
    }

    override fun initView() {
        text.text = mTitle
    }

    override fun lazyLoad() {
    }

    companion object {
        fun getInstance(title: String): ShopFragment {
            val fragment = ShopFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }
}