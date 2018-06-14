package com.qiushi.wechatshop.ui.manage


import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.ShopOrder

import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.util.oss.OnUploadListener
import com.qiushi.wechatshop.util.oss.UploadManager

import kotlinx.android.synthetic.main.activity_add_goods.*
import kotlinx.android.synthetic.main.addgoods_header.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem
import java.io.File

import java.util.*


class AddGoodsActivity : BaseActivity() {
    override fun layoutId(): Int = R.layout.activity_add_goods
    private var mShopOrderList = ArrayList<ShopOrder>()

    /**
     * 整体recyclerview adapter
     */
    private val mAdapter by lazy {
        AddGoodsAdapter(mShopOrderList)
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

        isVisible()
    }


    private fun isVisible() {
        if (mShopOrderList != null && mShopOrderList.size > 0) {
            rl_layout.visibility = View.VISIBLE
            fl_addlayout.visibility = View.GONE
            foot_layout.visibility = View.VISIBLE
            mRecyclerView.visibility = View.VISIBLE
            mRecyclerView.adapter = mAdapter
            //foot add点击事件
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
            val intent = Intent()
            //获取intent对象
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
            R.id.foot_add_text -> EditTextActivity.startEditTextActivity(this, "")
        }
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
                    var mShopOrder5 = ShopOrder(1, mText, Constants.GOOD7, 8, false)
                    if (mShopOrderList == null||mShopOrderList.size==0) {
                        mShopOrderList.add(mShopOrder5)
                        isVisible()
                    } else {
                        mAdapter.addData(mShopOrder5)
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
//            ToastUtils.showError("失败" + currentSize / totalSize)
        }

        override fun onSuccess(file: File?, id: Long) {
            if (file != null && file.path != null) {
                if (mShopOrderList == null||mShopOrderList.size==0) {
                    var mShopOrder5 = ShopOrder(0, "老虎", "file://"+file.path, 8, false)
                    mShopOrderList.add(mShopOrder5)
                    isVisible()
                } else {
                    var mShopOrder5 = ShopOrder(0, "老虎", "file://"+file.path, 8, false)
                    mAdapter.addData(mShopOrder5)
                }
            }
        }
    }
}

