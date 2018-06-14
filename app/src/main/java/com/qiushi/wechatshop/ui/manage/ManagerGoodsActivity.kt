package com.qiushi.wechatshop.ui.manage

import android.content.Context
import android.content.Intent

import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.ShopOrder
import com.qiushi.wechatshop.net.BaseResponse
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_manager_goods.*


import java.util.ArrayList


class ManagerGoodsActivity : BaseActivity() {
    var type = 0
    private val TYPE_ZD = 1// 置顶
    private val TYPE_DELETE = 2//删除
    private val TYPE_XJ = 3//下架

    private var mShopOrderList = ArrayList<ShopOrder>()
    var mItemPosition: Int = -1
    /**
     * 整体recyclerview adapter
     */
    private val mAdapter by lazy {
        ManagerGoodsAdapter(mShopOrderList)
    }
    /**
     * 整体recyclerview manager
     */
    private val linearLayoutManager by lazy {
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }


    override fun layoutId(): Int = R.layout.activity_manager_goods

    override fun init() {
        StatusBarUtil.immersive(this!!)
        StatusBarUtil.setPaddingSmart(this!!, toolbar)

        var mShopOrder = ShopOrder(1, "老虎商店", Constants.GOOD0, 1, false)
        var mShopOrder1 = ShopOrder(2, "测试老虎1", Constants.GOOD1, 2, false)
        var mShopOrder2 = ShopOrder(3, "测试老虎2", Constants.GOOD2, 3, false)
        var mShopOrder3 = ShopOrder(4, "测试老虎3", Constants.GOOD3, 4, false)
        var mShopOrder4 = ShopOrder(5, "测试老虎4", Constants.GOOD4, 5, false)
        var mShopOrder5 = ShopOrder(6, "测试老虎5", Constants.GOOD5, 6, false)
        var mShopOrder6 = ShopOrder(7, "测试老虎6", Constants.GOOD6, 7, false)
        var mShopOrder7 = ShopOrder(8, "测试老虎7", Constants.GOOD7, 8, false)
        mShopOrderList.add(mShopOrder)
        mShopOrderList.add(mShopOrder1)
        mShopOrderList.add(mShopOrder2)
        mShopOrderList.add(mShopOrder3)
        mShopOrderList.add(mShopOrder4)
        mShopOrderList.add(mShopOrder5)
        mShopOrderList.add(mShopOrder6)
        mShopOrderList.add(mShopOrder7)

        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.adapter = mAdapter
        mAdapter.onItemChildClickListener = itemChildClickListener
        mRecyclerView.addOnScrollListener(scrollListener)

        tv_add.setOnClickListener(View.OnClickListener { v: View? ->

            AddGoodsActivity.startAddGoodsActivity(this)
        })
    }

    override fun getData() {

    }


    companion object {
        fun startManagerGoodsActivity(context: Context) {
            val intent = Intent()
            //获取intent对象
            intent.setClass(context, ManagerGoodsActivity::class.java)
            // 获取class是使用::反射
            ContextCompat.startActivity(context, intent, null)
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (mItemPosition != -1 && mAdapter != null) {
                mAdapter.getViewByPosition(mRecyclerView, mItemPosition, R.id.layout_shape)!!.visibility = View.GONE
                mItemPosition = -1
            }
        }
    }

    /**
     * 商品条目点击事件
     */
    private val itemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
        var mData = adapter.getItem(position) as ShopOrder
        when (view.id) {
            R.id.iv_more -> {
                var item = adapter.getViewByPosition(mRecyclerView, position, R.id.layout_shape)
                if (mItemPosition == -1) {
                    if (item!!.visibility == View.VISIBLE) {
                        item.visibility = View.GONE
                    } else {
                        item.visibility = View.VISIBLE
                    }
                    mItemPosition = position
                } else {
                    if (mItemPosition == position) {
                        if (item!!.visibility == View.VISIBLE) {
                            item.visibility = View.GONE
                        } else {
                            item.visibility = View.VISIBLE
                        }
                        mItemPosition = position
                    } else {
                        adapter.getViewByPosition(mRecyclerView, mItemPosition, R.id.layout_shape)!!.visibility = View.GONE
                        if (item!!.visibility == View.VISIBLE) {
                            item.visibility = View.GONE
                        } else {
                            item.visibility = View.VISIBLE
                        }
                        mItemPosition = position
                    }
                }
            }
            R.id.tv_delete -> {
                type = TYPE_DELETE
                setTop(mData.id.toLong(), type)
                ToastUtils.showError("删除")
                adapter.getViewByPosition(mRecyclerView, position, R.id.layout_shape)?.visibility = View.GONE
            }
            R.id.tv_xj -> {
                type = TYPE_XJ
                setTop(mData.id.toLong(), type)
                ToastUtils.showError("下架")
                adapter.getViewByPosition(mRecyclerView, position, R.id.layout_shape)?.visibility = View.GONE
            }
            R.id.tv_zd -> {
                type = TYPE_ZD
                ToastUtils.showError("置顶")
                setTop(mData.id.toLong(), type)
                adapter.getViewByPosition(mRecyclerView, position, R.id.layout_shape)?.visibility = View.GONE
            }


        }
    }

    /**
     * 置顶 下架 删除
     */
    private fun setTop(goods_id: Long, type: Int) {
        var observable: Observable<BaseResponse<Boolean>> = when (type) {
            TYPE_ZD -> RetrofitManager.service.setTop(goods_id)
            TYPE_XJ -> RetrofitManager.service.upShop(goods_id)
//            TYPE_DELETE -> RetrofitManager.service.deleteShop(goods_id)
            else -> RetrofitManager.service.deleteShop(goods_id)
        }
        val disposable = observable
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        when (type) {
                            TYPE_ZD -> {
                                if (t) {
                                    ToastUtils.showSuccess("置顶成功")
                                } else {
                                    ToastUtils.showSuccess("取消置顶")

                                }
                            }
                            TYPE_XJ -> {
                                if (t) {
                                    ToastUtils.showSuccess("下架成功")
                                } else {
                                    ToastUtils.showSuccess("上架成功")
                                }
                            }
                            else -> {
                                if (t) {
                                    ToastUtils.showSuccess("删除成功")
                                } else {
                                    ToastUtils.showSuccess("删除失败")
                                }
                            }
                        }

                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
        addSubscription(disposable)
    }
}
