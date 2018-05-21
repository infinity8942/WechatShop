package com.qiushi.wechatshop.ui.order

import android.os.Bundle
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.LazyLoadFragment
import kotlinx.android.synthetic.main.fragment_order.*

/**
 * 订单Fragment
 */
class OrderFragment : LazyLoadFragment() {

    private var mTitle: String? = null

    override fun layoutId(): Int {
        return R.layout.fragment_order
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
        fun getInstance(title: String): OrderFragment {
            val fragment = OrderFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }
}
