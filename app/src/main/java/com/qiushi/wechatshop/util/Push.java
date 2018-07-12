package com.qiushi.wechatshop.util;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.qiushi.wechatshop.Constants;
import com.qiushi.wechatshop.WAppContext;
import com.qiushi.wechatshop.model.Notifycation;
import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import static android.os.Looper.getMainLooper;

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
                    if (msg.extra.get("type") != null) {
                        Log.e("tag", msg.extra.get("type") + "");
                        switch (msg.extra.get("type")) {
                            case "1":
                                //待发货订单 提醒卖家
                                break;
                            case "2":
                                //库存紧缺（提醒卖家）
                                RxBus.getInstance().post(new Notifycation(Constants.PUSH_KUCUN, 0L));
                                break;
                            case "3":
                                //待支付订单（提醒卖家 十分钟自动提醒）
                                break;
                            case "4":
                                //标记发货（提醒买家）
                                break;
                            case "5":
                                //标记发货（提醒买家）
                                break;
                            case "6":
                                //下架或删除时如果用户购物车中有该商品则推送一条
                                break;
                        }

                    }
                }
                return super.getNotification(context, msg);
            }

            @Override
            public void dealWithCustomMessage(final Context context, final UMessage uMessage) {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //踢登录
                        RxBus.getInstance().post(new Notifycation(Constants.T_LOGIN, 2L));
                    }
                });
            }
        });

        mPushAgent.setNotificationClickHandler(new UmengNotificationClickHandler() {
            @Override
            public void openActivity(Context context, UMessage uMessage) {
                if (uMessage.extra != null) {

                }
            }

            @Override
            public void openUrl(Context context, final UMessage uMessage) {
                if (uMessage.extra != null) {

                }
            }
        });
    }


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