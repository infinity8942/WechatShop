package com.qiushi.wechatshop.ui.manage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Notifycation
import com.qiushi.wechatshop.model.User
import com.qiushi.wechatshop.net.RetrofitManager
import com.qiushi.wechatshop.rx.BaseObserver
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.*
import com.qiushi.wechatshop.util.oss.Error
import com.qiushi.wechatshop.util.oss.OnUploadListener
import com.qiushi.wechatshop.util.oss.UploadManager
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_decorate.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem
import java.io.File

/**
 * Created by Rylynn on 2018-06-15.
 *
 * 店铺装修
 */
class DecorateActivity : BaseActivity(), View.OnClickListener {

    private var shop_name: String = ""
    private var bgUrl: String = ""
    private var logoUrl: String = ""
    private var isLogo: Boolean = false
    private var shop_id: Long = 0
    private var oss_id = ""
    private var bg_oss_id = ""
    private var mHandler = Handler()

    override fun layoutId(): Int = R.layout.activity_decorate

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)
        UploadManager.getInstance().register(uploadListener)
        back.setOnClickListener(this)
        logo.setOnClickListener(this)
        cover.setOnClickListener(this)
        commit.setOnClickListener(this)

        if (shop_name.isNotEmpty()) {
            name.setText(shop_name)
            name.setSelection(shop_name.length)
        }
        if (bgUrl.isNotEmpty()) {
            ImageHelper.loadImageWithCorner(application, cover, bgUrl, 343, 178,
                    RoundedCornersTransformation(DensityUtils.dp2px(5.toFloat()), 0, RoundedCornersTransformation.CornerType.ALL))

        }
        if (logoUrl.isNotEmpty()) {
            ImageHelper.loadImageWithCorner(application, logo, logoUrl, 64, 64,
                    RoundedCornersTransformation(DensityUtils.dp2px(5.toFloat()), 0, RoundedCornersTransformation.CornerType.ALL))
        }

        if (User.getCurrent() != null && User.getCurrent().shop_id != null) {
            shop_id = User.getCurrent().shop_id
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.logo -> choicePhotoWrapper(1, Constants.ADDIMG_LOGO)
            R.id.cover -> choicePhotoWrapper(1, Constants.ADDIMG_GOODS_BG)
            R.id.commit -> edit()
        }
    }

    override fun getData() {
    }

    private fun edit() {
        val name = name.text.toString().trim()
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showWarning("店铺名不能为空")
            return
        }
        if (name == "我的店" || name == "我的店铺" || name == "My Shop" || name == "MyShop") {
            ToastUtils.showWarning("店铺名不合法，请重新输入")
            return
        }

        val disposable = RetrofitManager.service.editShop(name, oss_id, shop_id, bg_oss_id)//TODO oss id
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<String>() {
                    override fun onHandleSuccess(t: String) {
                        if (t.isNotEmpty()) {
                            //开店, 保存到本地
                            User.editCurrent { u -> u!!.shop_id = t.toLong() }
                            ToastUtils.showMessage("开店铺成功")
                            RxBus.getInstance().post(Notifycation(Constants.OPEN_SHOP_OR_ZX, 0L))
                            finish()
                        } else {
                            ToastUtils.showMessage("装修店铺成功")
                            RxBus.getInstance().post(Notifycation(Constants.OPEN_SHOP_OR_ZX, 0L))
                            finish()
                        }
                    }

                    override fun onHandleError(error: com.qiushi.wechatshop.net.exception.Error) {
                        ToastUtils.showError(error.msg)
                    }
                })
        compositeDisposable.add(disposable)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.ADDIMG_LOGO -> {
                if (data != null) {
                    isLogo = true
                    val selected = BGAPhotoPickerActivity.getSelectedPhotos(data)
                    if (selected != null && selected.size > 0) {
                        val mFile = File(selected[0])
                        UploadManager.getInstance().add(mFile)
                        showLoading("正在上传,请等待....")
                    }
                }
            }
            Constants.ADDIMG_GOODS_BG -> {
                if (data != null) {
                    isLogo = false
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

    private val uploadListener = object : OnUploadListener {
        override fun onProgress(file: File?, currentSize: Long, totalSize: Long) {
        }

        override fun onSuccess(file: File?, id: Long) {
            mHandler.postDelayed({
                dismissLoading()
                when (isLogo) {
                    false -> {
                        Log.e("tag", file!!.path + "~~is~~~")
                        bg_oss_id = id.toString()
                        ImageHelper.loadImageWithCorner(application, cover, ("file://" + file!!.path), 343, 178,
                                RoundedCornersTransformation(DensityUtils.dp2px(5.toFloat()), 0, RoundedCornersTransformation.CornerType.ALL))
                    }
                    true -> {
                        Log.e("tag", file!!.path)
                        oss_id = id.toString()
                        ImageHelper.loadImageWithCorner(application, logo, ("file://" + file!!.path), 64, 64,
                                RoundedCornersTransformation(DensityUtils.dp2px(5.toFloat()), 0, RoundedCornersTransformation.CornerType.ALL))
                    }
                }
            }, 300)
        }

        override fun onFailure(file: File?, error: Error?) {
            dismissLoading()
        }
    }

    override fun getParams(intent: Intent) {
        super.getParams(intent)
        shop_name = intent.getStringExtra("shop_name")
        logoUrl = intent.getStringExtra("logo")
        bgUrl = intent.getStringExtra("bg")
    }

    companion object {
        fun startDecorateActivity(context: Context, shop_name: String, logo: String, bg: String) {
            val intent = Intent(context, DecorateActivity::class.java)
            intent.putExtra("shop_name", shop_name)
            intent.putExtra("logo", logo)
            intent.putExtra("bg", bg)
            ContextCompat.startActivity(context, intent, null)
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
}