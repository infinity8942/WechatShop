package com.qiushi.wechatshop;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.qiushi.wechatshop.config.Constants;
import com.qiushi.wechatshop.ui.MainActivity;
import com.qiushi.wechatshop.util.Utils;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.tinker.loader.app.DefaultApplicationLike;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;

@SuppressWarnings("unused")
public class AppLike extends DefaultApplicationLike {
    public AppLike(Application application,
                   int tinkerFlags,
                   boolean tinkerLoadVerifyFlag,
                   long applicationStartElapsedTime,
                   long applicationStartMillisTime,
                   Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initBugly();//初始化热更新

        //Logger
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodCount(0)
                .tag("WechatShop")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return Constants.IS_DEVELOPER;
            }
        });

        //友盟推送
//        Push.init();

        if (AppContext.application.getPackageName().equals(Utils.getProcessName()))
            init();
    }

    private void initBugly() {
        Beta.largeIconId = R.mipmap.ic_launcher;
        Beta.smallIconId = R.mipmap.ic_launcher;
        Beta.canShowUpgradeActs.add(MainActivity.class);
        Beta.enableHotfix = true;
        Beta.showInterruptedStrategy = true;
        Beta.autoDownloadOnWifi = true;
        Beta.enableNotification = true;
        Beta.canAutoDownloadPatch = true;
        Beta.canAutoPatch = true;
        Beta.canNotifyUserRestart = true;
        Beta.betaPatchListener = new BetaPatchListener() {
            @Override
            public void onPatchReceived(String patchFile) {
                Log.d("WechatShop", "onPatchReceived 补丁下载地址 = " + patchFile);
            }

            @Override
            public void onDownloadReceived(long savedLength, long totalLength) {
                Log.d("WechatShop", "onDownloadReceived = " +
                        String.format(Locale.getDefault(), "%s %d%%",
                                Beta.strNotificationDownloading,
                                (int) (totalLength == 0 ? 0 : savedLength * 100 / totalLength)));
            }

            @Override
            public void onDownloadSuccess(String msg) {
                Log.d("WechatShop", "onDownloadSuccess 补丁下载成功");
            }

            @Override
            public void onDownloadFailure(String msg) {
                Log.d("WechatShop", "onDownloadFailure 补丁下载失败");
            }

            @Override
            public void onApplySuccess(String msg) {
                Log.d("WechatShop", "onApplySuccess 补丁应用成功");
            }

            @Override
            public void onApplyFailure(String msg) {
                Log.d("WechatShop", "onApplyFailure 补丁应用失败");
            }

            @Override
            public void onPatchRollback() {
                Log.d("WechatShop", "onPatchRollback 补丁回滚");
            }
        };

        Bugly.setIsDevelopmentDevice(AppContext.context, Constants.IS_DEVELOPER);
//        Bugly.setAppChannel(AppContext.context, Utils.getWalleChannel());
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(AppContext.context);
        String processName = Utils.getProcessName();
        strategy.setUploadProcess(processName == null || processName.equals(AppContext.application.getPackageName()));
//        strategy.setAppChannel(Utils.getWalleChannel());
        strategy.setAppVersion(Utils.getVersionName());
        Bugly.init(AppContext.context, Constants.BUGLY_APPID, Constants.IS_DEVELOPER, strategy);
    }

    private void init() {
        //Realm数据库
        Realm.init(AppContext.context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

//        Bugly.setUserId(AppContext.context, User.getCurrent() == null ? "0" : String.valueOf(User.getCurrent().getId()));

        //Refresh Header
//        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
//            @NonNull
//            @Override
//            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
//                return new MyRefreshLottieHeader(context);
//            }
//        });
    }

    @Override
    public void onTrimMemory(int level) {
        Glide.get(AppContext.context).clearMemory();
        super.onTrimMemory(level);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        MultiDex.install(base);

        AppContext.application = getApplication();
        AppContext.context = getApplication();

        Beta.installTinker(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(
            Application.ActivityLifecycleCallbacks callbacks) {
        getApplication().registerActivityLifecycleCallbacks(callbacks);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Beta.unInit();
    }
}