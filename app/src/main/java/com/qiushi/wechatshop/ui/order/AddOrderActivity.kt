package com.qiushi.wechatshop.ui.order

import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.ui.goods.GoodsListActivity
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.PriceUtil
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_add_order.*

/**
 * 手动创建订单
 */
class AddOrderActivity : BaseActivity(), View.OnClickListener, TextWatcher {

    private var goodsID: Long = 0L
    private var price: Double = 0.00
    private var amount: Int = 1

    override fun layoutId(): Int = R.layout.activity_add_order

    override fun init() {
        StatusBarUtil.immersive(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        back.setOnClickListener(this)
        add.setOnClickListener(this)
        commit.setOnClickListener(this)
        et_price.addTextChangedListener(object : PriceUtil.MoneyTextWatcher(et_price) {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                calculatePirce()
            }
        })
        et_amount.addTextChangedListener(this)
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.add -> GoodsListActivity.startOrderListActivity(this)
            R.id.commit -> addOrder()
        }
    }

    override fun afterTextChanged(s: Editable?) {
        calculatePirce()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    private fun calculatePirce() {
        if (TextUtils.isEmpty(et_price.text.toString().trim())) {
            price = 0.00
            et_all.setText("")
            return
        }
        if (TextUtils.isEmpty(et_amount.text.toString().trim())) {
            amount = 0
            et_all.setText("")
            return
        }

        price = et_price.text.toString().trim().toDouble()
        amount = et_amount.text.toString().trim().toInt()
        et_all.setText(PriceUtil.doubleTrans(price * amount))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1000 -> {//选择返回
                if (null != data) {
                    val goods = data.getSerializableExtra("goods") as Goods
                    ImageHelper.loadImageWithCorner(this@AddOrderActivity, add, goods.cover, 64, 64, 5.toFloat())
                    et_price.setText(PriceUtil.doubleTrans(goods.price))
                    calculatePirce()
                    goodsID = goods.id
                }
            }
        }
    }

    /**
     * 手动添加订单
     */
    private fun addOrder() {
        if (goodsID == 0L) {
            ToastUtils.showWarning("请选择产品")
            return
        }

        if (TextUtils.isEmpty(et_price.text.toString().trim())) {
            ToastUtils.showWarning("请填写单价")
            return
        }

        if (price == 0.00) {
            ToastUtils.showWarning("单价不能为0")
            return
        }

        if (TextUtils.isEmpty(et_amount.text.toString().trim())) {
            ToastUtils.showWarning("请填写产品数量")
            return
        }

        if (amount == 0) {
            ToastUtils.showWarning("产品数量不能为0")
            return
        }
        val disposable = RetrofitManager.service.addOrder(
                User.getCurrent().shop_id, goodsID,
                et_des.text.toString().trim(),
                price, amount)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        if (t) {
                            ToastUtils.showMessage("添加成功")
                            setResult(RESULT_OK)
                            this@AddOrderActivity.finish()
                        }
                    }

                    override fun onHandleError(error: com.qiushi.wechatshop.net.exception.Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
    }
}