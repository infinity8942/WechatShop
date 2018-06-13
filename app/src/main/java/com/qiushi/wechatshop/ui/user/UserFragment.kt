package com.qiushi.wechatshop.ui.user

import android.os.Bundle
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_user.*

/**
 * 用户Fragment
 */
class UserFragment : BaseFragment() {

    override fun getLayoutId(): Int = R.layout.fragment_user

    override fun initView() {
        StatusBarUtil.immersive(activity!!, android.R.color.transparent)
        StatusBarUtil.setPaddingSmart(activity!!, placeholder)

        //TODO test
        ImageHelper.loadAvatar(context, avatar, Constants.AVATAR, 48)
        name.text = "尼萌酱"
    }

    override fun lazyLoad() {
    }

    companion object {
        fun getInstance(): UserFragment {
            val fragment = UserFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }
}