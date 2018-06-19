package com.qiushi.wechatshop.ui.moments

import android.content.Intent
import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_moments_type.*

/**
 * Created by Rylynn on 2018-06-14.
 *
 * 选择素材类型
 */
class MomentsTypeActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int {
        return R.layout.activity_moments_type
    }

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)
        back.setOnClickListener(this)
        type1.setOnClickListener(this)
        type2.setOnClickListener(this)
        type3.setOnClickListener(this)
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.type1 -> goToCreateMoments(1)
            R.id.type2 -> goToCreateMoments(2)
            R.id.type3 -> goToCreateMoments(3)
        }
    }

    /**
     * 跳转创建素材页
     */
    private fun goToCreateMoments(type: Int) {
        val intent = Intent(this, CreateMomentsActivity::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
        finish()
    }
}