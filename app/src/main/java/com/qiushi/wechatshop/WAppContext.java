package com.qiushi.wechatshop;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

@SuppressLint("StaticFieldLeak")
public class WAppContext {
    public static Application application = null;
    public static Context context = null;
}