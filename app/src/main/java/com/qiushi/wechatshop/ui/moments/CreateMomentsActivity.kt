package com.qiushi.wechatshop.ui.moments

import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_moments_create.*

/**
 * Created by Rylynn on 2018-06-14.
 *
 * 创建素材
 */
class CreateMomentsActivity : BaseActivity() {

    private var type: Int = 1 //1产品、2鸡汤、3海报

    override fun layoutId(): Int {
        return R.layout.activity_moments_create
    }

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)
        back.setOnClickListener(this)

        type = intent.getIntExtra("type", 1)
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
        }
    }

}