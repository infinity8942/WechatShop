package com.qiushi.wechatshop.ui.order

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.*
import com.alipay.sdk.app.PayTask
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.orhanobut.logger.Logger
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.GlideApp
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.WAppContext
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.model.Order
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.ui.pay.PayResultActivity
import com.qiushi.wechatshop.util.*
import com.qiushi.wechatshop.util.web.WebActivity
import com.qiushi.wechatshop.wxapi.WXPayEntryActivity
import com.tencent.mm.opensdk.modelpay.PayReq
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_detail.*

/**
 * Created by Rylynn on 2018-06-12.
 *
 * 订单详情
 */
class OrderDetailActivity : BaseActivity(), View.OnClickListener {

    private var isManage: Boolean = true //true店铺订单管理,false用户订单
    private var order: Order? = null
    private var orderID: Long = 0
    private lateinit var mAdapter: OrderGoodsAdapter
    //pay
    private lateinit var mPayDialog: BottomSheetDialog //底部Dialog
    private var priceTv: TextView? = null
    private var close: ImageView? = null
    private var layout_wx: RelativeLayout? = null
    private var layout_ali: RelativeLayout? = null
    private var rb_wx: RadioButton? = null
    private var rb_ali: RadioButton? = null
    private var pay: TextView? = null

    override fun layoutId(): Int = R.layout.activity_order_detail

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        orderID = intent.getLongExtra("id", -1)
        isManage = intent.getBooleanExtra("isManage", true)

        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = OrderGoodsAdapter(false, true)
        mRecyclerView.adapter = mAdapter

        initPayDialog()

        back.setOnClickListener(this)
        copy.setOnClickListener(this)
        phone.setOnClickListener(this)
        action.setOnClickListener(this)
        action1.setOnClickListener(this)
        action2.setOnClickListener(this)
        layout_express.setOnClickListener(this)
        mAdapter.setOnItemClickListener { adapter, _, position ->
            goToGoodsDetails(adapter.data[position] as Goods)
        }

        if (intent.getBooleanExtra("payNow", false))//立即支付
            showPayDialog()
    }

    override fun getData() {
        val disposable = RetrofitManager.service.getOrderDetail(
                User.getCurrent().shop_id, orderID)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Order>() {
                    override fun onHandleSuccess(t: Order) {
                        setOrderData(t)
                        order = t
                    }

                    override fun onHandleError(error: com.qiushi.wechatshop.net.exception.Error) {
                        ToastUtils.showMessage(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    /**
     * 设置订单信息
     */
    private fun setOrderData(t: Order) {
        if (t.type == 1) {//自建订单
            GlideApp.with(this@OrderDetailActivity).load(R.mipmap.ic_custom)
                    .override(DensityUtils.dp2px(24.toFloat()), DensityUtils.dp2px(24.toFloat()))
                    .transform(CircleCrop()).into(logo)
            layout_status.visibility = View.GONE
            layout_express.visibility = View.GONE
            layout_user.visibility = View.GONE
            line_user.visibility = View.GONE
            layout_message.visibility = View.GONE
            layout_phone.visibility = View.GONE
            layout_time1.visibility = View.GONE
            layout_time2.visibility = View.GONE
            action.visibility = View.GONE
            action1.visibility = View.VISIBLE
            action1.text = "删除订单"
            action2.visibility = View.GONE
        } else {
            ImageHelper.loadAvatar(this@OrderDetailActivity, logo, t.shop.logo, 24)
            if (null != t.user) {
                var nameStr = "暂无"
                var mobileStr = "暂无"
                if (!TextUtils.isEmpty(t.user.receiver)) {
                    nameStr = t.user.receiver
                }
                if (!TextUtils.isEmpty(t.user.mobile)) {
                    mobileStr = t.user.mobile
                }
                if (nameStr == "暂无" && mobileStr == "暂无") {
                    name.text = "暂无"
                } else if (nameStr == "暂无") {
                    name.text = mobileStr
                } else if (mobileStr == "暂无") {
                    name.text = nameStr
                } else {
                    name.text = nameStr + "  " + mobileStr
                }
                address.text = if (!TextUtils.isEmpty(t.user.address)) t.user.address else "暂无"
            } else {
                name.text = "暂无"
                address.text = "暂无"
            }

            message.text = "买家留言：" + t.content
            pay_time.text = "付款时间：" + DateUtil.getMillon(t.pay_time)
            when (t.pay_type) {
                1 -> payment.text = "支付方式：微信"
                2 -> payment.text = "支付方式：支付宝"
            }

            deliver_time.text = "发货时间：" + DateUtil.getMillon(t.shipping_time)
            achieve_time.text = "成交时间：" + DateUtil.getMillon(t.shipping_end_time)
        }

        shop.text = t.shop.name

        mAdapter.setIsPass(t.pass)
        mAdapter.setNewData(t.goods)

        amount.text = "共计" + t.num + "件商品"
        price.text = "￥" + PriceUtil.doubleTrans(t.price)
        if (null != priceTv) {
            priceTv!!.text = PriceUtil.doubleTrans(t.price)
        }
        number.text = "订单编号：" + t.numbers
        create_time.text = "创建时间：" + DateUtil.getMillon(t.create_time)
        when (t.status) {
            Constants.READY_TO_PAY -> {
                if (isManage) {
                    status.text = "等待买家付款"
//                    action.text = "提醒支付"
                    action.visibility = View.GONE
                    action1.visibility = View.VISIBLE
                    action1.text = "修改价格"
//                    action2.visibility = View.VISIBLE
//                    action2.text = "删除订单"
                } else {
                    status.text = "等待付款"
                    action.text = "立即付款"
                    action1.visibility = View.VISIBLE
                    action1.text = "取消订单"
                }
                cover.setBackgroundResource(R.mipmap.bg_order_fukuan)
            }
            Constants.PAYED -> {
                if (isManage) {
                    status.text = "买家已付款"
                    action.text = "确认发货"
                    numbers.visibility = View.VISIBLE
                } else {
                    status.text = "等待卖家发货"
                    action.text = "提醒发货"
                }
                cover.setBackgroundResource(R.mipmap.bg_order_shouhuo)
            }
            Constants.DELIVERED -> {
                if (isManage) {
                    status.text = "等待买家收货"
                    action.visibility = View.GONE
                    action1.visibility = View.VISIBLE
                    action1.text = "查看物流"
                } else {
                    status.text = "卖家已发货"
                    action.text = "确认收货"
                    action1.visibility = View.VISIBLE
                    action1.text = "查看物流"
                }
                cover.setBackgroundResource(R.mipmap.bg_order_shouhuo)
            }
            Constants.DONE -> {
                if (isManage) {
                    status.text = "已完成"
                    action.visibility = View.GONE
                    action1.visibility = View.VISIBLE
                    action1.text = "删除订单"
                } else {
                    status.text = "已完成"
                    action.text = "再次购买"
                    action1.visibility = View.VISIBLE
                    action1.text = "删除订单"
                    action2.visibility = View.VISIBLE
                    action2.text = "查看物流"
                }
                cover.setBackgroundResource(R.mipmap.bg_order_finish)
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.phone ->
                if (null != order && null != order!!.user && order!!.type != 1) {
                    Utils.call(this, order!!.user.mobile)
                }
            R.id.copy -> {
                ToastUtils.showMessage("已复制到剪贴板")
                val cm: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.primaryClip = ClipData.newPlainText("Label", number.text.toString().replace("订单编号：", ""))
            }
            R.id.layout_express ->
                if (null != order) {
                    goToExpress(order!!.id)
                }
            R.id.action ->
                when (order!!.status) {
                    Constants.READY_TO_PAY ->
                        if (!isManage) {//立即支付
                            showPayDialog()
                        } else {//提醒支付
//                            if (order!!.remind_pay == 0) {
//                                notifyToPay(order!!.id)
//                            } else {
//                                ToastUtils.showMessage("已发出过提醒，请等待买家支付")
//                            }
                        }
                    Constants.PAYED ->
                        if (isManage) {//标记发货
                            val numbers = numbers.text.toString().trim()
                            if (numbers.isEmpty()) {
                                ToastUtils.showMessage("请填写运单号")
                            } else {
                                markAsDeliver(order!!.id, numbers)
                            }
                        } else {//提醒发货
                            if (order!!.remind_send == 0) {
                                notifyToDeliver(order!!.id)
                            } else {
                                ToastUtils.showMessage("已发出过提醒，请等待卖家发货")
                            }
                        }
                    Constants.DELIVERED ->
                        if (!isManage) {//确认收货
                            markAsDone(order!!.id)
                        }
                    Constants.DONE ->
                        if (!isManage) {//再次购买
                            if (order!!.goods.size == 1) {
                                goToGoodsDetails(order!!.goods[0])
                            } else {
                                val intent = Intent()
                                intent.putExtra("shop_id", order!!.shop.id)
                                setResult(RESULT_OK, intent)
                                finish()
                            }
                        }
                }
            R.id.action1 ->
                when (order!!.status) {
                    Constants.READY_TO_PAY ->
                        if (isManage) {//修改价格
                            showEditPriceDialog(order!!.id)
                        } else {//取消订单
                            cancelOrder(order!!.id)
                        }
                    Constants.DELIVERED -> goToExpress(order!!.id)//查看物流
                    Constants.DONE ->
                        if (isManage) {//删除订单
                            delOrder(order!!.id)
                        } else {//删除订单
                            delOrder(order!!.id)
                        }
                    Constants.CUSTOM -> delOrder(order!!.id)//自建订单，删除订单
                }
            R.id.action2 ->
                when (order!!.status) {
//                    Constants.READY_TO_PAY ->
//                        if (isManage) {//删除订单
//                            delOrder(order!!.id)
//                        }
                    Constants.DONE ->
                        if (!isManage) {//查看物流
                            goToExpress(order!!.id)
                        }
                }
        //pay
            R.id.close -> mPayDialog.dismiss()
            R.id.layout_wx -> {
                rb_wx!!.isChecked = true
                rb_ali!!.isChecked = false
            }
            R.id.layout_ali -> {
                rb_wx!!.isChecked = false
                rb_ali!!.isChecked = true
            }
            R.id.pay -> {
                if (rb_wx!!.isChecked) {
                    getWechatPay(order!!.numbers)
                } else {
                    getAliPay(order!!.numbers)
                }
                mPayDialog.dismiss()
            }
        }
    }

    /**
     * 标记发货
     */
    private fun markAsDeliver(order_id: Long, express_number: String) {
        val disposable = RetrofitManager.service.markAsDeliver(order_id, express_number)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        if (t) {
                            ToastUtils.showMessage("已发货")
                            getData()
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showMessage(error.msg)
                    }
                })
        addSubscription(disposable)
    }

//    /**
//     * 提醒支付
//     */
//    private fun notifyToPay(order_id: Long) {
//        val disposable = RetrofitManager.service.notifyToPay(order_id)
//                .compose(SchedulerUtils.ioToMain())
//                .subscribeWith(object : BaseObserver<Boolean>() {
//                    override fun onHandleSuccess(t: Boolean) {
//                        if (t) {
//                            ToastUtils.showMessage("已发送提醒")
//                            action.text = "已提醒"
//                            action.isEnabled = false
//                        }
//                    }
//
//                    override fun onHandleError(error: Error) {
//                        ToastUtils.showMessage(error.msg)
//                        if (error.code == 1004) {
//                            action.text = "已提醒"
//                            action.isEnabled = false
//                        }
//                    }
//                })
//        addSubscription(disposable)
//    }

    /**
     * 提醒发货
     */
    private fun notifyToDeliver(order_id: Long) {
        val disposable = RetrofitManager.service.notifyToDeliver(order_id)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        if (t) {
                            ToastUtils.showMessage("已发送提醒")
                            action.text = "已提醒"
                            action.isEnabled = false
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showMessage(error.msg)
                        if (error.code == 1004) {
                            action.text = "已提醒"
                            action.isEnabled = false
                        }
                    }
                })
        addSubscription(disposable)
    }

    /**
     * 确认收货
     */
    private fun markAsDone(order_id: Long) {
        val dialog = AlertDialog.Builder(this)
                .setMessage("您要确认收货吗？")
                .setPositiveButton("确认") { _, _ ->
                    val disposable = RetrofitManager.service.markAsDone(order_id)
                            .compose(SchedulerUtils.ioToMain())
                            .subscribeWith(object : BaseObserver<Boolean>() {
                                override fun onHandleSuccess(t: Boolean) {
                                    if (t) {
                                        ToastUtils.showMessage("交易完成")
                                        getData()
                                    }
                                }

                                override fun onHandleError(error: Error) {
                                    ToastUtils.showMessage(error.msg)
                                }
                            })
                    addSubscription(disposable)
                }.setNegativeButton("取消", null).create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.color_more))
    }

    private fun showEditPriceDialog(order_id: Long) {
        val et = EditText(this)
        et.gravity = Gravity.CENTER
        et.inputType = Constants.TYPE_NUMBER_FLAG_DECIMAL
        et.addTextChangedListener(PriceUtil.MoneyTextWatcher(et))

        val dialog = AlertDialog.Builder(this).setView(et)
                .setPositiveButton("修改") { _, _ ->
                    val price = et.text.toString().trim()
                    if (price.isEmpty()) {
                        ToastUtils.showMessage("请填写更改后的价格")
                    } else {
                        editOrderPrice(order_id, price.toDouble())
                    }
                }
                .setNegativeButton("取消", null).create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.color_more))
    }

    /**
     * 修改价格
     */
    private fun editOrderPrice(order_id: Long, price: Double) {
        val disposable = RetrofitManager.service.editOrderPrice(order_id, price)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        if (t) {
                            ToastUtils.showMessage("修改成功")
                            getData()
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showMessage(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    /**
     * 删除订单
     */
    private fun delOrder(order_id: Long) {
        val dialog = AlertDialog.Builder(this)
                .setMessage("您确定要删除该订单吗？")
                .setPositiveButton("删除") { _, _ ->
                    val disposable = RetrofitManager.service.delOrder(order_id)
                            .compose(SchedulerUtils.ioToMain())
                            .subscribeWith(object : BaseObserver<Boolean>() {
                                override fun onHandleSuccess(t: Boolean) {
                                    if (t) {
                                        ToastUtils.showMessage("删除成功")
                                        finish()
                                    }
                                }

                                override fun onHandleError(error: Error) {
                                    ToastUtils.showMessage(error.msg)
                                }
                            })
                    addSubscription(disposable)
                }.setNegativeButton("取消", null).create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.color_more))
    }

    /**
     * 取消订单
     */
    private fun cancelOrder(order_id: Long) {
        val dialog = AlertDialog.Builder(this)
                .setMessage("您确定要取消该订单吗？")
                .setPositiveButton("确定") { _, _ ->
                    val disposable = RetrofitManager.service.cancelOrder(order_id)
                            .compose(SchedulerUtils.ioToMain())
                            .subscribeWith(object : BaseObserver<Boolean>() {
                                override fun onHandleSuccess(t: Boolean) {
                                    if (t) {
                                        ToastUtils.showMessage("已取消")
                                        getData()
                                    }
                                }

                                override fun onHandleError(error: Error) {
                                    ToastUtils.showMessage(error.msg)
                                }
                            })
                    addSubscription(disposable)
                }.setNegativeButton("取消", null).create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.color_more))
    }

    private fun initPayDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_order_pay, null)
        priceTv = dialogView.findViewById(R.id.price)
        close = dialogView.findViewById(R.id.close)
        layout_wx = dialogView.findViewById(R.id.layout_wx)
        layout_ali = dialogView.findViewById(R.id.layout_ali)
        rb_wx = dialogView.findViewById(R.id.rb_wx)
        rb_ali = dialogView.findViewById(R.id.rb_ali)
        pay = dialogView.findViewById(R.id.pay)

        close!!.setOnClickListener(this)
        layout_wx!!.setOnClickListener(this)
        layout_ali!!.setOnClickListener(this)
        pay!!.setOnClickListener(this)

        mPayDialog = BottomSheetDialog(this)
        mPayDialog.setContentView(dialogView)
    }

    private fun showPayDialog() {
        mPayDialog.show()
    }

    /**
     * 微信
     */
    private fun getWechatPay(numbers: String) {
        val disposable = RetrofitManager.service.getWechatPay(numbers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : BaseObserver<PayReq>() {
                    override fun onHandleSuccess(payReq: PayReq) {
                        if (null != payReq) {
                            if (!WXPayEntryActivity.pay(applicationContext, payReq)) {
                                ToastUtils.showMessage("未安装微信客户端")
                            }
                        }
                    }

                    override fun onHandleError(e: Error) {
                        ToastUtils.showMessage(e.msg)
                    }
                })
        addSubscription(disposable)
    }

    /**
     * 支付宝
     */
    private fun getAliPay(numbers: String) {
        val disposable = RetrofitManager.service.getAliPay(numbers)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { s ->
                    val pay = PayTask(this@OrderDetailActivity)
                    Observable.just(pay.payV2(s.data, true))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Map<String, String>>() {
                    override fun onNext(map: Map<String, String>) {
                        Logger.d("aliResp status = " + map["resultStatus"])
                        Logger.d("aliResp result = " + map["result"])
                        Logger.d("aliResp memo = " + map["memo"])

                        when (map["resultStatus"]) {
                            "9000" -> startActivity(Intent(this@OrderDetailActivity, PayResultActivity::class.java))
                            "8000", "6004" -> {
                                ToastUtils.showMessage("支付状态异常")
                                startActivity(Intent(this@OrderDetailActivity, PayResultActivity::class.java))
                            }
                            "4000" -> startActivity(Intent(this@OrderDetailActivity, PayResultActivity::class.java))
                            "5000" -> ToastUtils.showMessage("重复请求")
                            "6001" -> ToastUtils.showMessage("您已取消支付")
                            "6002" -> ToastUtils.showMessage("网络错误")
                            else -> {
                                ToastUtils.showMessage("支付错误")
                                startActivity(Intent(this@OrderDetailActivity, PayResultActivity::class.java))
                            }
                        }//PayResultActivity.SUCCESS
                        //PayResultActivity.FAILED
                        //PayResultActivity.FAILED
                        //PayResultActivity.FAILED
                    }

                    override fun onError(e: Throwable) {
                        if (e != null) {
                            e.printStackTrace()
                            ToastUtils.showMessage(e.message)
                        } else {
                            ToastUtils.showMessage("获取订单信息失败，请重试")
                        }
                    }

                    override fun onComplete() {}
                })
        addSubscription(disposable)
    }

    /**
     * 跳转商品详情页
     */
    private fun goToGoodsDetails(goods: Goods) {
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtra(WebActivity.PARAM_TITLE, goods.name)
        intent.putExtra(WebActivity.PARAM_URL, Constants.GOODS_DETAIL + goods.id)
        startActivity(intent)
    }

    /**
     * 跳转物流H5页
     */
    private fun goToExpress(order_id: Long) {
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtra(WebActivity.PARAM_TITLE, "物流信息")
        intent.putExtra(WebActivity.PARAM_URL, Constants.EXPRESS + order_id)
        startActivity(intent)
    }
}