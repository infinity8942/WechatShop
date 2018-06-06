package com.qiushi.wechatshop.ui.shop

import android.os.Bundle
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_order.*

/**
 * 订单Fragment
 */
class OrderFragment : BaseFragment() {

    private var mTitle: String? = null

    override fun getLayoutId(): Int = R.layout.fragment_order

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
