package com.qiushi.wechatshop.ui.manage


import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.AddGoods
import com.qiushi.wechatshop.model.Content
import com.qiushi.wechatshop.model.ShopOrder
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper

import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.util.oss.OnUploadListener
import com.qiushi.wechatshop.util.oss.UploadManager
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

import kotlinx.android.synthetic.main.activity_add_goods.*
import kotlinx.android.synthetic.main.addgoods_header.*
import kotlinx.android.synthetic.main.item_moment.*
import kotlinx.android.synthetic.main.next_layout.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem
import java.io.File

import kotlin.collections.ArrayList


class AddGoodsActivity : BaseActivity() {
    var goods_id: Long = 0
    override fun layoutId(): Int = R.layout.activity_add_goods
    var isBg: Boolean = false
    var addGoods = AddGoods()
    var mHandler = Handler()
    var addContentList = ArrayList<Content>()
    var contentList = ArrayList<Content>()
    /**
     * 整体recyclerview adapter
     */
    private val mAdapter by lazy {
        AddGoodsAdapter(ArrayList())
    }
    /**
     * 整体recyclerview manager
     */
    private val linearLayoutManager by lazy {
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }


    override fun init() {
        StatusBarUtil.immersive(this!!)
        StatusBarUtil.setPaddingSmart(this!!, toolbar)
        UploadManager.getInstance().register(uploadListener)
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.adapter = mAdapter

        mAdapter.onItemChildClickListener = itemchildListener
        ic_bg.setOnClickListener(onclicklistener)
        rl_next.setOnClickListener(onclicklistener)

    }


    private fun isVisible() {
        if (addGoods != null && addGoods.content != null && addGoods.content!!.size > 0) {
            rl_layout.visibility = View.VISIBLE
            fl_addlayout.visibility = View.GONE
            foot_layout.visibility = View.VISIBLE
            mRecyclerView.visibility = View.VISIBLE
            foot_add_img.setOnClickListener(onclicklistener)
            foot_add_text.setOnClickListener(onclicklistener)
        } else {
            rl_layout.visibility = View.GONE
            foot_layout.visibility = View.GONE
            fl_addlayout.visibility = View.VISIBLE
            mRecyclerView.visibility = View.GONE
            item_add_img.setOnClickListener(onclicklistener)
            item_add_text.setOnClickListener(onclicklistener)
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
                            Log.e("tag", "addgons" + addGoods.content!!.size)
                            isVisible()
                            if (addGoods != null && addGoods.content != null && addGoods.content!!.size > 0) {
                                contentList.addAll(addGoods.content!!)
                                //展示数据
                                setData(addGoods)
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
        if (addGoods.price != null && addGoods.price != 0.toLong()) {
            price.setText(addGoods.price.toString())
        }
        //库存
        if (addGoods.stock != null && addGoods.stock != 0.toLong()) {
            stock.setText(addGoods.stock.toString())
        }

    }

    companion object {
        fun startAddGoodsActivity(context: Context, goods_id: Long) {
            val intent = Intent()
            //获取intent对象
            intent.putExtra("goods_id", goods_id)
            intent.setClass(context, AddGoodsActivity::class.java)
            // 获取class是使用::反射
            ContextCompat.startActivity(context, intent, null)
        }
    }

    private val onclicklistener = View.OnClickListener { v: View? ->
        when (v?.id) {
            R.id.foot_add_img -> {
                //进入相册
                choicePhotoWrapper(1, Constants.ADDIMG_RESUALT)
            }
            R.id.item_add_img -> {
                choicePhotoWrapper(1, Constants.ADDIMG_ITEM_REQUEST)
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

        addGoods.shop_id = Constants.SHOP_ID
        if (et_brief.text.toString().isNotEmpty()) {
            addGoods.brief = et_brief.text.toString()
        }
        if (et_name.text.toString().isNotEmpty()) {
            addGoods.name = et_name.text.toString()
        }
        if (price.text.toString().isNotEmpty()) {
            addGoods.price = price.text.toString().toLong()
        }
        if (stock.text.toString().isNotEmpty()) {
            addGoods.stock = stock.text.toString().toLong()
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
        if (addGoods.price == 0.toLong()) {
            ToastUtils.showError("单价未设置")
            return
        }
        if (addGoods.stock == 0.toLong()) {
            ToastUtils.showError("库存数量为设置")
            return
        }
        if (addGoods.content == null || addGoods.content!!.size <= 0) {
            ToastUtils.showError("产品详情未设置")
            return
        }

        if (goods_id != 0.toLong() && addContentList != null && addContentList.size > 0) {
            for (item in addContentList) {
                if (!addGoods.content!!.contains(item)) {
                    addGoods.content!!.add(item)
                }
            }
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
                    var selected = BGAPhotoPickerActivity.getSelectedPhotos(data)
                    if (selected != null && selected.size > 0) {
                        //去上传
                        var mFile = File(selected[0])
                        UploadManager.getInstance().add(mFile)
                    }
                }
            }
            Constants.ADDIMG_ITEM_REQUEST -> {
                if (data != null) {
                    isBg = false
                    var selected = BGAPhotoPickerActivity.getSelectedPhotos(data)
                    if (selected != null && selected.size > 0) {
                        //去上传
                        var mFile = File(selected[0])
                        UploadManager.getInstance().add(mFile)
                    }
                }
            }
            Constants.EDIT_TEXT_REQUEST -> {
                var mText = data?.getStringExtra("text")
                if (mText != null && !mText.equals("")) {
                    if (goods_id != 0.toLong()) {
                        var content = Content()
                        content.content = mText
                        contentList.add(content)
                        addContentList.add(content)
//                    addGoods.content = contentList
                        isVisible()
                        mAdapter.setNewData(contentList)
                    } else {
                        var content = Content()
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
                    var selected = BGAPhotoPickerActivity.getSelectedPhotos(data)
                    if (selected != null && selected.size > 0) {
                        var mFile = File(selected[0])
                        UploadManager.getInstance().add(mFile)
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

        }

        override fun onProgress(file: File?, currentSize: Long, totalSize: Long) {

        }

        override fun onSuccess(file: File?, id: Long) {

            mHandler.postDelayed({
                if (isBg) {
                    addGoods.cover = id.toString()
                    //如果是背景 ,显示背景
                    ImageHelper.loadImageWithCorner(application, ic_bg, ("file://" + file!!.path), 94, 94,
                            RoundedCornersTransformation(DensityUtils.dp2px(5.toFloat()), 0, RoundedCornersTransformation.CornerType.ALL))
                } else {
                    if (goods_id != 0.toLong()) {
                        //编辑
                        var content = Content()
                        content.oss_id = id
                        content.img = "file://" + file!!.path
                        contentList.add(content)
                        addContentList.add(content)
//                        addGoods.content = contentList
                        if (file != null && file.path != null) {
                            isVisible()
                            mAdapter.setNewData(contentList)
                        }
                    } else {
                        var content = Content()
                        content.oss_id = id
                        content.img = "file://" + file!!.path
                        contentList.add(content)
                        addGoods.content = contentList
                        if (file != null && file.path != null) {
                            isVisible()
                            mAdapter.setNewData(addGoods.content)
                        }
                    }

                }
            }, 500)

        }
    }

    val itemchildListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
        when (view?.id) {
            R.id.iv_remove -> {
                if (goods_id != 0.toLong()) {
                    //编辑
                    if (contentList != null && contentList.size > 0 && contentList.size > position) {
                        val removeAt = contentList.removeAt(position)

//                        if (addContentList != null && addContentList.size > 0 && addContentList.contains(removeAt)) {
//                            addContentList.remove(removeAt)
//                        }
                        Log.e("tag", "size~~~~dd" + addGoods.content!!.size)
                        for (item in addContentList) {
                            if (item.id == removeAt.id) {
                                addContentList.remove(item)
                            }
                        }
                        for (item in addGoods.content!!) {
                            if (item.id == removeAt.id) {
                                item.is_del = 1
                            }
                        }
//                        if (addGoods.content!!.contains(removeAt)) {
//                            addGoods.content!![addGoods.content!!.indexOf(removeAt)].is_del = 1
//                        }
                        isVisible()
                        mAdapter.setNewData(contentList)
                    }

                } else {
                    //新增
                    if (contentList != null && contentList.size > 0 && contentList.size > position) {
                        contentList.removeAt(position)
                        addGoods.content = contentList
                        isVisible()
                        mAdapter.setNewData(addGoods.content)
                    }
                }
            }
        }
    }
}




