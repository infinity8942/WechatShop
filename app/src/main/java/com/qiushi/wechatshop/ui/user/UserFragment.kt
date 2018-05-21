package com.qiushi.wechatshop.ui.user

import android.os.Bundle
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.LazyLoadFragment
import kotlinx.android.synthetic.main.fragment_order.*

/**
 * 用户Fragment
 */
class UserFragment : LazyLoadFragment() {

    private var mTitle: String? = null

    override fun layoutId(): Int {
        return R.layout.fragment_user
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
        fun getInstance(title: String): UserFragment {
            val fragment = UserFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }
}