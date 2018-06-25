package com.qiushi.wechatshop.ui.manage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.AddGoods
import com.qiushi.wechatshop.model.Content
import com.qiushi.wechatshop.util.DensityUtils
import com.qiushi.wechatshop.util.ImageHelper
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.util.oss.OnUploadListener
import com.qiushi.wechatshop.util.oss.UploadManager
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_add_goods.*
import kotlinx.android.synthetic.main.addgoods_header.*
import kotlinx.android.synthetic.main.next_layout.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem
import java.io.File

class AddGoodsActivity : BaseActivity() {
    override fun layoutId(): Int = R.layout.activity_add_goods
    var isBg: Boolean = false
    var addGoods = AddGoods()

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
        StatusBarUtil.immersive(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)
        UploadManager.getInstance().register(uploadListener)
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.adapter = mAdapter
        ic_bg.setOnClickListener(onclicklistener)
        rl_next.setOnClickListener(onclicklistener)
        isVisible()
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

    override fun getData() {
    }

    companion object {
        fun startAddGoodsActivity(context: Context) {
            ContextCompat.startActivity(context, Intent(context, AddGoodsActivity::class.java), null)
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
                    }
                }
            }
            Constants.EDIT_TEXT_REQUEST -> {
                val mText = data?.getStringExtra("text")
                if (mText != null && mText.isNotEmpty()) {
                    val content = Content()
                    content.content = mText
                    contentList.add(content)
                    addGoods.content = contentList
                    isVisible()
                    mAdapter.setNewData(addGoods.content)
                }
            }
            Constants.ADDIMG_BG -> {
                if (data != null) {
                    isBg = true
                    val selected = BGAPhotoPickerActivity.getSelectedPhotos(data)
                    if (selected != null && selected.size > 0) {
                        val mFile = File(selected[0])
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
            if (isBg) {
                addGoods.cover = id.toString()
                //如果是背景 ,显示背景
                ImageHelper.loadImageWithCorner(application, ic_bg, ("file://" + file!!.path), 94, 94,
                        RoundedCornersTransformation(DensityUtils.dp2px(5.toFloat()), 0, RoundedCornersTransformation.CornerType.ALL))
            } else {
                val content = Content()
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
    }
}