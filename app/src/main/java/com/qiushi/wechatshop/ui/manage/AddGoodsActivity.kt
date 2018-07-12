package com.qiushi.wechatshop.ui.manage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import cnn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.AddGoods
import com.qiushi.wechatshop.model.Content
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.*
import com.qiushi.wechatshop.util.oss.OnUploadListener
import com.qiushi.wechatshop.util.oss.UploadManager
import com.qiushi.wechatshop.util.permission.HiPermission
import com.qiushi.wechatshop.util.permission.PermissionCallback
import com.qiushi.wechatshop.util.permission.PermissionItem
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_add_goods.*
import kotlinx.android.synthetic.main.addgoods_header.*
import kotlinx.android.synthetic.main.next_layout.*
import java.io.File

class AddGoodsActivity : BaseActivity() {
    var goods_id: Long = 0
    var isBg: Boolean = false
    var addGoods = AddGoods()
    var addContentList = ArrayList<Content>()
    var contentList = ArrayList<Content>()
    private var mHandler = Handler()
    var size: Int = 0
    /**
     * 整体recyclerview adapter
     */
    private val mAdapter by lazy {
        AddGoodsAdapter()
    }
    /**
     * 整体recyclerview manager
     */
    private val linearLayoutManager by lazy {
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun layoutId(): Int = R.layout.activity_add_goods

    override fun init() {
        StatusBarUtil.immersive(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)
        UploadManager.getInstance().register(uploadListener)
        mRecyclerView.layoutManager = linearLayoutManager
        mAdapter.bindToRecyclerView(mRecyclerView)

        mAdapter.onItemChildClickListener = itemChildListener
        ic_bg.setOnClickListener(onClickListener)
        back.setOnClickListener(onClickListener)
        rl_next.setOnClickListener(onClickListener)
        et_brief.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(100))
        et_name.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
        et_brief.addTextChangedListener(textWatcherListener)
        price.addTextChangedListener(PriceUtil.MoneyTextWatcher(price))
    }

    private fun isVisible() {
        if (addGoods != null && addGoods.content != null && addGoods.content!!.size > 0) {
            rl_layout.visibility = View.VISIBLE
            fl_addlayout.visibility = View.GONE
            foot_layout.visibility = View.VISIBLE
            mRecyclerView.visibility = View.VISIBLE
            foot_add_img.setOnClickListener(onClickListener)
            foot_add_text.setOnClickListener(onClickListener)
        } else {
            rl_layout.visibility = View.GONE
            foot_layout.visibility = View.GONE
            fl_addlayout.visibility = View.VISIBLE
            mRecyclerView.visibility = View.GONE
            item_add_img.setOnClickListener(onClickListener)
            item_add_text.setOnClickListener(onClickListener)
        }
    }

    override fun getParams(intent: Intent) {
        super.getParams(intent)
        goods_id = intent.getLongExtra("goods_id", 0)
    }

    override fun getData() {
        if (goods_id != null && goods_id != 0.toLong()) {
            val subscribeWith: BaseObserver<AddGoods> = RetrofitManager.service.getGoods(goods_id)
                    .compose(SchedulerUtils.ioToMain())
                    .subscribeWith(object : BaseObserver<AddGoods>() {
                        override fun onHandleSuccess(t: AddGoods) {
                            addGoods = t
                            addGoods.content = t.content
                            isVisible()
                            setData(addGoods)
                            if (addGoods != null && addGoods.content != null && addGoods.content!!.size > 0) {
                                contentList.addAll(addGoods.content!!)
                                //展示数据
                                mAdapter.setNewData(contentList)
                            }
                        }

                        override fun onHandleError(error: Error) {
                        }
                    })
            addSubscription(subscribeWith)
        } else {
            isVisible()
        }
    }

    fun setData(addGoods: AddGoods) {
        if (addGoods.content != null) {
            addGoods.content!!
                    .filter { it.content.isEmpty() }
                    .forEach { size += 1 }
        }
        //背景图
        if (addGoods.cover_url != null && addGoods.cover_url.isNotEmpty()) {
            ImageHelper.loadImageWithCorner(application, ic_bg, addGoods.cover_url, 94, 94,
                    RoundedCornersTransformation(DensityUtils.dp2px(5.toFloat()), 0, RoundedCornersTransformation.CornerType.ALL))
        }
        //名字
        if (addGoods.name != null && addGoods.name.isNotEmpty()) {
            et_name.setText(addGoods.name)
        }
        //描述
        if (addGoods.brief != null && addGoods.brief.isNotEmpty()) {
            et_brief.setText(addGoods.brief)
        }
        //单价
        if (addGoods.price != null && addGoods.price != 0.toDouble()) {
            price.setText(PriceUtil.doubleTrans(addGoods.price))
        }
        //库存
        if (addGoods.stock != null && addGoods.stock != 0) {
            stock.setText(addGoods.stock.toString())
        }
    }

    companion object {
        fun startAddGoodsActivity(context: Context, goods_id: Long) {
            val intent = Intent(context, AddGoodsActivity::class.java)
            intent.putExtra("goods_id", goods_id)
            context.startActivity(intent)
        }
    }

    private val onClickListener = View.OnClickListener { v: View ->
        when (v.id) {
            R.id.back -> finish()
            R.id.foot_add_img -> {
                //进入相册
                if (size < 10) {
                    choicePhotoWrapper(1, Constants.ADDIMG_RESUALT)
                } else {
                    ToastUtils.showError("最多能上传10张图片")
                }

            }
            R.id.item_add_img -> {
                if (size < 10) {
                    choicePhotoWrapper(1, Constants.ADDIMG_ITEM_REQUEST)
                } else {
                    ToastUtils.showError("最多能上传10张图片")
                }
            }
            R.id.item_add_text -> {
                //跳转编辑文本 Activity
                EditTextActivity.startEditTextActivity(this, "")
            }
            R.id.foot_add_text -> {
                EditTextActivity.startEditTextActivity(this, "")
            }
            R.id.ic_bg -> {
                //背景 进入相册
                choicePhotoWrapper(1, Constants.ADDIMG_BG)
            }
            R.id.rl_next -> {
                //判断 数据空值限制
                isDataNull()
            }
        }
    }

    private fun isDataNull() {
        if (User.getCurrent() != null && User.getCurrent().shop_id != null) {
            addGoods.shop_id = User.getCurrent().shop_id
        }
        if (et_brief.text.toString().isNotEmpty()) {
            addGoods.brief = et_brief.text.toString()
        }
        if (et_name.text.toString().isNotEmpty()) {
            addGoods.name = et_name.text.toString()
        }
        if (price.text.toString().isNotEmpty()) {
            addGoods.price = price.text.toString().toDouble()
        }
        if (stock.text.toString().isNotEmpty()) {
            addGoods.stock = stock.text.toString().toInt()
        }
        if (addGoods == null) {
            ToastUtils.showError("未填写数据")
            return
        }
        if (addGoods.name.isEmpty()) {
            ToastUtils.showError("产品名称未写")
            return
        }
        if (addGoods.cover_url.isEmpty() && addGoods.cover.isEmpty()) {
            ToastUtils.showError("封面图未上传")
            return
        }
        if (addGoods.price == 0.toDouble()) {
            ToastUtils.showError("单价未设置")
            return
        }
        if (addGoods.stock == 0) {
            ToastUtils.showError("库存数量为设置")
            return
        }
        if (goods_id != null && goods_id != 0.toLong()) {
            if (contentList == null || contentList.size <= 0) {
                ToastUtils.showError("产品详情未设置")
                return
            }
        } else {
            if (addGoods.content == null || addGoods.content!!.size <= 0) {
                ToastUtils.showError("产品详情未设置")
                return
            }
        }

        if (goods_id != null && goods_id != 0.toLong() && addContentList != null && addContentList.size > 0) {
            addContentList
                    .filter { addGoods != null && addGoods.content != null && !addGoods.content!!.contains(it) }
                    .forEach { addGoods.content!!.add(it) }
        }

        AddGoodsNextActivity.startAddGoodsNextActivity(this, addGoods)
    }

    private fun choicePhotoWrapper(count: Int, resultCode: Int) {

        getPermission()

        val intent = BGAPhotoPickerActivity.IntentBuilder(this)
                .cameraFileDir(File(Environment.getExternalStorageDirectory(), "WechatShop"))
                .maxChooseCount(count) // 图片选择张数的最大值
                .selectedPhotos(null) // 当前已选中的图片路径集合
                .pauseOnScroll(true) // 滚动列表时是否暂停加载图片
                .build()
        startActivityForResult(intent, resultCode)
    }

    /**
     * 相册权限
     */
    private fun getPermission() {
        val permissionItems = ArrayList<PermissionItem>()
        permissionItems.add(PermissionItem(Manifest.permission.CAMERA, "照相机", R.drawable.permission_ic_camera))
        permissionItems.add(PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储", R.drawable.permission_ic_storage))
        HiPermission.create(this).permissions(permissionItems).title("权限申请")
                .filterColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, theme))
                .checkMutiPermission(object : PermissionCallback {
                    override fun onGuarantee(permission: String?, position: Int) {
                    }

                    override fun onDeny(permission: String?, position: Int) {
                        ToastUtils.showError("拒绝权限")
                    }

                    override fun onClose() {
                        ToastUtils.showError("拒绝权限")
                    }

                    override fun onFinish() {
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.ADDIMG_RESUALT -> {
                if (data != null) {
                    isBg = false
                    val selected = BGAPhotoPickerActivity.getSelectedPhotos(data)
                    if (selected != null && selected.size > 0) {
                        //去上传
                        val mFile = File(selected[0])
                        UploadManager.getInstance().add(mFile)
                        showLoading("正在上传,请等待....")
                    }
                }
            }
            Constants.ADDIMG_ITEM_REQUEST -> {
                if (data != null) {
                    isBg = false
                    val selected = BGAPhotoPickerActivity.getSelectedPhotos(data)
                    if (selected != null && selected.size > 0) {
                        //去上传
                        val mFile = File(selected[0])
                        UploadManager.getInstance().add(mFile)
                        showLoading("正在上传,请等待....")
                    }
                }
            }
            Constants.EDIT_TEXT_REQUEST -> {
                val mText = data?.getStringExtra("text")
                if (mText != null && mText != "") {
                    if (goods_id != 0.toLong()) {
                        val content = Content()
                        content.content = mText
                        contentList.add(content)
                        addContentList.add(content)
//                    addGoods.content = contentList
                        isVisibleEdit()
                        mAdapter.setNewData(contentList)
                    } else {
                        val content = Content()
                        content.content = mText
                        contentList.add(content)
                        addGoods.content = contentList
                        isVisible()
                        mAdapter.setNewData(addGoods.content)
                    }

                }
            }
            Constants.ADDIMG_BG -> {
                if (data != null) {
                    isBg = true
                    val selected = BGAPhotoPickerActivity.getSelectedPhotos(data)
                    if (selected != null && selected.size > 0) {
                        val mFile = File(selected[0])
                        UploadManager.getInstance().add(mFile)
                        showLoading("正在上传,请等待....")
                    }
                }
            }
        }
    }

    /**
     * 上传图片的 监听
     */
    private val uploadListener = object : OnUploadListener {
        override fun onFailure(file: File?, error: com.qiushi.wechatshop.util.oss.Error?) {
            dismissLoading()
        }

        override fun onProgress(file: File?, currentSize: Long, totalSize: Long) {
        }

        override fun onSuccess(file: File?, id: Long) {
            mHandler.postDelayed({
                dismissLoading()
                if (isBg) {
                    addGoods.cover = id.toString()
                    //如果是背景 ,显示背景
                    ImageHelper.loadImageWithCorner(application, ic_bg, ("file://" + file!!.path), 94, 94,
                            RoundedCornersTransformation(DensityUtils.dp2px(5.toFloat()), 0, RoundedCornersTransformation.CornerType.ALL))
                } else {
                    if (goods_id != 0.toLong()) {
                        //编辑
                        val content = Content()
                        content.oss_id = id
                        content.img = "file://" + file!!.path
                        contentList.add(content)
                        addContentList.add(content)
//                        addGoods.content = contentList
                        if (file != null && file.path != null) {
                            size += 1
                            isVisibleEdit()
                            mAdapter.setNewData(contentList)
                        }
                    } else {
                        val content = Content()
                        content.oss_id = id
                        content.img = "file://" + file!!.path
                        contentList.add(content)
                        addGoods.content = contentList
                        if (file != null && file.path != null) {
                            size += 1
                            isVisible()
                            mAdapter.setNewData(addGoods.content)
                        }
                    }
                }
            }, 300)

        }
    }

    private val itemChildListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
        when (view?.id) {
            R.id.iv_remove -> {
                if (goods_id != 0.toLong()) {
                    //编辑
                    if (contentList != null && contentList.size > 0 && contentList.size > position) {
                        val removeAt = contentList.removeAt(position)

                        if (addContentList.contains(removeAt)) {
                            addContentList.remove(removeAt)
                        }
//                        for (item in addContentList) {
//                            if (item.id == removeAt.id) {
//                                addContentList.remove(item)
//                            }
//                        }
                        for (item in addGoods.content!!) {
                            if (item.id == removeAt.id) {
                                item.is_del = 1
                            }
                        }
                        if (removeAt.content.isEmpty()) {
                            size -= 1
                        }
                        isVisibleEdit()
                        mAdapter.setNewData(contentList)
                    }

                } else {
                    //新增
                    if (contentList != null && contentList.size > 0 && contentList.size > position) {
                        val removeAt = contentList.removeAt(position)
                        if (removeAt.content.isEmpty()) {
                            size -= 1
                        }
                        addGoods.content = contentList
                        isVisible()
                        mAdapter.setNewData(addGoods.content)
                    }
                }
            }
        }
    }

    /**
     * 编辑时候得展示盒影藏
     */
    private fun isVisibleEdit() {
        if (contentList != null && contentList.size > 0) {
            rl_layout.visibility = View.VISIBLE
            fl_addlayout.visibility = View.GONE
            foot_layout.visibility = View.VISIBLE
            mRecyclerView.visibility = View.VISIBLE
            foot_add_img.setOnClickListener(onClickListener)
            foot_add_text.setOnClickListener(onClickListener)
        } else {
            rl_layout.visibility = View.GONE
            foot_layout.visibility = View.GONE
            fl_addlayout.visibility = View.VISIBLE
            mRecyclerView.visibility = View.GONE
            item_add_img.setOnClickListener(onClickListener)
            item_add_text.setOnClickListener(onClickListener)
        }
    }

    private val textWatcherListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
//            if (s.toString().length < 100) {
//                tv_count.text = "描述(" + s.toString().length.toString() + "/100)"
//            } else {
//                tv_count.text = "描述(" + 100 + "/100)"
//                ToastUtils.showError("最多只能输入100个字")
//            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.toString().length < 100) {
                tv_count.text = "描述(" + s.toString().length.toString() + "/100)"
            } else {
                tv_count.text = "描述(" + 100 + "/100)"
                ToastUtils.showError("最多只能输入100个字")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (textWatcherListener != null && et_brief != null) {
            et_brief.removeTextChangedListener(textWatcherListener)
        }
    }
}