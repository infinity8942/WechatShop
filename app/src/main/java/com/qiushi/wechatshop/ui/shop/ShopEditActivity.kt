package com.qiushi.wechatshop.ui.shop

import android.app.Activity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.heaven7.android.dragflowlayout.ClickToDeleteItemListenerImpl
import com.heaven7.android.dragflowlayout.DragAdapter
import com.heaven7.android.dragflowlayout.DragFlowLayout
import com.heaven7.android.dragflowlayout.IViewObserver
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.WAppContext
import com.qiushi.wechatshop.model.Shop
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_shop_edit.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

/**
 * Created by Rylynn on 2018-06-13.
 *
 *  店铺编辑页
 *
 *  */
class ShopEditActivity : Activity(), View.OnClickListener {

    private var isEdit = false    //是否处于编辑状态
    private var isChange = false //是否修改过
    private var list = ArrayList<Shop>()
    private var compositeDisposable = CompositeDisposable()//订阅集合

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_edit)
        window.setLayout(DensityUtils.getScreenWidth(), DensityUtils.getScreenHeight() + StatusBarUtil.getStatusBarHeight(this))
        //状态栏透明和间距处理
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPadding(this, toolbar)

        //getData
        list = intent.getSerializableExtra("shops") as ArrayList<Shop>
        drag_flowLayout.setDragAdapter(object : DragAdapter<Shop>() {
            override fun getItemLayoutId(): Int {
                return R.layout.item_drag_flow
            }

            override fun onBindData(itemView: View, dragState: Int, data: Shop) {
                itemView.tag = data

                val tv = itemView.findViewById(R.id.name) as TextView
                tv.text = data.name

                if (data.is_owner) {
                    itemView.findViewById<RelativeLayout>(R.id.bg).setBackgroundResource(0)
                } else {
                    itemView.findViewById<RelativeLayout>(R.id.bg).setBackgroundResource(R.drawable.bg_drag_item)
                }

                itemView.findViewById<ImageView>(R.id.close).visibility =
                        if (dragState != DragFlowLayout.DRAG_STATE_IDLE && !data.is_owner)
                            View.VISIBLE
                        else
                            View.GONE
            }

            override fun getData(itemView: View): Shop {
                return itemView.tag as Shop
            }
        })
        drag_flowLayout.dragItemManager.addItems(list)

        //Listener
        edit.setOnClickListener(this)
        btn_close.setOnClickListener(this)
        commit.setOnClickListener(this)

        et.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addShop()
            }
            false
        }
        drag_flowLayout.setOnDragStateChangeListener { _, dragState ->
            if (dragState == DragFlowLayout.DRAG_STATE_DRAGGING) {
                tv_title.text = "拖拽调整顺序"
                edit.text = "完成"
                isEdit = true
                UIUtil.hideKeyboard(this@ShopEditActivity)
            }
        }
        drag_flowLayout.addViewObserver(object : IViewObserver {
            override fun onAddView(child: View, index: Int) {
            }

            override fun onRemoveView(child: View, index: Int) {
            }
        })
        drag_flowLayout.setOnItemClickListener(object : ClickToDeleteItemListenerImpl(R.id.close) {
            override fun onDeleteSuccess(dfl: DragFlowLayout?, child: View?, data: Any?) {
                list.remove(data as Shop)
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit -> changeEditStatus()
            R.id.btn_close -> closeEdit()
            R.id.commit -> addShop()
        }
    }

    /**
     * 开关编辑状态
     */
    private fun changeEditStatus() {
        isEdit = if (!isEdit) {
            if ((list.size == 0 || (list.size == 1 && !list[0].isDraggable))) {
                ToastUtils.showMessage("暂无可编辑的店铺，请先关注新店铺")
                false
            } else {
                tv_title.text = "拖拽调整顺序"
                edit.text = "完成"
                drag_flowLayout.beginDrag()
                true
            }
        } else {
            editShop()
            tv_title.text = "我的浏览店铺"
            edit.text = "编辑"
            drag_flowLayout.finishDrag()
            false
        }
    }

    /**
     * 添加店铺
     */
    private fun addShop() {
        val code = et.text.toString().trim()
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showMessage("请填写邀请码")
            return
        }
        UIUtil.hideKeyboard(this@ShopEditActivity)
        val disposable = RetrofitManager.service.addShop(code)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Shop>() {
                    override fun onHandleSuccess(t: Shop) {
                        et.setText("")
                        ToastUtils.showMessage("添加成功")

                        if (list.isNotEmpty() && list[0].name == "我的店") {
                            drag_flowLayout.dragItemManager.addItem(1, t)
                            list.add(1, t)
                        } else {
                            drag_flowLayout.dragItemManager.addItem(0, t)
                            list.add(0, t)
                        }

                        if (isEdit) {
                            edit.text = "编辑"
                            drag_flowLayout.finishDrag()
                            isEdit = false
                        }

                        isChange = true
                    }

                    override fun onHandleError(error: Error) {
                        if (error.msg == "店铺不存在") {
                            et.setText("")
                            ToastUtils.showMessage(code + "店铺不存在，请重新输入")
                        } else {
                            ToastUtils.showMessage(error.msg)
                        }
                    }
                })
        compositeDisposable.add(disposable)
    }

    /**
     * 编辑关注的店铺
     */
    private fun editShop() {
        val sb = StringBuilder()
        for (shop in drag_flowLayout.dragItemManager.getItems<Shop>()) {
            sb.append(shop.id).append(",")
        }
        var shopIds = ""
        if (sb.isNotEmpty()) {
            shopIds = sb.substring(0, sb.length - 1)
        }
        val disposable = RetrofitManager.service.editShop(shopIds)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        isChange = true
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showMessage(error.msg)
                    }
                })
        compositeDisposable.add(disposable)
    }

    /**
     * 退出编辑页
     */
    private fun closeEdit() {
        if (isEdit) {//确认关闭提示
            val dialog = AlertDialog.Builder(this@ShopEditActivity)
                    .setMessage("正在编辑，您确定要现在退出吗？")
                    .setPositiveButton("退出") { _, _ ->
                        returnShopData()
                    }.setNegativeButton("取消", null).create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.colorAccent))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(WAppContext.context, R.color.color_more))
        } else {
            returnShopData()
        }
    }

    /**
     * 返回数据
     */
    private fun returnShopData() {
        if (isChange)
            setResult(RESULT_OK)
        finish()
    }

    override fun onBackPressed() {
        closeEdit()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}