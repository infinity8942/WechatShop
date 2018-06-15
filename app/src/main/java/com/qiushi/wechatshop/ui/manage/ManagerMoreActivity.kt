package com.qiushi.wechatshop.ui.manage

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
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
import kotlinx.android.synthetic.main.activity_manager_more.*
import java.util.*

class ManagerMoreActivity : BaseActivity() {

    var isEdit: Boolean = false
    override fun layoutId(): Int = R.layout.activity_manager_more

    override fun init() {
        StatusBarUtil.immersive(this!!)
        StatusBarUtil.setPaddingSmart(this!!, toolbar1)

        often_recycler.layoutManager = useGrideManager
        often_recycler.adapter = useGrideAdapter

        //如果接口返回的 不常用工具List为空，则隐藏
        notuse_recycler.layoutManager = noUserMnager
        notuse_recycler.adapter = noUserGrideAdapter

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
                            useGrideAdapter.setNewData(t.on_info)
                            if (t.off_info != null) {
                                line.visibility = View.VISIBLE
                                tv_nouse.visibility = View.VISIBLE
                                noUserGrideAdapter.setNewData(t.off_info)

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
}