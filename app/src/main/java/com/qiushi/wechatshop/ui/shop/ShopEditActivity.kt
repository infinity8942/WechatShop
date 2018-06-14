package com.qiushi.wechatshop.ui.shop

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.heaven7.android.dragflowlayout.ClickToDeleteItemListenerImpl
import com.heaven7.android.dragflowlayout.DragAdapter
import com.heaven7.android.dragflowlayout.DragFlowLayout
import com.heaven7.android.dragflowlayout.IViewObserver
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.model.Shop
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_shop_edit.*

/**
 * Created by Rylynn on 2018-06-13.
 */
class ShopEditActivity : Activity(), View.OnClickListener {

    private var isEdit = false
    private val list = ArrayList<Shop>()

    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_edit)
        window.setLayout(DensityUtils.getScreenWidth(), DensityUtils.getScreenHeight() + StatusBarUtil.getStatusBarHeight(this))
        //状态栏透明和间距处理
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPadding(this, toolbar)

        edit.setOnClickListener(this)
        btn_close.setOnClickListener(this)
        commit.setOnClickListener(this)

        drag_flowLayout.setOnItemClickListener(object : ClickToDeleteItemListenerImpl(R.id.close) {
            override fun onDeleteSuccess(dfl: DragFlowLayout?, child: View?, data: Any?) {
                //删除成功后的处理
            }
        })
        drag_flowLayout.setDragAdapter(object : DragAdapter<Shop>() {
            override fun getItemLayoutId(): Int {
                return R.layout.item_drag_flow
            }

            override fun onBindData(itemView: View, dragState: Int, data: Shop) {
                itemView.tag = data

                val tv = itemView.findViewById(R.id.name) as TextView
                tv.text = data.name

                if (data.name == "我的店") {
                    itemView.findViewById<RelativeLayout>(R.id.bg).setBackgroundResource(0)
                } else {
                    itemView.findViewById<RelativeLayout>(R.id.bg).setBackgroundResource(R.drawable.bg_drag_item)
                }

                itemView.findViewById<ImageView>(R.id.close).visibility =
                        if (dragState != DragFlowLayout.DRAG_STATE_IDLE && data.name != "我的店")
                            View.VISIBLE
                        else
                            View.GONE
            }

            override fun getData(itemView: View): Shop {
                return itemView.tag as Shop
            }
        })
        drag_flowLayout.addViewObserver(object : IViewObserver {
            override fun onAddView(child: View, index: Int) {
                // Logger.i(TAG, "onAddView", "index = " + index);
            }

            override fun onRemoveView(child: View, index: Int) {
                // Logger.i(TAG, "onRemoveView", "index = " + index);
            }
        })

        //setData
        list.add(Shop("我的店"))
        for (i in 1..5) {
            list.add(Shop("店铺店铺店铺" + i))
        }
        drag_flowLayout.dragItemManager.addItems(list)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit -> {//编辑
                isEdit = if (!isEdit) {
                    edit.text = "完成"
                    drag_flowLayout.beginDrag()
                    true
                } else {
                    edit.text = "编辑"
                    drag_flowLayout.finishDrag()
                    false
                }
            }
            R.id.btn_close -> {
                if (isEdit) {//确认关闭提示

                } else finish()
            }
            R.id.commit -> {
                addShop()
                for (shop in drag_flowLayout.dragItemManager.getItems<Shop>()) {
                }
            }
        }
    }

    private fun addShop() {
        val code = et.text.toString().trim()
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showWarning("请填写邀请码")
            return
        }
        disposable = RetrofitManager.service.addShop(code)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Shop>() {
                    override fun onHandleSuccess(t: Shop) {
                        drag_flowLayout.dragItemManager.addItem(t)
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}