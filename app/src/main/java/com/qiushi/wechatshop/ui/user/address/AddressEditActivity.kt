package com.qiushi.wechatshop.ui.user.address

import android.support.v4.content.ContextCompat
import android.view.View
import cn.qqtheme.framework.entity.Province
import cn.qqtheme.framework.picker.AddressPicker
import com.orhanobut.logger.Logger
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.WAppContext
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Buyer
import com.qiushi.wechatshop.util.AddressInitTask
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_address_edit.*
import java.util.*

/**
 * Created by Rylynn on 2018-06-29.
 *
 * 新增、编辑地址
 */
class AddressEditActivity : BaseActivity(), View.OnClickListener {

    var isEdit = false//是否编辑
    var address: Buyer? = null

    override fun layoutId(): Int = R.layout.activity_address_edit

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            address = intent.getSerializableExtra("address") as Buyer
            //TODO

        }
        back.setOnClickListener(this)
        save.setOnClickListener(this)
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.save -> saveAddress()
        }
    }

    /**
     * 弹出地区选择器
     */
    private fun showAddressPicker() {
        AddressInitTask(this, object : AddressInitTask.InitCallback {
            override fun onDataInitFailure() {
                ToastUtils.showError("获取地址失败，请检查网络")
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
                        //忽略直辖市的二级名称
                        if (cityName == "市辖区" || cityName == "市" || cityName == "县") {
                            cityName = ""
                        }
                    }
                    var countyName = ""
                    if (county != null) {
                        countyName = county.name
                    }

                    //TODO 设置地址
                    Logger.e("showAddressPicker = $provinceName $cityName $countyName")
                }
                picker.show()
            }
        }).execute()
    }

    private fun saveAddress() {

    }
}