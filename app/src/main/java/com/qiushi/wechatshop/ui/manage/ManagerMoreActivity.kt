package com.qiushi.wechatshop.ui.manage

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION_CODES.M
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.Menu
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.MenuInfo
import com.qiushi.wechatshop.model.More
import com.qiushi.wechatshop.model.Tools
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import jp.wasabeef.glide.transformations.internal.FastBlur
import kotlinx.android.synthetic.main.activity_manager_more.*
import java.util.*
import kotlin.collections.ArrayList

class ManagerMoreActivity : BaseActivity() {

    var isEdit: Boolean = false
    override fun layoutId(): Int = R.layout.activity_manager_more
    var onList = ArrayList<MenuInfo>()
    var offList = ArrayList<MenuInfo>()
    var sb = StringBuilder()
    override fun init() {
        StatusBarUtil.immersive(this!!)
        StatusBarUtil.setPaddingSmart(this!!, toolbar1)
        line.visibility = View.GONE
        tv_nouse.visibility = View.GONE
        often_recycler.layoutManager = useGrideManager
        often_recycler.adapter = useGrideAdapter


        useGrideAdapter.onItemChildClickListener = useItemChildClickListener


        //如果接口返回的 不常用工具List为空，则隐藏
        notuse_recycler.layoutManager = noUserMnager
        notuse_recycler.adapter = noUserGrideAdapter

        noUserGrideAdapter.onItemChildClickListener = noUseItemChildClickListener

        tv_edit.setOnClickListener(clickListener)
    }

    private val useGrideManager by lazy {
        GridLayoutManager(this, 4)
    }

    private val useGrideAdapter by lazy {
        ToolsAlwayAdapter(ArrayList())
    }

    private val noUserMnager by lazy {
        GridLayoutManager(this, 4)
    }
    private val noUserGrideAdapter by lazy {
        ToolsDownAdapter(ArrayList())
    }

    /**
     * 请求接口数据
     */
    override fun getData() {
        val subscribeWith: BaseObserver<More> = RetrofitManager.service.getMore(Constants.SHOP_ID)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<More>() {
                    override fun onHandleSuccess(t: More) {
                        if (t != null) {
                            onList = t.on_info
                            useGrideAdapter.setIsOnclick(true)
                            useGrideAdapter.setNewData(t.on_info)
                            noUserGrideAdapter.setIsOnclick(true)
                            noUserGrideAdapter.setNewData(t.off_info)
                            if (t.off_info != null) {
                                offList = t.off_info
                                line.visibility = View.VISIBLE
                                tv_nouse.visibility = View.VISIBLE

                            } else {
                                line.visibility = View.GONE
                                tv_nouse.visibility = View.GONE
                            }
                        }
                    }

                    override fun onHandleError(error: Error) {

                    }

                })
        addSubscription(subscribeWith)
    }

    companion object {
        fun startManagerMoreActivity(context: Context) {
            val intent = Intent()
            //获取intent对象
            intent.setClass(context, ManagerMoreActivity::class.java)
            // 获取class是使用::反射
            startActivity(context, intent, null)
        }
    }

    private var clickListener = View.OnClickListener { v ->
        when (v!!.id) {
            R.id.tv_edit -> {
                isEdit = !isEdit
                if (tv_edit.text == "编辑") {
                    //编辑
                    tv_edit.text = "完成"
                    noUserGrideAdapter.setBackgroud(true)
                    useGrideAdapter.setBackground(true)
                } else if (tv_edit.text == "完成") {
                    //完成
                    finish()
                }
            }
        }
    }

    /**
     * 移除点击事件
     */
    private val useItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { adapter,
                                                        view, position ->
                var data = adapter.getItem(position) as MenuInfo
                when (view.id) {
                    R.id.iv_remove -> {
                        useGrideAdapter.setIsOnclick(false)
                        if (onList.contains(data)) {
                            onList.remove(data)
                            sb.delete(0, sb.length)
                            for (item in onList) {
                                if (sb.isNotEmpty()) {
                                    sb.append(",")
                                }
                                sb.append(item.menu_id)
                            }
                            menuMore(Constants.SHOP_ID, sb.toString())
                        }
                    }
                }
            }

    /**
     * 增加点击事件
     */
    private val noUseItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
                var data = adapter.getItem(position) as MenuInfo
                when (view.id) {
                    R.id.iv_add -> {
//                        ToastUtils.showSuccess("拼接id(增加)")
                        noUserGrideAdapter.setIsOnclick(false)
                        if (!onList.contains(data)) {
                            onList.add(data)
                            sb.delete(0, sb.length)
                            for (item in onList) {
                                if (sb.isNotEmpty()) {
                                    sb.append(",")
                                }
                                sb.append(item.menu_id)
                            }
                            menuMore(Constants.SHOP_ID, sb.toString())
                        }
                    }
                }
            }


    /**
     * 增加或者移除的请求
     */

    fun menuMore(shopId: Long, on_ids: String) {
        val subscribeWith: BaseObserver<Boolean> = RetrofitManager.service.menuMore(shopId, on_ids)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        getData()
                    }

                    override fun onHandleError(error: Error) {

                    }
                })
        addSubscription(subscribeWith)
    }
}