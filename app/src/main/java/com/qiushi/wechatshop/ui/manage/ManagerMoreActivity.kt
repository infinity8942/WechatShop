package com.qiushi.wechatshop.ui.manage

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.MenuInfo
import com.qiushi.wechatshop.model.More
import com.qiushi.wechatshop.model.Notifycation
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.RxBus
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_manager_more.*

class ManagerMoreActivity : BaseActivity() {

    var isEdit: Boolean = false
    override fun layoutId(): Int = R.layout.activity_manager_more
    var onList = ArrayList<MenuInfo>()
    var offList = ArrayList<MenuInfo>()
    override fun init() {
        StatusBarUtil.immersive(this)
        StatusBarUtil.setPaddingSmart(this, toolbar1)
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
        ToolsAlwayAdapter()
    }

    private val noUserMnager by lazy {
        GridLayoutManager(this, 4)
    }
    private val noUserGrideAdapter by lazy {
        ToolsDownAdapter()
    }

    /**
     * 请求接口数据
     */
    override fun getData() {
        val subscribeWith: BaseObserver<More> = RetrofitManager.service.getMore(User.getCurrent().shop_id)
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
            startActivity(context, Intent(context, ManagerMoreActivity::class.java), null)
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
                val data = adapter.getItem(position) as MenuInfo
                when (view.id) {
                    R.id.iv_remove -> {
                        useGrideAdapter.setIsOnclick(false)
                        menuMore(User.getCurrent().shop_id, data.menu_id, "0")
                    }
                }
            }

    /**
     * 增加点击事件
     */
    private val noUseItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
                val data = adapter.getItem(position) as MenuInfo
                when (view.id) {
                    R.id.iv_add -> {
//                        ToastUtils.showSuccess("拼接id(增加)")
                        noUserGrideAdapter.setIsOnclick(false)
                        menuMore(User.getCurrent().shop_id, data.menu_id, "1")
                    }
                }
            }

    /**
     * 增加或者移除的请求
     */
    private fun menuMore(shopId: Long, menu_id: String, is_del: String) {
        val subscribeWith: BaseObserver<Boolean> = RetrofitManager.service.menuMore(shopId, menu_id, is_del)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        getData()
                        if (t) {
                            //刷新主页界面
                            RxBus.getInstance().post(Notifycation(Constants.ZX_SHOP, 0L))
                        }
                    }

                    override fun onHandleError(error: Error) {
                    }
                })
        addSubscription(subscribeWith)
    }
}