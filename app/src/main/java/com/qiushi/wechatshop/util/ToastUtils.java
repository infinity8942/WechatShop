package com.qiushi.wechatshop.util;

import android.os.Build;
import android.provider.Settings;

import com.qiushi.wechatshop.R;
import com.qiushi.wechatshop.WAppContext;

import xyz.bboylin.universialtoast.UniversalToast;

/**
 * Toast工具类，可自定义图标icon、背景background、点击事件listener
 */
public class ToastUtils {

    private final static String DEFAULT_ERROR = "网络不给力，请稍后再试";

    public static void showMessage(String msg) {
        showMessage(msg, UniversalToast.UNIVERSAL);
    }

    public static void showSuccess(String msg) {
        showSuccess(msg, UniversalToast.UNIVERSAL);
    }

    public static void showError(String msg) {
        showError(msg, UniversalToast.UNIVERSAL);
    }

    public static void showWarning(String msg) {
        showWarning(msg, UniversalToast.UNIVERSAL);
    }

    /**
     * 自定义弹窗类型
     *
     * @param type UNIVERSAL 普通，EMPHASIZE 居中强调，CLICKABLE 可点击
     */
    public static void showMessage(String msg, int type) {
        if (!requestPermission()) {
            return;
        }
        if (msg == null)
            msg = DEFAULT_ERROR;
        try {
            UniversalToast.makeText(WAppContext.context, msg, UniversalToast.LENGTH_SHORT, type)
                    .setColor(R.color.bg_toast).show();
        } catch (Exception e) {
        }
    }

    public static void showSuccess(String msg, int type) {
        if (!requestPermission()) {
            return;
        }
        if (msg == null)
            msg = DEFAULT_ERROR;
        try {
            UniversalToast.makeText(WAppContext.context, msg, UniversalToast.LENGTH_SHORT, type)
                    .setColor(R.color.bg_toast).showSuccess();
        } catch (Exception e) {
        }
    }

    public static void showError(String msg, int type) {
        if (!requestPermission()) {
            return;
        }
        if (msg == null)
            msg = DEFAULT_ERROR;
        try {
            UniversalToast.makeText(WAppContext.context, msg, UniversalToast.LENGTH_SHORT, type)
                    .setColor(R.color.bg_toast).showError();
        } catch (Exception e) {
        }
    }

    public static void showWarning(String msg, int type) {
        if (!requestPermission()) {
            return;
        }
        if (msg == null)
            msg = DEFAULT_ERROR;
        try {
            UniversalToast.makeText(WAppContext.context, msg, UniversalToast.LENGTH_SHORT, type)
                    .setColor(R.color.bg_toast).showWarning();
        } catch (Exception e) {
        }
    }

    private static boolean requestPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.O || Settings.canDrawOverlays(WAppContext.context);
    }
}