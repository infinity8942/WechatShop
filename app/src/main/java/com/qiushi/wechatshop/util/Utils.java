package com.qiushi.wechatshop.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.meituan.android.walle.WalleChannelReader;
import com.qiushi.wechatshop.WAppContext;
import com.qiushi.wechatshop.model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    public static int getVersionCode() {
        try {
            return WAppContext.context.getPackageManager().getPackageInfo(WAppContext.application.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    public static String getVersionName() {
        try {
            return WAppContext.context.getPackageManager().getPackageInfo(WAppContext.application.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return Build.UNKNOWN;
        }
    }


    public static void call(Context context, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String getProcessName() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + android.os.Process.myPid() + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取Walle渠道包
     */
    public static String getWalleChannel() {
        String channel = WalleChannelReader.getChannel(WAppContext.context);
        if (channel == null || TextUtils.isEmpty(channel)) {
            channel = "debug";
        }
        return channel;
    }

    public static Map<String, String> getHttpHeaders() {
        User user = User.getCurrentFromRealm();
        Map<String, String> header = new HashMap<>();
        if (user != null) {
            header.put("client-id", user.getClient_id());
            header.put("access-token", user.getAccess_token());
        }
        return header;
    }
}