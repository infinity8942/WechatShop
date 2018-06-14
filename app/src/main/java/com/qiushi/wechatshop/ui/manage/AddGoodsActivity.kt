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
import com.qiushi.wechatshop.model.ShopOrder
import com.qiushi.wechatshop.rx.SchedulerUtils
import com.qiushi.wechatshop.util.StatusBarUtil
import com.qiushi.wechatshop.util.ToastUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
//        var mShopOrder1 = ShopOrder(0, "测试老虎7", Constants.GOOD7, 8, false)
//        var mShopOrder2 = ShopOrder(1, "测试老虎7", Constants.GOOD7, 8, false)
//        var mShopOrder3 = ShopOrder(1, "测试老虎7", Constants.GOOD7, 8, false)
//        var mShopOrder4 = ShopOrder(8, "测试老虎7", Constants.GOOD7, 8, false)
//        var mShopOrder5 = ShopOrder(8, "测试老虎7", Constants.GOOD7, 8, false)
//        mShopOrderList.add(mShopOrder1)
//        mShopOrderList.add(mShopOrder2)
//        mShopOrderList.add(mShopOrder3)
//        mShopOrderList.add(mShopOrder4)
//        mShopOrderList.add(mShopOrder5)

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
        } else {
            rl_layout.visibility = View.GONE
            foot_layout.visibility = View.GONE
            fl_addlayout.visibility = View.VISIBLE
            mRecyclerView.visibility = View.GONE
            item_add_img.setOnClickListener(onclicklistener)
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
                var selected = BGAPhotoPickerActivity.getSelectedPhotos(data)
                if (selected != null && selected.size > 0) {
                    var mShopOrder5 = ShopOrder(0, "测试老虎5", selected[0], 8, false)
//                    mShopOrderList.add(mShopOrder5)
                    mAdapter.addData(mShopOrder5)
                }
            }
            Constants.ADDIMG_ITEM_REQUEST -> {
                var selected = BGAPhotoPickerActivity.getSelectedPhotos(data)
                if (selected != null && selected.size > 0) {
                    var mShopOrder5 = ShopOrder(0, "测试老虎7", selected[0], 8, false)
                    mShopOrderList.add(mShopOrder5)
                    isVisible()
                }
            }
        }
    }
}
