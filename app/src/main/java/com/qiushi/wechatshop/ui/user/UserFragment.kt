package com.qiushi.wechatshop.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_user.*

/**
 * 用户Fragment
 */
class UserFragment : BaseFragment(), View.OnClickListener {

    override fun getLayoutId(): Int = R.layout.fragment_user

    override fun initView() {
        StatusBarUtil.immersive(activity!!, android.R.color.transparent)
        StatusBarUtil.setPaddingSmart(activity!!, placeholder)

        setting.setOnClickListener(this)
        pay.setOnClickListener(this)
        deliver.setOnClickListener(this)
        finish.setOnClickListener(this)
        all.setOnClickListener(this)
        layout_credit.setOnClickListener(this)
        layout_coupon.setOnClickListener(this)
        layout_cart.setOnClickListener(this)
        layout_address.setOnClickListener(this)

        //TODO test
        ImageHelper.loadAvatar(context, avatar, Constants.AVATAR, 48)
        name.text = "尼萌酱"
    }

    override fun lazyLoad() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.setting -> {
                startActivity(Intent(activity, SettingActivity::class.java))
            }
            R.id.pay -> {

            }
            R.id.deliver -> {

            }
            R.id.finish -> {

            }
            R.id.all -> {

            }
            R.id.layout_cart -> {
            }

            R.id.layout_address -> {

            }
            R.id.layout_credit -> {
            }
            R.id.layout_coupon -> {
            }
        }
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