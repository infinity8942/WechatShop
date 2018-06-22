package com.qiushi.wechatshop.ui.order

import android.content.Intent
import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.ui.goods.GoodsListActivity
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_add_order.*

/**
 * 添加订单
 */
class AddOrderActivity : BaseActivity() {

    var amount: Int = 1

    override fun layoutId(): Int = R.layout.activity_add_order

    override fun init() {
        StatusBarUtil.immersive(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        back.setOnClickListener(clickListener)
        add.setOnClickListener(clickListener)
    }

    override fun getData() {
    }

    private val clickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.back -> finish()
            R.id.add -> GoodsListActivity.startOrderListActivity(this)
            R.id.commit -> addOrder()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1000 -> {//选择返回
                if (null != data) {
                    val goods = data.getSerializableExtra("goods") as Goods
                }

            }
        }
    }

    private fun addOrder() {//TODO 添加订单接口
//        if (TextUtils.isEmpty(content.text.toString().trim())) {
//            ToastUtils.showWarning("请填写单价")
//            return
//        }
//        val disposable = RetrofitManager.service.feedback(type,
//                content.text.toString().trim(), phone.text.toString().trim())
//                .compose(SchedulerUtils.ioToMain())
//                .subscribeWith(object : BaseObserver<Boolean>() {
//                    override fun onHandleSuccess(t: Boolean) {
//                        if (t) {
//                            ToastUtils.showMessage("添加成功")
//                            this@AddOrderActivity.finish()
//                        }
//                    }
//
//                    override fun onHandleError(error: Error) {
//                        ToastUtils.showError(error.msg)
//                    }
//                })
//        addSubscription(disposable)
    }
}
