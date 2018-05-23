package com.qiushi.wechatshop.ui.user

import android.os.Bundle
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_user.*

/**
 * 用户Fragment
 */
class UserFragment : BaseFragment() {

    private var mTitle: String? = null

    override fun getLayoutId(): Int = R.layout.fragment_user

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