package com.qiushi.wechatshop.ui.moments

import android.Manifest
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import cnn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.qiushi.wechatshop.Constants
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.base.BaseActivity
import com.qiushi.wechatshop.model.Moment
import com.qiushi.wechatshop.model.NineImage
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
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_moments_create.*
import top.zibin.luban.Luban
import java.io.File

/**
 * Created by Rylynn on 2018-06-14.
 *
 * 创建、编辑素材
 */
class CreateMomentsActivity : BaseActivity() {

    private var type: Int = 1 //1产品、2鸡汤、3海报
    var id: Long = 0
    private var moment = Moment()//编辑
    var foot: View? = null
    var size: Int = 0
    private var mNineList = ArrayList<NineImage>()
    private var mFileList = ArrayList<File>()
    private var addNineList = ArrayList<NineImage>() //删除新加后的展示
    private var itemList = ArrayList<NineImage>()
    var gson = Gson()
    var mJson: String = ""
    var mNineImage = NineImage()
    private var mHandler = Handler()

    val CACHE_FOLDER = FileUtil.getUploadTempFolder().path

    private val mGrideManager by lazy {
        GridLayoutManager(this, 3)
    }

    private val mGrideAdapter by lazy {
        CreateMomentAdapter(ArrayList())
    }

    override fun layoutId(): Int = R.layout.activity_moments_create

    override fun init() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, R.color.colorPrimaryDark)
        StatusBarUtil.setPaddingSmart(this, toolbar)

        UploadManager.getInstance().register(uploadListener)

        if (id != 0.toLong()) {
            tv_title.text = "编辑素材"
        }
        if (type == 3) {
            et_text.visibility = View.GONE
        }
        mRecyclerView.layoutManager = mGrideManager
        mRecyclerView.adapter = mGrideAdapter

        //Listener
        back.setOnClickListener(this)
        tv_ok.setOnClickListener(this)

        mGrideAdapter.onItemChildClickListener = itemChildListener
    }

    override fun getParams(intent: Intent) {
        super.getParams(intent)
        type = intent.getIntExtra("type", 1)
        id = intent.getLongExtra("id", 0)
    }

    override fun getData() {
        if (id != 0.toLong()) {
            val subscribeWith: BaseObserver<Moment> = RetrofitManager.service.editMomentsInfo(id)
                    .compose(SchedulerUtils.ioToMain())
                    .subscribeWith(object : BaseObserver<Moment>() {
                        override fun onHandleSuccess(t: Moment) {
                            if (t != null) {
                                setData(t)
                            }
                        }

                        override fun onHandleError(error: com.qiushi.wechatshop.net.exception.Error) {
                        }
                    })
            addSubscription(subscribeWith)
        } else {
            mNineList.add(mNineImage)
            mGrideAdapter.setNewData(mNineList)
        }
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
                            ToastUtils.showMessage("未填写产品描述")
                            return
                        }
                    }
                    2 -> {
                        if (moment.content.isEmpty()) {
                            ToastUtils.showMessage("未填写鸡汤描述")
                            return
                        }
                    }
                }
                if (size == 0) {
                    ToastUtils.showMessage("未添加图片")
                    return
                }

                if (id != 0.toLong()) {
                    if (moment.images != null && moment.images!!.size > 0) {
                        for (item in moment.images!!) {
                            when (item.type) {
                                "1" -> {
                                    ToastUtils.showMessage("还有图片正在上传，请稍候")
                                    return
                                }
                                "2" -> {
                                    ToastUtils.showMessage("还有图片上传失败，是否重新上传")
                                    return
                                }
                            }
                        }
                    }
                } else {
                    for (item in mNineList) {
                        when (item.type) {
                            "1" -> {
                                ToastUtils.showMessage("还有图片正在上传，请稍候")
                                return
                            }
                            "2" -> {
                                ToastUtils.showMessage("还有图片上传失败，是否重新上传")
                                return
                            }
                        }
                    }

                }

                mJson = if (id != 0.toLong()) {
                    if (moment.images!!.contains(mNineImage)) {
                        moment.images!!.remove(mNineImage)
                    }
                    addNineList
                            .filterNot { moment.images!!.contains(it) }
                            .forEach { moment.images!!.add(it) }
                    size = moment.images!!.size

                    gson.toJson(moment.images)
                } else {
                    if (size < 9) {
                        mNineList.removeAt(mNineList.size - 1)
                        gson.toJson(mNineList)
                    } else {
                        gson.toJson(mNineList)
                    }
                }

                Log.e("tag", "mJson$mJson")
                postData(mJson)
            }
        }
    }

    private fun postData(mJson: String) {
        val subscribeWith: BaseObserver<Boolean> = RetrofitManager.service.addMoments(type, moment.id, moment.content, User.getCurrent().shop_id, mJson)
                .compose(SchedulerUtils.ioToMain())
                .subscribeWith(object : BaseObserver<Boolean>() {
                    override fun onHandleSuccess(t: Boolean) {
                        if (t) {
                            ToastUtils.showMessage("发布成功")
//                            setResult(RESULT_OK)
                            RxBus.getInstance().post(Notifycation(Constants.MOMENT_FRESH, type.toLong()))
                            finish()
                        } else {
                            ToastUtils.showMessage("发布失败")
                            finish()
                        }
                    }

                    override fun onHandleError(error: com.qiushi.wechatshop.net.exception.Error) {
                    }
                })
        addSubscription(subscribeWith)
    }

    private val itemChildListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
        when (view.id) {
            R.id.foot -> choicePhotoWrapper((9 - size), Constants.ADDSC_IMG)
            R.id.iv_remove -> {
                if (id != 0.toLong()) {
                    //编辑
                    val removeAt = mNineList.removeAt(position)
                    if (size < 9) {
                        if (null != addNineList && addNineList.size > 0 && addNineList.contains(removeAt)) {
                            addNineList.remove(removeAt)
                        }
                        if (moment.images!!.contains(removeAt)) {
                            moment.images!![moment.images!!.indexOf(removeAt)].is_del = 1
                        }
                        size = mNineList.size - 1
                    } else {
                        if (moment.images!!.contains(removeAt)) {
                            moment.images!![moment.images!!.indexOf(removeAt)].is_del = 1
                        }
                        if (addNineList != null && addNineList.size > 0 && addNineList.contains(removeAt)) {
                            addNineList.remove(removeAt)
                        }
                        size = mNineList.size
                        mNineList.add(mNineImage)
                    }
                    mGrideAdapter.setNewData(mNineList)
                } else {
                    //新添加
                    mNineList.removeAt(position)
                    moment.images = mNineList
                    if (size < 9) {
                        size = moment.images!!.size - 1
                        mGrideAdapter.setNewData(moment.images)
                    } else {
                        size = moment.images!!.size
//                        mNineList.addAll(mNineImage)
                        mNineList.add(mNineImage)
                        mGrideAdapter.setNewData(mNineList)
                    }
                }
            }
        }
    }

    private fun setData(t: Moment) {
        moment = t
        if (moment.content.isNotEmpty()) {
            et_text.setText(t.content)
        }
        if (moment.images != null && moment.images!!.size != 0) {
            size = moment.images!!.size
            if (size < 9) {
                for (item in moment.images!!) {
                    item.size = 1
                }
                mNineList.addAll(moment.images!!)
                mNineList.add(mNineImage)
                mGrideAdapter.setNewData(mNineList)
            } else {
                size = moment.images!!.size
                mNineList.addAll(moment.images!!)
//                mNineList = moment.images!!
                for (item in moment.images!!) {
                    item.size = 1
                }
                mGrideAdapter.setNewData(moment.images)
            }
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
                        ToastUtils.showMessage("拒绝权限")
                    }

                    override fun onClose() {
                        ToastUtils.showMessage("拒绝权限")
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
                    val selected = BGAPhotoPickerActivity.getSelectedPhotos(data)
                    if (selected != null && selected.size > 0) {

                        //compress
                        showLoading("压缩中，请稍候...")
                        compress(selected)
                    }
                }
            }
        }
    }

    /**
     * 压缩、上传
     */
    private fun compress(photos: ArrayList<String>) {
        Observable.just(photos)
                .observeOn(Schedulers.io())
                .map { t ->
                    Luban.with(this@CreateMomentsActivity).ignoreBy(Constants.MAX_UPLOAD_SIZE).setTargetDir(CACHE_FOLDER).load(t)
                            .filter { path -> !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif")) }.get()
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<File>> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onComplete() {
                        dismissLoading()
                    }

                    override fun onNext(t: List<File>) {
                        if (NetworkUtil.isWifi()) {
                            ToastUtils.showMessage("WIFI下自动上传")
                        }

                        if (id != 0.toLong()) {
                            //编辑
                            size += t.size
                            mFileList.clear()
                            for (item in t) {
                                val mFile = File(item.path)
                                val mNineImage = NineImage()
                                mNineImage.size = 1
                                mNineImage.img_url = "file://$item"
                                mNineList.add(mNineList.size - 1, mNineImage)
//                                moment.images!!.add(mNineImage)
                                mFileList.add(mFile)
                                addNineList.add(mNineImage)
                            }
                            if (size < 9) {
                                mGrideAdapter.setNewData(mNineList)
                            } else {
                                mNineList.removeAt(mNineList.size - 1)
                                mGrideAdapter.setNewData(mNineList)
                            }
                        } else {
                            //新添加
                            size += t.size
                            mFileList.clear()
                            for (item in t) {
                                val mFile = File(item.path)
                                val mNineImage = NineImage()
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
                        }
                        UploadManager.getInstance().add(mFileList)
                    }

                    override fun onError(t: Throwable) {
                        dismissLoading()
                        Logger.e(t, "!!! compress onError = ", t.message)
                    }
                })
    }

    /**
     * type  0 上传成功， 1，上传中，2 上传失败
     */
    private val uploadListener = object : OnUploadListener {
        override fun onProgress(file: File?, currentSize: Long, totalSize: Long) {
            val nineImage = findPictureByFile(file!!)
            if (nineImage != null) {
                for (item in nineImage) {
                    item.type = "1"
                }
            }
        }

        override fun onSuccess(file: File?, id: Long) {
            mHandler.postDelayed({
                val nineImage = findPictureByFile(file!!)
                if (nineImage != null) {
                    for (item in nineImage) {
                        item.oss_id = id
                        item.type = "0"
                    }
                }
            }, 300)
        }

        override fun onFailure(file: File?, error: Error?) {
            val nineImage = findPictureByFile(file!!)
            if (nineImage != null) {
                for (item in nineImage) {
                    item.oss_id = id
                    item.type = "2"
                }
            }
        }
    }

    /**
     * 检查 是否是同一张图片  设置progress
     */
    private fun findPictureByFile(file: File): ArrayList<NineImage>? {
        if (id != 0.toLong()) {
            itemList.clear()
            if (addNineList != null) {
                for (item in addNineList) {
                    if (item.img_url == "file://${file.path}") {
                        itemList.add(item)
                    }
                }
                return itemList
            }
        } else {
            itemList.clear()
            val mData = mGrideAdapter.data
            for (item in mData) {
                if (item.img_url == "file://${file.path}") {
                    itemList.add(item)
                }
            }
            return itemList
        }
        return null
    }
}