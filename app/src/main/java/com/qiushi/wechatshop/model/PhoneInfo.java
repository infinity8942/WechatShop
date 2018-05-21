package com.qiushi.wechatshop.model;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.qiushi.wechatshop.AppContext;
import com.qiushi.wechatshop.util.Utils;

/**
 * Created by 王冰 on 2017/3/6.
 */
public class PhoneInfo {

    private static PhoneInfo instance;

    public static PhoneInfo getInstance() {
        if (instance == null)
            instance = new PhoneInfo(AppContext.context);
        return instance;
    }

    private String imei = android.os.Build.UNKNOWN;//手机标识
    private String model;//手机型号
    private String brand;//手机品牌
    private String version;//版本号
    private String channel = "test";//渠道

    private PhoneInfo(Context context) {
        this.model = android.os.Build.MODEL;
        this.brand = android.os.Build.BRAND;
        this.version = Utils.getVersionName();
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null)
                this.imei = telephonyManager.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(this.imei)) {
            this.imei = android.os.Build.UNKNOWN;
        }

//        channel = Utils.getWalleChannel();
    }

    public String getImei() {
        return imei;
    }

    public String getModel() {
        return model;
    }

    public String getBrand() {
        return brand;
    }

    public String getVersion() {
        return version;
    }

    public String getChannel() {
        return channel;
    }
}