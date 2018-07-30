package com.qiushi.wechatshop.ui.manage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import cnn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
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
import com.qiushi.wechatshop.util.permission.HiPermission
import com.qiushi.wechatshop.util.permission.PermissionCallback
import com.qiushi.wechatshop.util.permission.PermissionItem
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_decorate.*
import java.io.File

/**
 * Created by Rylynn on 2018-06-15.
 *
 * 店铺装修
 */
class DecorateActivity : BaseActivity(), View.OnClickListener {

    private var shopName: String = ""
    private var bgUrl: String = ""
    private var logoUrl: String = ""
    private var shopId: Long = 0
    private var ossId = ""
    private var bgOssId = ""
    private var mHandler = Handler()

    private var logoPath = ""
    private var coverPath = ""


    override fun layoutId(): Int = R.layout.activity_decorate

    override fun init() {
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        UploadManager.getInstance().register(uploadListener)

        back.setOnClickListener(this)
        logo.setOnClickListener(this)
        cover.setOnClickListener(this)
        commit.setOnClickListener(this)

        if (logoUrl.isNotEmpty()) {
            ImageHelper.loadImageWithCorner(application, logo, logoUrl, 64, 64,
                    RoundedCornersTransformation(DensityUtils.dp2px(5.toFloat()), 0, RoundedCornersTransformation.CornerType.ALL))
        }

        val width = DensityUtils.getScreenWidth() - DensityUtils.dp2px(32.toFloat())
        val lp = RelativeLayout.LayoutParams(width, (width * 0.52).toInt())
        lp.addRule(RelativeLayout.BELOW, R.id.star3)
        lp.topMargin = DensityUtils.dp2px(8.toFloat())
        cover.layoutParams = lp
        if (bgUrl.isNotEmpty()) {
            ImageHelper.loadImageWithCorner(application, cover, bgUrl, 343, 178,
                    RoundedCornersTransformation(DensityUtils.dp2px(5.toFloat()), 0, RoundedCornersTransformation.CornerType.ALL))
        }

        if (shopName.isNotEmpty()) {
            name.setText(shopName)
            name.setSelection(shopName.length)
        }

        if (User.getCurrent() != null && User.getCurrent().shop_id != null) {
            shopId = User.getCurrent().shop_id
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
            ToastUtils.showMessage("店铺名不能为空")
            return
        }
        if (name == "我的店" || name == "我的店铺" || name == "My Shop" || name == "MyShop") {
            ToastUtils.showMessage("店铺名不合法，请重新输入")
            return
        }

        val disposable = RetrofitManager.service.editShop(name, ossId, shopId, bgOssId)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<String>() {
                    override fun onHandleSuccess(t: String) {
                        if (t.isNotEmpty()) {
                            //开店, 保存到本地
                            User.editCurrent { u -> u!!.shop_id = t.toLong() }
                            ToastUtils.showMessage("开店铺成功")
                            RxBus.getInstance().post(Notifycation(Constants.OPEN_SHOP, 0L))
                        } else {
                            ToastUtils.showMessage("装修店铺成功")
                            RxBus.getInstance().post(Notifycation(Constants.ZX_SHOP, 0L))
                        }
                        finish()
                    }

                    override fun onHandleError(error: com.qiushi.wechatshop.net.exception.Error) {
                        ToastUtils.showMessage(error.msg)
                    }
                })
        compositeDisposable.add(disposable)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.ADDIMG_LOGO -> {
                if (data != null) {
                    val selected = BGAPhotoPickerActivity.getSelectedPhotos(data)
                    if (selected != null && selected.size > 0) {
                        val mFile = File(selected[0])
                        logoPath = mFile.path
                        ImageHelper.loadImageWithCorner(application, logo, ("file://$logoPath"), 64, 64,
                                RoundedCornersTransformation(DensityUtils.dp2px(5.toFloat()), 0, RoundedCornersTransformation.CornerType.ALL))
                        UploadManager.getInstance().add(mFile)
                        showLoading("正在上传,请等待....")
                    }
                }
            }
            Constants.ADDIMG_GOODS_BG -> {
                if (data != null) {
                    val selected = BGAPhotoPickerActivity.getSelectedPhotos(data)
                    if (selected != null && selected.size > 0) {
                        val mFile = File(selected[0])
                        coverPath = mFile.path
                        ImageHelper.loadImageWithCorner(application, cover, ("file://$coverPath"), 343, 178,
                                RoundedCornersTransformation(DensityUtils.dp2px(5.toFloat()), 0, RoundedCornersTransformation.CornerType.ALL))
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

        override fun onSuccess(file: File, id: Long) {
            mHandler.postDelayed({
                dismissLoading()
                when (file.path) {
                    logoPath -> {
                        ossId = id.toString()
                    }
                    coverPath -> {
                        bgOssId = id.toString()
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
        shopName = intent.getStringExtra("shop_name")
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
        HiPermission.create(this).permissions(permissionItems)
                .checkMutiPermission(object : PermissionCallback {
                    override fun onGuarantee(permission: String?, position: Int) {
                    }

                    override fun onDeny(permission: String?, position: Int) {
                        ToastUtils.showMessage("拒绝权限")
                    }

                    override fun onClose() {
                        ToastUtils.showMessage("拒绝权限")
                    }

                    override fun onFinish() {
                    }
                })
    }
}