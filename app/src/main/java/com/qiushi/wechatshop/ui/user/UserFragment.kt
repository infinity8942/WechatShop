package com.qiushi.wechatshop.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseFragment
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.ui.order.OrderActivity
import com.qiushi.wechatshop.ui.user.address.AddressActivity
import com.qiushi.wechatshop.ui.user.setting.SettingActivity
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.web.WebActivity
import com.umeng.analytics.MobclickAgent
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
//        layout_credit.setOnClickListener(this)
//        layout_coupon.setOnClickListener(this)
        layout_cart.setOnClickListener(this)
        layout_address.setOnClickListener(this)

        val user = User.getCurrent()
        if (null != user) {
            ImageHelper.loadAvatar(context, avatar, user.avatar, 48)
            name.text = user.nick
        }
    }

    override fun lazyLoad() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.setting -> startActivity(Intent(activity, SettingActivity::class.java))
            R.id.pay -> goToOrder(1)
            R.id.deliver -> goToOrder(2)
            R.id.finish -> goToOrder(4)
            R.id.all -> goToOrder(0)
            R.id.layout_cart -> {
                val intent = Intent(activity, WebActivity::class.java)
                intent.putExtra(WebActivity.PARAM_TITLE, "我的购物车")
                intent.putExtra(WebActivity.PARAM_URL, Constants.SHOPCART)
                startActivity(intent)
            }
            R.id.layout_address -> startActivity(Intent(activity, AddressActivity::class.java))

//            R.id.layout_credit -> {
//            }
//            R.id.layout_coupon -> {
//            }
        }
    }

    private fun goToOrder(type: Int) {
        val intent = Intent(activity, OrderActivity::class.java)
        intent.putExtra("isManage", false)
        intent.putExtra("type", type)
        startActivity(intent)
    }

    companion object {
        fun getInstance(): UserFragment {
            val fragment = UserFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("UserFragment")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("UserFragment")
    }
}