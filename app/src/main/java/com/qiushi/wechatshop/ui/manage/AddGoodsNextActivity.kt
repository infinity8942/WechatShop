package com.qiushi.wechatshop.ui.manage

import android.app.Activity
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.text.InputFilter
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.AddGoods
import com.qiushi.wechatshop.model.Notifycation
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.RxBus
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_add_goods_next.*

class AddGoodsNextActivity : BaseActivity() {

    private var isSwitch: Boolean = false
    private var addGoods: AddGoods? = null

    override fun layoutId(): Int = R.layout.activity_add_goods_next

    override fun getParams(intent: Intent) {
        super.getParams(intent)
        addGoods = intent.getSerializableExtra("addGoods") as AddGoods
    }

    override fun init() {
        StatusBarUtil.immersive(this!!)
        StatusBarUtil.setPaddingSmart(this!!, toolbar)

        et_bz.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(100))
        isSwitch = if (addGoods != null) {
            addGoods!!.is_todo
        } else {
            false
        }
        if (addGoods != null && addGoods!!.left_todo != 0) {
            et_count.setText(addGoods!!.left_todo.toString())
        }

        if (addGoods != null && addGoods!!.sales_brief.isNotEmpty()) {
            et_bz.setText(addGoods!!.sales_brief)
        }

        if (isSwitch) {
            iv_switch.setImageResource(R.mipmap.ic_goods_open)
            line.visibility = View.VISIBLE
            countlayout.visibility = View.VISIBLE
        } else {
            line.visibility = View.GONE
            countlayout.visibility = View.GONE
            iv_switch.setImageResource(R.mipmap.ic_goods_close)
        }
        iv_switch.setOnClickListener(onclickListener)
        up_layout.setOnClickListener(onclickListener)
    }

    override fun getData() {
    }

    companion object {
        private lateinit var mContext: Activity

        fun startAddGoodsNextActivity(context: Activity, addGoods: AddGoods) {
            this.mContext = context
            val intent = Intent()
            //获取intent对象
            intent.putExtra("addGoods", addGoods)
            intent.setClass(context, AddGoodsNextActivity::class.java)
            // 获取class是使用::反射
            ContextCompat.startActivity(context, intent, null)
        }
    }

    private val onclickListener = View.OnClickListener { v ->
        when (v!!.id) {
            R.id.iv_switch -> {
                isSwitch = !isSwitch
                if (addGoods != null)
                    addGoods!!.is_todo = isSwitch
                if (isSwitch) {
                    line.visibility = View.VISIBLE
                    countlayout.visibility = View.VISIBLE
                    iv_switch.setImageResource(R.mipmap.ic_goods_open)
                } else {
                    line.visibility = View.GONE
                    countlayout.visibility = View.GONE
                    iv_switch.setImageResource(R.mipmap.ic_goods_close)
                }
            }
            R.id.up_layout -> {
                isDataNull()
            }
        }
    }

    private fun isDataNull() {
        if (et_bz.text.isNotEmpty()) {
            addGoods!!.sales_brief = et_bz.text.toString()
        }
        if (et_count.text.isNotEmpty()) {
            addGoods!!.left_todo = et_count.text.toString().toInt()
        }
        if (addGoods == null) {
            ToastUtils.showError("产品数据未填写，请返回填写")
            return
        }

        if (addGoods!!.is_todo) {
            if (addGoods!!.left_todo == 0) {
                ToastUtils.showError("库存提醒数量未填写")
                return
            }
        }
        //走接口
        putData()
    }

    private fun putData() {
        val toJson = Gson().toJson(addGoods)
        Log.e("tag", "toJson~~~~~~~~~~~~~~~~~$toJson")
        val subscribeWith: BaseObserver<Boolean> = RetrofitManager.service.postGoods(toJson)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        if (t) {
                            ToastUtils.showSuccess("上传成功")
                            RxBus.getInstance().post(Notifycation(Constants.ADD_IMG_REFRESH, 0L))
                            mContext.finish()
                            finish()
                        } else {
                            ToastUtils.showError("上传失败")
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError("上传失败")
                    }
                })
        addSubscription(subscribeWith)
    }
}