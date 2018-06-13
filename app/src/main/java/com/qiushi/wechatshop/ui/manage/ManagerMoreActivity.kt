package com.qiushi.wechatshop.ui.manage

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.GridLayoutManager
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Function
import com.qiushi.wechatshop.model.Tools
import com.qiushi.wechatshop.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_manager_more.*

class ManagerMoreActivity : BaseActivity() {
    private var mToolsList = ArrayList<Tools>()

    override fun layoutId(): Int = R.layout.activity_manager_more

    override fun init() {
        StatusBarUtil.immersive(this!!)
        StatusBarUtil.setPaddingSmart(this!!, toolbar1)

        var mTool1 = Tools(1, "待办事项", Constants.GOOD0)
        var mTool2 = Tools(2, "订单管理", Constants.GOOD1)
        var mTool3 = Tools(3, "素材管理", Constants.GOOD2)
        var mTool4 = Tools(4, "知识库", Constants.GOOD3)
        var mTool5 = Tools(5, "用户管理", Constants.GOOD4)
        var mTool6 = Tools(6, "账户管理", Constants.GOOD5)
        var mTool7 = Tools(7, "优惠卷管理", Constants.GOOD6)

        mToolsList.add(mTool1)
        mToolsList.add(mTool2)
        mToolsList.add(mTool3)
        mToolsList.add(mTool4)
        mToolsList.add(mTool5)
        mToolsList.add(mTool6)
        mToolsList.add(mTool7)

        //假数据

        often_recycler.layoutManager = mNoUseGrideManager
        often_recycler.adapter = mNoUseGrideAdapter

        //如果接口返回的 不常用工具List为空，则隐藏
        notuse_recycler.layoutManager = mGrideManager
        notuse_recycler.adapter = mGrideAdapter
    }

    private val mGrideManager by lazy {
        GridLayoutManager(this, 4)
    }


    private val mGrideAdapter by lazy {
        ToolsAlwayAdapter(mToolsList)
    }

    private val mNoUseGrideManager by lazy {
        GridLayoutManager(this, 4)
    }
    private val mNoUseGrideAdapter by lazy {
        ToolsAlwayAdapter(mToolsList)
    }

    /**
     * 请求接口数据
     */
    override fun getData() {

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


}
