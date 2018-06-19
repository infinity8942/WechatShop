package com.qiushi.wechatshop.ui.order

import android.content.Intent
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.EditText
import android.widget.TextView
import cn.qqtheme.framework.picker.DatePicker
import cn.qqtheme.framework.util.ConvertUtils
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.base.BaseFragmentAdapter
import com.qiushi.wechatshop.util.DateUtil
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.TabEntity
import com.qiushi.wechatshop.util.ToastUtils
import kotlinx.android.synthetic.main.activity_order.*
import java.util.*

/**
 * Created by Rylynn on 2018-06-12.
 *
 * 订单管理、我的订单
 */
class OrderActivity : BaseActivity(), View.OnClickListener {

    private var isManage: Boolean = true
    private val tabList = ArrayList<String>()
    private val mTabEntities = ArrayList<CustomTabEntity>()
    private val fragments = ArrayList<Fragment>()

    //筛选条件
    private lateinit var mFilterDialog: BottomSheetDialog //底部Dialog
    private var sourceAPP: TextView? = null
    private var sourceWeChat: TextView? = null
    private var dateWeek: TextView? = null
    private var dateMonth: TextView? = null
    private var dateYear: TextView? = null
    private var start_time: TextView? = null
    private var end_time: TextView? = null
    private var number: EditText? = null
    private var cancel: TextView? = null
    private var confirm: TextView? = null
    //
    private var source = 0 //订单来源 0不选择、1客户端、2小程序
    private var date = 0 //下单时间 0不选择、1近一周内、2近一月内、3近一年内
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var orderNumber: String = ""

    override fun layoutId(): Int {
        return R.layout.activity_order
    }

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        //getData
        isManage = intent.getBooleanExtra("isManage", true)

        if (isManage)
            layout_order.visibility = View.VISIBLE
        else
            layout_order.visibility = View.GONE

        tabList.add("全部")
        tabList.add("代付款")
        tabList.add("待发货")
        tabList.add("已发货")
        tabList.add("已完成")
        mTabEntities.add(TabEntity("全部", 0, 0))
        mTabEntities.add(TabEntity("代付款", 0, 0))
        mTabEntities.add(TabEntity("待发货", 0, 0))
        mTabEntities.add(TabEntity("已发货", 0, 0))
        mTabEntities.add(TabEntity("已完成", 0, 0))
        fragments.add(OrderFragment.getInstance(1))
        fragments.add(OrderFragment.getInstance(2))
        fragments.add(OrderFragment.getInstance(3))
        fragments.add(OrderFragment.getInstance(4))
        fragments.add(OrderFragment.getInstance(5))

        viewpager.adapter = BaseFragmentAdapter(supportFragmentManager, fragments, tabList)
        tab.setTabData(mTabEntities)
        tab.currentTab = intent.getIntExtra("type", 0)

        initFilterDialog()

        //Listener
        tab.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                viewpager.currentItem = position
            }

            override fun onTabReselect(position: Int) {
            }
        })
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                tab.currentTab = position
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
        })
        viewpager.currentItem = 0

        back.setOnClickListener(this)
        if (isManage) {
            filter.setOnClickListener(this)
            add.setOnClickListener(this)
        }
    }

    private fun initFilterDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_order_filter, null)
        sourceAPP = dialogView.findViewById(R.id.source_app)
        sourceWeChat = dialogView.findViewById(R.id.source_wechat)
        dateWeek = dialogView.findViewById(R.id.date_week)
        dateMonth = dialogView.findViewById(R.id.date_month)
        dateYear = dialogView.findViewById(R.id.date_year)
        start_time = dialogView.findViewById(R.id.start_time)
        end_time = dialogView.findViewById(R.id.end_time)
        number = dialogView.findViewById(R.id.number)
        cancel = dialogView.findViewById(R.id.cancel)
        confirm = dialogView.findViewById(R.id.confirm)

        sourceAPP!!.setOnClickListener(this)
        sourceWeChat!!.setOnClickListener(this)
        dateWeek!!.setOnClickListener(this)
        dateMonth!!.setOnClickListener(this)
        dateYear!!.setOnClickListener(this)
        start_time!!.setOnClickListener(this)
        end_time!!.setOnClickListener(this)
        number!!.setOnClickListener(this)
        cancel!!.setOnClickListener(this)
        confirm!!.setOnClickListener(this)

        mFilterDialog = BottomSheetDialog(this)
        mFilterDialog.setContentView(dialogView)
    }

    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.filter -> {//筛选弹框
                showBottomFilterDialog()
            }
            R.id.add -> {//新增订单
                startActivity(Intent(this, AddOrderActivity::class.java))
            }
        //筛选
            R.id.source_app -> changeSource(1)
            R.id.source_wechat -> changeSource(2)
            R.id.date_week -> changeDate(1)
            R.id.date_month -> changeDate(2)
            R.id.date_year -> changeDate(3)
            R.id.start_time -> chooseTime(true)
            R.id.end_time -> chooseTime(false)
            R.id.cancel -> mFilterDialog.dismiss()
            R.id.confirm -> {
                mFilterDialog.dismiss()
                searchOrder()
            }
        }
    }

    private fun showBottomFilterDialog() {
        mFilterDialog.show()
    }

    private fun changeSource(chooseSource: Int) {
        if (source != chooseSource) {
            source = chooseSource
            when (chooseSource) {
                1 -> {
                    sourceAPP!!.setTextColor(ContextCompat.getColor(this, R.color.coupon_yellow))
                    sourceAPP!!.setBackgroundResource(R.drawable.bg_order_filter_item_selected)
                    sourceWeChat!!.setTextColor(ContextCompat.getColor(this, R.color.gray1))
                    sourceWeChat!!.setBackgroundResource(R.drawable.bg_order_filter_item)
                }
                2 -> {
                    sourceWeChat!!.setTextColor(ContextCompat.getColor(this, R.color.coupon_yellow))
                    sourceWeChat!!.setBackgroundResource(R.drawable.bg_order_filter_item_selected)
                    sourceAPP!!.setTextColor(ContextCompat.getColor(this, R.color.gray1))
                    sourceAPP!!.setBackgroundResource(R.drawable.bg_order_filter_item)
                }
            }
        } else {
            source = 0
            sourceWeChat!!.setTextColor(ContextCompat.getColor(this, R.color.gray1))
            sourceWeChat!!.setBackgroundResource(R.drawable.bg_order_filter_item)
            sourceAPP!!.setTextColor(ContextCompat.getColor(this, R.color.gray1))
            sourceAPP!!.setBackgroundResource(R.drawable.bg_order_filter_item)
        }
    }

    private fun changeDate(chooseDate: Int) {
        if (date != chooseDate) {

            when (chooseDate) {
                1 -> {
                    dateWeek!!.setTextColor(ContextCompat.getColor(this, R.color.coupon_yellow))
                    dateWeek!!.setBackgroundResource(R.drawable.bg_order_filter_item_selected)
                    dateMonth!!.setTextColor(ContextCompat.getColor(this, R.color.gray1))
                    dateMonth!!.setBackgroundResource(R.drawable.bg_order_filter_item)
                    dateYear!!.setTextColor(ContextCompat.getColor(this, R.color.gray1))
                    dateYear!!.setBackgroundResource(R.drawable.bg_order_filter_item)
                }
                2 -> {
                    dateWeek!!.setTextColor(ContextCompat.getColor(this, R.color.gray1))
                    dateWeek!!.setBackgroundResource(R.drawable.bg_order_filter_item)
                    dateMonth!!.setTextColor(ContextCompat.getColor(this, R.color.coupon_yellow))
                    dateMonth!!.setBackgroundResource(R.drawable.bg_order_filter_item_selected)
                    dateYear!!.setTextColor(ContextCompat.getColor(this, R.color.gray1))
                    dateYear!!.setBackgroundResource(R.drawable.bg_order_filter_item)
                }
                3 -> {
                    dateWeek!!.setTextColor(ContextCompat.getColor(this, R.color.gray1))
                    dateWeek!!.setBackgroundResource(R.drawable.bg_order_filter_item)
                    dateMonth!!.setTextColor(ContextCompat.getColor(this, R.color.gray1))
                    dateMonth!!.setBackgroundResource(R.drawable.bg_order_filter_item)
                    dateYear!!.setTextColor(ContextCompat.getColor(this, R.color.coupon_yellow))
                    dateYear!!.setBackgroundResource(R.drawable.bg_order_filter_item_selected)
                }
            }
            date = chooseDate
            //重置下单时间区间
            start_time!!.text = ""
            startTime = 0L
            end_time!!.text = ""
            endTime = 0L
        } else {
            date = 0
            dateWeek!!.setTextColor(ContextCompat.getColor(this, R.color.gray1))
            dateWeek!!.setBackgroundResource(R.drawable.bg_order_filter_item)
            dateMonth!!.setTextColor(ContextCompat.getColor(this, R.color.gray1))
            dateMonth!!.setBackgroundResource(R.drawable.bg_order_filter_item)
            dateYear!!.setTextColor(ContextCompat.getColor(this, R.color.gray1))
            dateYear!!.setBackgroundResource(R.drawable.bg_order_filter_item)
        }
    }

    private fun chooseTime(isStart: Boolean) {
        val picker = DatePicker(this)
        picker.setCanceledOnTouchOutside(true)
        picker.setUseWeight(true)
        picker.setSubmitText("确认")
        picker.setSubmitTextColor(ContextCompat.getColor(this, R.color.coupon_yellow))
        picker.setCancelText("清除")
        picker.setCancelTextColor(ContextCompat.getColor(this, R.color.gray1))
        picker.setTitleTextColor(ContextCompat.getColor(this, R.color.color_item_text))
        picker.setTextColor(ContextCompat.getColor(this, R.color.coupon_yellow))
        picker.setLabelTextColor(ContextCompat.getColor(this, R.color.color_item_text))
        picker.setTopPadding(ConvertUtils.toPx(this, 10f))
        picker.setRangeStart(2018, 1, 1)
        picker.setRangeEnd(2118, 12, 31)
        picker.setSelectedItem(2018, 6, 25)
        picker.setResetWhileWheel(false)
        picker.setOnWheelListener(object : DatePicker.OnWheelListener {
            override fun onYearWheeled(index: Int, year: String) {
                picker.setTitleText(year + "-" + picker.selectedMonth + "-" + picker.selectedDay)
            }

            override fun onMonthWheeled(index: Int, month: String) {
                picker.setTitleText(picker.selectedYear + "-" + month + "-" + picker.selectedDay)
            }

            override fun onDayWheeled(index: Int, day: String) {
                picker.setTitleText(picker.selectedYear + "-" + picker.selectedMonth + "-" + day)
            }
        })
        picker.show()
        picker.cancelButton.setOnClickListener {
            picker.dismiss()
            if (isStart)
                start_time!!.text = ""
            else
                end_time!!.text = ""
        }
        picker.submitButton.setOnClickListener {
            if (isStart) {
                if (endTime != 0L && endTime < DateUtil.getMillis(DateUtil.str2Date(
                                picker.selectedYear + "-" + picker.selectedMonth + "-" + picker.selectedDay,
                                DateUtil.FORMAT_YMD))) {
                    ToastUtils.showError("开始日期不能晚于结束日期")
                } else {
                    picker.dismiss()
                    start_time!!.text = picker.selectedYear + "-" + picker.selectedMonth + "-" + picker.selectedDay
                    startTime = DateUtil.getMillis(DateUtil.str2Date(start_time!!.text.toString(), DateUtil.FORMAT_YMD))
                    //重置下单时间
                    if (date != 0)
                        changeDate(date)
                }
            } else {
                if (startTime != 0L && startTime > DateUtil.getMillis(DateUtil.str2Date(
                                picker.selectedYear + "-" + picker.selectedMonth + "-" + picker.selectedDay,
                                DateUtil.FORMAT_YMD))) {
                    ToastUtils.showError("结束日期不能早于开始日期")
                } else {
                    picker.dismiss()
                    end_time!!.text = picker.selectedYear + "-" + picker.selectedMonth + "-" + picker.selectedDay
                    endTime = DateUtil.getMillis(DateUtil.str2Date(end_time!!.text.toString(), DateUtil.FORMAT_YMD))
                    //重置下单时间
                    if (date != 0)
                        changeDate(date)
                }
            }
        }
    }

    /**
     * 关键字搜索订单
     */
    private fun searchOrder() {
        (fragments[viewpager.currentItem] as OrderFragment).getOrder("")
    }
}