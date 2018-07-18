package com.qiushi.wechatshop.ui.user.address

import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import cn.qqtheme.framework.entity.Province
import cn.qqtheme.framework.picker.AddressPicker
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.WAppContext
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Buyer
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.AddressInitTask
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_address_edit.*

/**
 * Created by Rylynn on 2018-06-29.
 *
 * 新增、编辑地址
 */
class AddressEditActivity : BaseActivity(), View.OnClickListener {

    var isEdit = false//是否编辑
    var isDefault = false//是否默认地址
    var buyer: Buyer? = null

    override fun layoutId(): Int = R.layout.activity_address_edit

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            buyer = intent.getSerializableExtra("address") as Buyer

            name.setText(buyer!!.receiver)
            phone.setText(buyer!!.mobile)
            area.text = buyer!!.area
            address.setText(buyer!!.address)

            isDefault = if (buyer!!.is_default == 1) {
                default_address.setBackgroundResource(R.mipmap.btn_open)
                true
            } else {
                default_address.setBackgroundResource(R.mipmap.btn_close)
                false
            }
        }
        back.setOnClickListener(this)
        area.setOnClickListener(this)
        default_address.setOnClickListener(this)
        save.setOnClickListener(this)
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.area -> showAddressPicker()
            R.id.default_address -> {
                isDefault = if (isDefault) {
                    default_address.setBackgroundResource(R.mipmap.btn_close)
                    false
                } else {
                    default_address.setBackgroundResource(R.mipmap.btn_open)
                    true
                }
            }
            R.id.save -> saveAddress()
        }
    }

    /**
     * 弹出地区选择器
     */
    private fun showAddressPicker() {
        AddressInitTask(this, object : AddressInitTask.InitCallback {
            override fun onDataInitFailure() {
                ToastUtils.showMessage("获取地址失败，请检查网络")
            }

            override fun onDataInitSuccess(provinces: ArrayList<Province>?) {
                val picker = AddressPicker(this@AddressEditActivity, provinces)
                picker.setCanceledOnTouchOutside(true)
                picker.setUseWeight(true)
                picker.setSubmitText("确认")
                picker.setSubmitTextColor(ContextCompat.getColor(WAppContext.context, R.color.coupon_yellow))
                picker.setCancelText("取消")
                picker.setCancelTextColor(ContextCompat.getColor(WAppContext.context, R.color.gray1))
                picker.setTitleTextColor(ContextCompat.getColor(WAppContext.context, R.color.color_item_text))
                picker.setTextColor(ContextCompat.getColor(WAppContext.context, R.color.coupon_yellow))
                picker.setLabelTextColor(ContextCompat.getColor(WAppContext.context, R.color.color_item_text))
                picker.setOnAddressPickListener { province, city, county ->
                    val provinceName = province.name
                    var cityName = ""
                    if (city != null) {
                        cityName = city.name
                        if (cityName == "市辖区" || cityName == "市" || cityName == "县") {//忽略直辖市的二级名称
                            cityName = ""
                        }
                    }
                    var countyName = ""
                    if (county != null) {
                        countyName = county.name
                    }

                    area.text = provinceName + cityName + countyName
                }
                picker.show()
            }
        }).execute()
    }

    /**
     * 增加、编辑地址
     */
    private fun saveAddress() {
        if (TextUtils.isEmpty(name.text.toString().trim())) {
            ToastUtils.showMessage("请填写收货人姓名")
            return
        }
        if (TextUtils.isEmpty(phone.text.toString().trim())) {
            ToastUtils.showMessage("请填写手机号码")
            return
        }
        if (TextUtils.isEmpty(area.text.toString().trim())) {
            ToastUtils.showMessage("请选择地区")
            return
        }
        if (TextUtils.isEmpty(address.text.toString().trim())) {
            ToastUtils.showMessage("请填写详细地址")
            return
        }

        val observer = if (isEdit) {//编辑
            RetrofitManager.service.editAddress(name.text.toString().trim(), phone.text.toString().trim(),
                    area.text.toString().trim(), address.text.toString().trim(), if (isDefault) 1 else 0, buyer!!.id)
        } else {//添加
            RetrofitManager.service.addAddress(name.text.toString().trim(), phone.text.toString().trim(),
                    area.text.toString().trim(), address.text.toString().trim(), if (isDefault) 1 else 0)
        }

        val disposable = observer.compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Buyer>() {
                    override fun onHandleSuccess(t: Buyer) {
                        if (null != t) {
                            ToastUtils.showMessage(if (isEdit) "修改成功" else "添加成功")
                            setResult(RESULT_OK)
                            finish()
                        }
                    }

                    override fun onHandleError(error: com.qiushi.wechatshop.net.exception.Error) {
                        ToastUtils.showMessage(error.msg)
                    }
                })
        addSubscription(disposable)
    }
}