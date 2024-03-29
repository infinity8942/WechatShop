package com.qiushi.wechatshop.ui.manage

import android.content.Context
import android.content.Intent
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Goods
import com.qiushi.wechatshop.model.Notifycation
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.net.BaseResponse
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.net.exception.ErrorStatus
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.RxBus
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.util.web.WebActivity
import com.qiushi.wechatshop.view.search.MaterialSearchView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_manager_goods.*

class ManagerGoodsActivity : BaseActivity() {
    var type = 0
    private val TYPE_ZD = 1// 置顶
    private val TYPE_DELETE = 2//删除
    private val TYPE_XJ = 3//下架
    private lateinit var notDataView: View
    private lateinit var errorView: View
    var mItemPosition: Int = -1
    var state: Int = -1
    var keyword: String = ""
    private lateinit var mFilterDialog: BottomSheetDialog //底部Dialog

    private var tvAll: TextView? = null
    private var ivAll: ImageView? = null
    private var tvZd: TextView? = null
    private var ivZd: ImageView? = null
    private var tvSj: TextView? = null
    private var ivSj: ImageView? = null
    private var tvXj: TextView? = null
    private var ivXj: ImageView? = null
    private var tvJf: TextView? = null
    private var ivJf: ImageView? = null
    private var tvYhj: TextView? = null
    private var ivYhj: ImageView? = null
    private var tvStock: TextView? = null
    private var ivStock: ImageView? = null
    private var tvClose: TextView? = null

    private var allLayout: RelativeLayout? = null
    private var zdLayout: RelativeLayout? = null
    private var sjLayout: RelativeLayout? = null
    private var xjLayout: RelativeLayout? = null
    private var jfLayout: RelativeLayout? = null
    private var yhjLayout: RelativeLayout? = null
    private var stockLayout: RelativeLayout? = null
    private var closeLyout: RelativeLayout? = null

    private var distance: Int = 0
    private var isViewVisible = true

    var mShowAnim: AlphaAnimation = AlphaAnimation(0.0f, 1.0f)
    var mHiddenAmin: AlphaAnimation = AlphaAnimation(1.0f, 0.0f)

    /**
     * 整体recyclerview adapter
     */
    private var page = 1
    private val mAdapter by lazy {
        ManagerGoodsAdapter()
    }
    /**
     * 整体recyclerview manager
     */
    private val linearLayoutManager by lazy {
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun layoutId(): Int = R.layout.activity_manager_goods

    override fun init() {
        StatusBarUtil.immersive(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)
        StatusBarUtil.setPaddingSmart(this, search_view)

        mShowAnim.duration = 300
        mHiddenAmin.duration = 300

        search_view.adjustTintAlpha(0.8f)
        search_view.setCloseOnTintClick(true)

        search_view.setSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                if (search_view.currentQuery.isEmpty()) {
                    keyword = ""
                    getData()
                } else {
                    tv_header_title.text = "产品管理"
                }
                search_view.setShouldAnimate(true)
            }

            override fun onSearchViewOpened() {
                search_view.setShouldAnimate(false)
            }
        })

        search_view.setOnItemClickListener { _, _, position, _ ->
            val suggestion = search_view.getSuggestionAtPosition(position)
            search_view.setQuery(suggestion, true)
        }
        search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                keyword = query
                tv_header_title.text = query
                getData()
                return false
            }
        })

        notDataView = layoutInflater.inflate(R.layout.empty_content_view, mRecyclerView.parent as ViewGroup, false)
        notDataView.setOnClickListener { getData() }
        errorView = layoutInflater.inflate(R.layout.empty_network_view, mRecyclerView.parent as ViewGroup, false)
        errorView.setOnClickListener { getData() }

        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.adapter = mAdapter
        mAdapter.onItemChildClickListener = itemChildClickListener
        mRecyclerView.addOnScrollListener(scrollListener)
        mAdapter.setOnItemClickListener { adapter, _, position ->
            goToGoodsDetails(adapter.data[position] as Goods)
        }

        mRefreshLayout.setOnRefreshListener {
            page = 1
            getData()
        }
        mRefreshLayout.setOnLoadMoreListener { getData() }
        setBottomSheet()

        back.setOnClickListener(onclickListener)
        search_bar.setOnClickListener(onclickListener)
        tv_add.setOnClickListener(onclickListener)
        tv_filter.setOnClickListener(onclickListener)
    }

    override fun getData() {
        mItemPosition = -1
        val subscribeWith: BaseObserver<List<Goods>> = RetrofitManager.service
                .getMnagerGoods(User.getCurrent().shop_id, state, keyword, page)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<List<Goods>>() {
                    override fun onHandleSuccess(t: List<Goods>) {
                        if (page == 1) {
                            mAdapter.setNewData(t)
                            mRefreshLayout.finishRefresh(true)
                        } else {
                            mAdapter.addData(t)
                            mRefreshLayout.finishRefresh(true)
                        }
                        if (t == null || t.isEmpty()) {
                            mAdapter.emptyView = notDataView
                        }
                        if (t != null) {
                            if (t.size < Constants.PAGE_NUM) {
                                mRefreshLayout.setEnableLoadMore(false)
                            } else {
                                mRefreshLayout.setEnableLoadMore(true)
                                page++
                            }
                        } else {
                            mRefreshLayout.setEnableLoadMore(false)
                        }
                    }

                    override fun onHandleError(error: Error) {
                        if (page == 1) {
                            mRefreshLayout.finishRefresh(false)
                        } else {
                            mRefreshLayout.finishRefresh(false)
                        }
                        if (mAdapter.itemCount == 0) {
                            if (error.code == ErrorStatus.NETWORK_ERROR) {
                                mAdapter.emptyView = errorView
                            } else {
                                mAdapter.emptyView = notDataView
                            }
                        }
                    }
                })
        addSubscription(subscribeWith)
    }

    override fun getParams(intent: Intent) {
        super.getParams(intent)
        state = intent.getIntExtra("state", -1)
    }

    companion object {
        fun startManagerGoodsActivity(context: Context, state: Int) {
            val intent = Intent(context, ManagerGoodsActivity::class.java)
            intent.putExtra("state", state)
            context.startActivity(intent)
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (mItemPosition != -1 && mAdapter != null) {
                mAdapter.getViewByPosition(mRecyclerView, mItemPosition, R.id.layout_shape)!!.visibility = View.GONE
                mItemPosition = -1
            }
        }

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (distance < -ViewConfiguration.get(this@ManagerGoodsActivity).scaledTouchSlop && !isViewVisible) {
                //显示fab
                toggleFilter(true)
                distance = 0
                isViewVisible = true
            } else if (distance > ViewConfiguration.get(this@ManagerGoodsActivity).scaledTouchSlop && isViewVisible) {
                //隐藏
                toggleFilter(false)
                distance = 0
                isViewVisible = false
            }
            if ((dy > 0 && isViewVisible) || (dy < 0 && !isViewVisible))
                distance += dy
        }
    }

    /**
     * 商品条目点击事件
     */
    private val itemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
        val mData = adapter.getItem(position) as Goods
        when (view.id) {
            R.id.iv_more -> {
                val item = adapter.getViewByPosition(mRecyclerView, position, R.id.layout_shape)
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
                setTop(mData.id, type)
                setVisible(position)
            }
            R.id.tv_xj -> {
                type = TYPE_XJ
                setTop(mData.id, type)
                setVisible(position)
            }
            R.id.tv_zd -> {
                type = TYPE_ZD
                setTop(mData.id, type)
                setVisible(position)
            }
            R.id.iv_edit -> {
                setVisible(position)
                AddGoodsActivity.startAddGoodsActivity(this, mData.id)
            }
        }
    }

    private fun setVisible(position: Int) {
        if (mItemPosition != -1 && mAdapter.getViewByPosition(mRecyclerView, position, R.id.layout_shape) != null) {
            mAdapter.getViewByPosition(mRecyclerView, position, R.id.layout_shape)?.visibility = View.GONE
        }
    }

    /**
     * 置顶 下架 删除
     */
    private fun setTop(goods_id: Long, type: Int) {
        val observable: Observable<BaseResponse<Boolean>> = when (type) {
            TYPE_ZD -> RetrofitManager.service.setTop(goods_id)
            TYPE_XJ -> RetrofitManager.service.upShop(goods_id)
            else -> RetrofitManager.service.deleteShop(goods_id)
        }
        val disposable = observable
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        when (type) {
                            TYPE_ZD -> {
                                if (t) {
                                    ToastUtils.showMessage("置顶成功")
                                } else {
                                    ToastUtils.showMessage("取消置顶")
                                }
                                page = 1
                                getData()
                                //通知首页刷新
                                RxBus.getInstance().post(Notifycation(Constants.MANAGER_GOODS, 0L))
                            }
                            TYPE_XJ -> {
                                if (t) {
                                    ToastUtils.showMessage("下架成功")
                                } else {
                                    ToastUtils.showMessage("上架成功")
                                }
                                page = 1
                                getData()
                                //通知首页刷新
                                RxBus.getInstance().post(Notifycation(Constants.MANAGER_GOODS, 0L))
                            }
                            else -> {
                                if (t) {
                                    ToastUtils.showMessage("删除成功")
                                } else {
                                    ToastUtils.showMessage("删除失败")
                                }
                                page = 1
                                getData()
                                //通知首页刷新
                                RxBus.getInstance().post(Notifycation(Constants.MANAGER_GOODS, 0L))
                            }
                        }
                    }

                    override fun onHandleError(error: Error) {
                        ToastUtils.showMessage(error.msg)
                    }
                })
        addSubscription(disposable)
    }

    /**
     * 筛选弹窗
     */
    private fun setBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.filter_dialog_layout, null)

        tvAll = dialogView.findViewById(R.id.tv_all)
        ivAll = dialogView.findViewById(R.id.iv_all)
        tvZd = dialogView.findViewById(R.id.tv_zd)
        ivZd = dialogView.findViewById(R.id.iv_zd)
        tvSj = dialogView.findViewById(R.id.tv_sj)
        ivSj = dialogView.findViewById(R.id.iv_sj)
        tvXj = dialogView.findViewById(R.id.tv_xj)
        ivXj = dialogView.findViewById(R.id.iv_xj)
        tvJf = dialogView.findViewById(R.id.tv_jf)
        ivJf = dialogView.findViewById(R.id.iv_jf)
        tvYhj = dialogView.findViewById(R.id.tv_yhj)
        ivYhj = dialogView.findViewById(R.id.iv_yhj)
        tvStock = dialogView.findViewById(R.id.tv_stock)
        ivStock = dialogView.findViewById(R.id.iv_stock)
        tvClose = dialogView.findViewById(R.id.tv_close)

        allLayout = dialogView.findViewById(R.id.all_layout)
        zdLayout = dialogView.findViewById(R.id.zd_layout)
        sjLayout = dialogView.findViewById(R.id.sj_layout)
        xjLayout = dialogView.findViewById(R.id.xj_layout)
        jfLayout = dialogView.findViewById(R.id.jf_layout)
        yhjLayout = dialogView.findViewById(R.id.yhj_layout)
        stockLayout = dialogView.findViewById(R.id.stock_layout)
        closeLyout = dialogView.findViewById(R.id.close_layout)

        allLayout!!.setOnClickListener(bottomSheetListener)
        zdLayout!!.setOnClickListener(bottomSheetListener)
        sjLayout!!.setOnClickListener(bottomSheetListener)
        xjLayout!!.setOnClickListener(bottomSheetListener)
        jfLayout!!.setOnClickListener(bottomSheetListener)
        yhjLayout!!.setOnClickListener(bottomSheetListener)
        stockLayout!!.setOnClickListener(bottomSheetListener)
        closeLyout!!.setOnClickListener(bottomSheetListener)

        mFilterDialog = BottomSheetDialog(this)
        mFilterDialog.setContentView(dialogView)
    }

    private val bottomSheetListener = View.OnClickListener { v ->
        when (v!!.id) {
            R.id.all_layout -> {
                state = 0
                mFilterDialog.dismiss()
                page = 1
                getData()
            }
            R.id.zd_layout -> {
                state = 1
                mFilterDialog.dismiss()
                page = 1
                getData()
            }
            R.id.sj_layout -> {
                state = 2
                mFilterDialog.dismiss()
                page = 1
                getData()
            }
            R.id.xj_layout -> {
                state = 3
                mFilterDialog.dismiss()
                page = 1
                getData()
            }
            R.id.jf_layout -> {
                state = 4
                mFilterDialog.dismiss()
                page = 1
                getData()
            }
            R.id.yhj_layout -> {
                state = 5
                mFilterDialog.dismiss()
                page = 1
                getData()
            }
            R.id.stock_layout -> {
                state = 6
                mFilterDialog.dismiss()
                page = 1
                getData()
            }
            R.id.close_layout -> mFilterDialog.dismiss()
        }
    }

    private fun showBottomFilterDialog() {
        mFilterDialog.show()
        when (state) {
            0 -> {
                changeSelectedStatus(false, tvYhj, ivYhj)
                changeSelectedStatus(true, tvAll, ivAll)
                changeSelectedStatus(false, tvZd, ivZd)
                changeSelectedStatus(false, tvSj, ivSj)
                changeSelectedStatus(false, tvXj, ivXj)
                changeSelectedStatus(false, tvJf, ivJf)
                changeSelectedStatus(false, tvStock, ivStock)
            }
            1 -> {
                changeSelectedStatus(false, tvYhj, ivYhj)
                changeSelectedStatus(false, tvAll, ivAll)
                changeSelectedStatus(true, tvZd, ivZd)
                changeSelectedStatus(false, tvSj, ivSj)
                changeSelectedStatus(false, tvXj, ivXj)
                changeSelectedStatus(false, tvJf, ivJf)
                changeSelectedStatus(false, tvStock, ivStock)
            }
            2 -> {
                changeSelectedStatus(false, tvYhj, ivYhj)
                changeSelectedStatus(false, tvAll, ivAll)
                changeSelectedStatus(false, tvZd, ivZd)
                changeSelectedStatus(true, tvSj, ivSj)
                changeSelectedStatus(false, tvXj, ivXj)
                changeSelectedStatus(false, tvJf, ivJf)
                changeSelectedStatus(false, tvStock, ivStock)
            }
            3 -> {
                changeSelectedStatus(false, tvYhj, ivYhj)
                changeSelectedStatus(false, tvAll, ivAll)
                changeSelectedStatus(false, tvZd, ivZd)
                changeSelectedStatus(false, tvSj, ivSj)
                changeSelectedStatus(true, tvXj, ivXj)
                changeSelectedStatus(false, tvJf, ivJf)
                changeSelectedStatus(false, tvStock, ivStock)
            }
            4 -> {
                changeSelectedStatus(false, tvYhj, ivYhj)
                changeSelectedStatus(false, tvAll, ivAll)
                changeSelectedStatus(false, tvZd, ivZd)
                changeSelectedStatus(false, tvSj, ivSj)
                changeSelectedStatus(false, tvXj, ivXj)
                changeSelectedStatus(true, tvJf, ivJf)
                changeSelectedStatus(false, tvStock, ivStock)
            }
            5 -> {
                changeSelectedStatus(true, tvYhj, ivYhj)
                changeSelectedStatus(false, tvAll, ivAll)
                changeSelectedStatus(false, tvZd, ivZd)
                changeSelectedStatus(false, tvSj, ivSj)
                changeSelectedStatus(false, tvXj, ivXj)
                changeSelectedStatus(false, tvJf, ivJf)
                changeSelectedStatus(false, tvStock, ivStock)
            }
            6 -> {
                changeSelectedStatus(false, tvYhj, ivYhj)
                changeSelectedStatus(false, tvAll, ivAll)
                changeSelectedStatus(false, tvZd, ivZd)
                changeSelectedStatus(false, tvSj, ivSj)
                changeSelectedStatus(false, tvXj, ivXj)
                changeSelectedStatus(false, tvJf, ivJf)
                changeSelectedStatus(true, tvStock, ivStock)
            }
        }
    }

    private fun changeSelectedStatus(choosen: Boolean, tv: TextView?, iv: ImageView?) {
        if (choosen) {
            tv!!.setTextColor(ContextCompat.getColor(this, R.color.filter_color))
            iv!!.visibility = View.VISIBLE
        } else {
            tv!!.setTextColor(ContextCompat.getColor(this, R.color.gray3))
            iv!!.visibility = View.GONE
        }
    }

    /**
     * 跳转商品详情页
     */
    private fun goToGoodsDetails(goods: Goods) {
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtra(WebActivity.PARAM_TITLE, goods.name)
        intent.putExtra(WebActivity.PARAM_URL, Constants.GOODS_DETAIL + goods.id)
        startActivity(intent)
    }

    private fun toggleFilter(show: Boolean) {
        if (show) {
            layout_filter.startAnimation(mShowAnim)
            layout_filter.visibility = View.VISIBLE
        } else {
            if (layout_filter.visibility == View.VISIBLE) {
                layout_filter.startAnimation(mHiddenAmin)
                layout_filter.visibility = View.INVISIBLE
            }
        }
    }

    override fun accept(t: Notifycation?) {
        super.accept(t)
        when (t!!.code) {
            Constants.ADD_IMG_REFRESH -> {
                page = 1
                getData()
            }
        }
    }

    private val onclickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.back -> finish()
            R.id.search_bar -> search_view.openSearch()
            R.id.tv_filter -> showBottomFilterDialog()
            R.id.tv_add -> {
                setVisible(mItemPosition)
                AddGoodsActivity.startAddGoodsActivity(this, 0)
            }
        }
    }
}