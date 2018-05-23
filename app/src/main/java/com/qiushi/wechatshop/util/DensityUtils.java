package com.qiushi.wechatshop.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.qiushi.wechatshop.WAppContext;

/**
 * 常用单位转换的辅助类
 */
public class DensityUtils {
    private DensityUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获得屏幕宽度
     */
    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) WAppContext.INSTANCE.getApplication().getSystemService(Context.WINDOW_SERVICE);
        if (null != wm) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            return outMetrics.widthPixels;
        }
        return 0;
    }

    /**
     * 获得屏幕高度
     */
    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) WAppContext.INSTANCE.getApplication().getSystemService(Context.WINDOW_SERVICE);
        if (null != wm) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            return outMetrics.heightPixels;
        }
        return 0;
    }

    /**
     * dp转px
     */
    public static int dp2px(float dpVal) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpVal * scale + 0.5f);
    }

    /**
     * sp转px
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * px转dp
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }
}