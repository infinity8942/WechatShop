package cnn.bingoogolapple.photopicker.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;

public class BGAPhotoPickerUtil {
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    private BGAPhotoPickerUtil() {
    }

    private static void runInUIThread(Runnable task) {
        sHandler.post(task);
    }

    /**
     * 获取取屏幕宽度
     */
    public static int getScreenWidth() {
        WindowManager windowManager = (WindowManager) BGABaseAdapterUtil.getApp().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight() {
        WindowManager windowManager = (WindowManager) BGABaseAdapterUtil.getApp().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static String md5(String... strs) {
        if (strs == null || strs.length == 0) {
            throw new RuntimeException("请输入需要加密的字符串!");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            boolean isNeedThrowNotNullException = true;
            for (String str : strs) {
                if (!TextUtils.isEmpty(str)) {
                    isNeedThrowNotNullException = false;
                    md.update(str.getBytes());
                }
            }
            if (isNeedThrowNotNullException) {
                throw new RuntimeException("请输入需要加密的字符串!");
            }
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 显示吐司
     */
    public static void show(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            if (text.length() < 10) {
                Toast.makeText(BGABaseAdapterUtil.getApp(), text, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BGABaseAdapterUtil.getApp(), text, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 显示吐司
     */
    public static void show(@StringRes int resId) {
        show(BGABaseAdapterUtil.getApp().getString(resId));
    }

    /**
     * 在子线程中显示吐司时使用该方法
     */
    public static void showSafe(final CharSequence text) {
        runInUIThread(new Runnable() {
            @Override
            public void run() {
                show(text);
            }
        });
    }

    /**
     * 在子线程中显示吐司时使用该方法
     */
    public static void showSafe(@StringRes int resId) {
        showSafe(BGABaseAdapterUtil.getApp().getString(resId));
    }
}