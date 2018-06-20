package com.qiushi.wechatshop.util;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.qiushi.wechatshop.WAppContext;
import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import static android.os.Looper.getMainLooper;

/**
 * Created by 王冰 on 2017/2/9.
 */
public class Push {
    private static String DeviceToken;

    public static String getDeviceToken() {
        if (TextUtils.isEmpty(DeviceToken)) {
            DeviceToken = SpHelper.getString(Push.class, "DeviceToken");
        }
        Logger.d("~~~~~~~~~~~~~~ getDeviceToken = " + DeviceToken);
        return DeviceToken;
    }

    public static void init() {
        PushAgent mPushAgent = PushAgent.getInstance(WAppContext.context);
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                SpHelper.putString(Push.class, "DeviceToken", deviceToken);
                DeviceToken = deviceToken;
                Logger.d("~~~~~~~~~~~~~~onSuccess deviceToken = " + DeviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Logger.e("~~~~~~~~~~~~~~ PushAgent onFailure" + s + " " + s1);
            }
        });
        mPushAgent.setMessageHandler(new UmengMessageHandler() {
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                if (msg.extra != null) {
                }
                return super.getNotification(context, msg);
            }

            @Override
            public void dealWithCustomMessage(final Context context, final UMessage uMessage) {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        });

        mPushAgent.setNotificationClickHandler(new UmengNotificationClickHandler() {
            @Override
            public void openActivity(Context context, UMessage uMessage) {
                if (uMessage.extra != null) {
                    Log.e("tag", "ljl~~~~~id~~~~=" + uMessage.extra.get("aId"));

                }
            }

            @Override
            public void openUrl(Context context, final UMessage uMessage) {
//                Intent starter = new Intent(context, WebActivity.class);
//                starter.putExtra("url", uMessage.url);
//                starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(starter);

                if (uMessage.extra != null) {

                }
            }
        });
    }

//    private static void setRead(Long nId) {
//        Subscription subscribe = ApiService.Creator.get().setReadDy(String.valueOf(nId))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap(new FlatMapResponse<TopResponse<Void>>())
//                .subscribe(new Subscriber<TopResponse<Void>>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//
//                    @Override
//                    public void onNext(TopResponse<Void> voidTopResponse) {
//                        if (voidTopResponse.getCode() == 200) {
//                            //通知刷新
//                            RxBus.getDefault().post(new org.wb.frame.config.Notification(Constant.DYNAMIC, 999));
//                        }
//                    }
//                });
//        mCompositeSubscription.add(subscribe);
//    }

    public static void enable() {
        PushAgent.getInstance(WAppContext.context).enable(new IUmengCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(String s, String s1) {
            }
        });
    }

    public static void disable() {
        PushAgent.getInstance(WAppContext.context).disable(new IUmengCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(String s, String s1) {
            }
        });
    }
}