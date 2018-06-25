package com.qiushi.wechatshop.ui.moments

import android.Manifest
import android.content.Intent
import android.database.Observable
import android.os.Environment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Moment
import com.qiushi.wechatshop.model.NineImage
import com.qiushi.wechatshop.net.BaseResponse
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import com.qiushi.wechatshop.util.oss.Error
import com.qiushi.wechatshop.util.oss.OnUploadListener
import com.qiushi.wechatshop.util.oss.UploadManager
import kotlinx.android.synthetic.main.activity_moments_create.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem
import java.io.File


/**
 * Created by Rylynn on 2018-06-14.
 *
 * 创建、编辑素材
 */
class CreateMomentsActivity : BaseActivity() {

    private var type: Int = 1 //1产品、2鸡汤、3海报
    private var moment = Moment()//编辑
    var foot: View? = null
    var size: Int = 0
    private var mNineList = ArrayList<NineImage>()
    private var mFileList = ArrayList<File>()
    var gson = Gson()
    var mJson: String = ""
    override fun layoutId(): Int {
        return R.layout.activity_moments_create
    }


    private val mGrideManager by lazy {
        GridLayoutManager(this, 3)
    }


    private val mGrideAdapter by lazy {
        CreateMomentAdapter(ArrayList())
    }

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        UploadManager.getInstance().register(uploadListener)
        type = intent.getIntExtra("type", 1)
        if (intent.hasExtra("moment")) {
            tv_title.text = "编辑素材"
            moment = intent.getParcelableExtra("moment") as Moment
            setData()
        }


        //Listener
        back.setOnClickListener(this)
        tv_ok.setOnClickListener(this)
        var mNineImage = NineImage()
        mNineList.add(mNineImage)
        mRecyclerView.layoutManager = mGrideManager
        mRecyclerView.adapter = mGrideAdapter
        mGrideAdapter.setNewData(mNineList)

        mGrideAdapter.onItemChildClickListener = itemchildListener
    }


    override fun getData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.tv_ok -> {
                //需要判断size是否等于9 小于9 去掉最后一个
                if (et_text.text.toString().trim().isNotEmpty()) {
                    moment.content = et_text.text.toString().trim()
                }

                when (type) {
                    1 -> {
                        if (moment.content.isEmpty()) {
                            ToastUtils.showError("未填写产品描述")
                            return
                        }
                    }
                    2 -> {
                        if (moment.content.isEmpty()) {
                            ToastUtils.showError("未填写鸡汤描述")
                            return
                        }
                    }

                }

                if (size < 9) {
                    mNineList.removeAt(mNineList.size - 1)
                    mJson = gson.toJson(mNineList)
                } else {
                    mJson = gson.toJson(mNineList)
                }

                postData(mJson)
            }
        }
    }

    private fun postData(mJson: String) {
        RetrofitManager.service.addMoments(type, moment.id, moment.content, Constants.SHOP_ID, mJson)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        if (t) {
                            ToastUtils.showSuccess("发布成功")
                            finish()
                        }else{
                            ToastUtils.showError("发布失败")
                            finish()
                        }
                    }

                    override fun onHandleError(error: com.qiushi.wechatshop.net.exception.Error) {

                    }
                })
    }

    private val itemchildListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
        when (view.id) {
            R.id.foot -> {
                choicePhotoWrapper((9 - size), Constants.ADDSC_IMG)
            }
        }
    }

    private fun setData() {

    }

    private fun addMoments() {
//        val disposable = RetrofitManager.service.addMoments(id)
//                .compose(SchedulerUtils.ioToMain())
//                .subscribeWith(object : BaseObserver<Boolean>() {
//                    override fun onHandleSuccess(t: Boolean) {
//                        if (t) {
//                            ToastUtils.showMessage("添加成功")
//                            finish()
//                        }
//                    }
//
//                    override fun onHandleError(error: Error) {
//                        ToastUtils.showError(error.msg)
//                    }
//                })
//        addSubscription(disposable)
    }

    private fun editMoments() {
//        val disposable = RetrofitManager.service.editMoments(id)
//                .compose(SchedulerUtils.ioToMain())
//                .subscribeWith(object : BaseObserver<Boolean>() {
//                    override fun onHandleSuccess(t: Boolean) {
//                        if (t) {
//                            ToastUtils.showMessage("编辑成功")
//                            finish()
//                        }
//                    }
//
//                    override fun onHandleError(error: Error) {
//                        ToastUtils.showError(error.msg)
//                    }
//                })
//        addSubscription(disposable)
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
            Constants.ADDSC_IMG -> {
                if (data != null) {
                    var selected = BGAPhotoPickerActivity.getSelectedPhotos(data)
                    if (selected != null && selected.size > 0) {
                        size += selected.size
                        mFileList.clear()
                        for (item in selected) {
                            var mFile = File(item)
                            var mNineImage = NineImage()
                            mNineImage.size = 1
                            mNineImage.img_url = "file://$item"
                            if (mNineList.size != 0) {
                                mNineList.add(mNineList.size - 1, mNineImage)
                            } else {
                                mNineList.add(mNineImage)
                            }
                            mFileList.add(mFile)
                        }
                        if (size < 9) {
                            moment.images = mNineList
                            mGrideAdapter.setNewData(moment.images)
                        } else {
                            mNineList.removeAt(mNineList.size - 1)
                            moment.images = mNineList
                            mGrideAdapter.setNewData(moment.images)
                        }
                        UploadManager.getInstance().add(mFileList)
                    }
                }
            }
        }
    }

    private val uploadListener = object : OnUploadListener {
        override fun onProgress(file: File?, currentSize: Long, totalSize: Long) {

            val nineImage = findPictureByFile(file!!)
        }

        override fun onSuccess(file: File?, id: Long) {
            Log.e("tag", "longID===========" + id)
            val nineImage = findPictureByFile(file!!)
            if (nineImage != null) {
                nineImage.oss_id = id
            }
        }

        override fun onFailure(file: File?, error: Error?) {
        }
    }

    /**
     * 检查 是否是同一张图片  设置progress
     */

    private fun findPictureByFile(file: File): NineImage? {

        var mData = mGrideAdapter.data

        for (item in mData) {
            if (item.img_url == "file://${file.path}")
                return item
        }

        return null
    }
}