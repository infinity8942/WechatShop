package com.qiushi.wechatshop.util;

import android.widget.Toast;

import com.qiushi.wechatshop.WAppContext;

import me.drakeet.support.toast.ToastCompat;

public class ToastUtils {

    private final static String DEFAULT_ERROR = "网络不给力，请稍后再试";

    public static void showMessage(String msg) {
        if (msg == null)
            msg = DEFAULT_ERROR;
        ToastCompat.makeText(WAppContext.context, msg, Toast.LENGTH_SHORT).show();
    }
}