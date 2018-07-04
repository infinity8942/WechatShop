package com.qiushi.wechatshop.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.qiushi.wechatshop.R;

import java.io.File;
import java.util.ArrayList;

public class ShareUtils {
    PopupWindow popupWindow;
    Context context;
    private String path;//单张图片路径
    private String content;
    private Button btn;
    private Uri[] uris;//多张图片路径uri数组
    public ShareUtils(Context context, String content){
        this.context=context;
        //  this.path=path;
        this.content=content;

        //  this.btn=btn;

//        showpop();
    }

    public void setUri(Uri[] uris){
        this.uris = uris;
    }

    public void setPath(String path){
        this.path = path;
    }

//    private void showpop() {
//        View view= LayoutInflater.from(context).inflate(
//                R.layout.share_view, null);
//        ImageView img_weixin= (ImageView) view.findViewById(R.id.share_weixin);
//        ImageView img_pyq= (ImageView) view.findViewById(R.id.share_pengyouquan);
//        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        popupWindow.setBackgroundDrawable(new BitmapDrawable()); // 点击返回按钮popwindow消失
//
//        img_weixin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (StringUtils.isWeixinAvilible(context)) {// 判断是否安装微信客户端
//                    // shareweixin(path);
//                    shareWXSomeImg(context,uris);
//                    // login(SHARE_MEDIA.WEIXIN);
//                } else {
//                    ActivityUtil.showToast(context, "请安装微信客户端");
//                }
//
//                popupWindow.dismiss();
//            }
//        });
//        img_pyq.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (StringUtils.isWeixinAvilible(context)) {// 判断是否安装微信客户端
//                    //   shareweipyq(path,content);//拉起微信朋友圈带一张图片
//                    shareweipyqSomeImg(context,uris);//拉起微信朋友圈带多张图片
//                    // login(SHARE_MEDIA.WEIXIN);
//                } else {
//                    ActivityUtil.showToast(context, "请安装微信客户端");
//                }
//                popupWindow.dismiss();
//            }
//        });
//
//        popupWindow.showAtLocation( LayoutInflater.from(context).inflate(
//                R.layout.activity_posterxq, null).findViewById(R.id.img_share), Gravity.BOTTOM, 0, 0);// 先设置popwindow的所有参数，最后再show
//
//    }

    /**
     * 拉起微信好友发送单张图片
     * */
    private void shareweixin(String path){
        Uri uriToImage = Uri.fromFile(new File(path));
        Intent shareIntent = new Intent();
        //发送图片到朋友圈
        //ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        //发送图片给好友。
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        shareIntent.setComponent(comp);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        shareIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(shareIntent, "分享图片"));
    }
    /**
     * 拉起微信朋友圈发送单张图片
     * */
    private void shareweipyq(String path,String content){
        Uri uriToImage = Uri.fromFile(new File(path));
        Intent shareIntent = new Intent();
        //发送图片到朋友圈
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        //发送图片给好友。
//        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        shareIntent.setComponent(comp);
        shareIntent.putExtra("Kdescription", content);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        shareIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(shareIntent, "分享图片"));
    }

    /**
     * 拉起微信朋友圈发送多张图片
     * */
    public void shareweipyqSomeImg(Context context,Uri[] uri){
        //2添加图片数组
        ArrayList<Uri> imageUris = new ArrayList<>();
        for (int i = 0; i < uri.length; i++) {
            imageUris.add(uri[i]);
        }
        Intent shareIntent = new Intent();
        //1调用系统分析
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.e("tag","imageUris.size"+imageUris.size()+imageUris.get(0).getEncodedPath());

        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,imageUris);
        shareIntent.setType("image/*");

        //3指定选择微信
        ComponentName componentName = new ComponentName("com.tencent.mm","com.tencent.mm.ui.tools.ShareToTimeLineUI");
        shareIntent.setComponent(componentName);

        //4开始分享
        context.startActivity(Intent.createChooser(shareIntent,"分享图片"));
    }

    /**
     * 拉起微信发送多张图片给好友
     * */
    private void shareWXSomeImg(Context context, Uri[] uri){
        Intent shareIntent = new Intent();
        //1调用系统分析
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //2添加图片数组
        ArrayList<Uri> imageUris = new ArrayList<>();
        for (int i = 0; i < uri.length; i++) {
            imageUris.add(uri[i]);
        }

        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,imageUris);
        shareIntent.setType("image/*");

        //3指定选择微信
        ComponentName componentName = new ComponentName("com.tencent.mm","com.tencent.mm.ui.tools.ShareImgUI");
        shareIntent.setComponent(componentName);

        //4开始分享
        context.startActivity(Intent.createChooser(shareIntent,"分享图片"));
    }



    public static void sharePhotoToWX(Context context, String text, String photoPath) {
        if (!uninstallSoftware(context, "com.tencent.mm")) {
            Toast.makeText(context, "微信没有安装！", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(photoPath);
        if (!file.exists()) {
            String tip = "文件不存在";
            Toast.makeText(context, tip + " path = " + photoPath, Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(componentName);
        intent.setAction("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra("Kdescription", text);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        context.startActivity(intent);
    }

    private static boolean uninstallSoftware(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            if (packageInfo != null) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


}
