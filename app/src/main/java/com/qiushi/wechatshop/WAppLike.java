package com.qiushi.wechatshop;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.previewlibrary.ZoomMediaLoader;
import com.qiushi.wechatshop.ui.MainActivity;
import com.qiushi.wechatshop.util.NineImageLoader;
import com.qiushi.wechatshop.util.Push;
import com.qiushi.wechatshop.util.Utils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.umeng.commonsdk.UMConfigure;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;

@SuppressWarnings("unused")
public class WAppLike extends DefaultApplicationLike {

    private static final String TAG = "WechatShop";
    public static IWXAPI mWxApi;

    public WAppLike(Application application,
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
                .methodOffset(7)
                .tag(TAG)
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return Constants.IS_DEVELOPER;
            }
        });

        //友盟初始化
        UMConfigure.init(WAppContext.context, Constants.UMENG_APPKEY, Utils.getWalleChannel(),
                UMConfigure.DEVICE_TYPE_PHONE, Constants.UMENG_SECRET);
        //友盟推送
        Push.init();

        registToWX();
        if (WAppContext.application.getPackageName().equals(Utils.getProcessName()))
            init();
    }

    /**
     * 微信登录初始化
     */
    private void registToWX() {
        //AppConst.WEIXIN.APP_ID是指你应用在微信开放平台上的AppID，记得替换。
        mWxApi = WXAPIFactory.createWXAPI(WAppContext.context, Constants.WX_ID, false);
        // 将该app注册到微信

        mWxApi.registerApp(Constants.WX_ID);

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
                Log.d(TAG, "onPatchReceived 补丁下载地址 = " + patchFile);
            }

            @Override
            public void onDownloadReceived(long savedLength, long totalLength) {
                Log.d(TAG, "onDownloadReceived = " +
                        String.format(Locale.getDefault(), "%s %d%%",
                                Beta.strNotificationDownloading,
                                (int) (totalLength == 0 ? 0 : savedLength * 100 / totalLength)));
            }

            @Override
            public void onDownloadSuccess(String msg) {
                Log.d(TAG, "onDownloadSuccess 补丁下载成功");
            }

            @Override
            public void onDownloadFailure(String msg) {
                Log.d(TAG, "onDownloadFailure 补丁下载失败");
            }

            @Override
            public void onApplySuccess(String msg) {
                Log.d(TAG, "onApplySuccess 补丁应用成功");
            }

            @Override
            public void onApplyFailure(String msg) {
                Log.d(TAG, "onApplyFailure 补丁应用失败");
            }

            @Override
            public void onPatchRollback() {
                Log.d(TAG, "onPatchRollback 补丁回滚");
            }
        };

        Bugly.setIsDevelopmentDevice(WAppContext.context, Constants.IS_DEVELOPER);
        Bugly.setAppChannel(WAppContext.context, Utils.getWalleChannel());
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(WAppContext.context);
        String processName = Utils.getProcessName();
        strategy.setUploadProcess(processName == null || processName.equals(WAppContext.application.getPackageName()));
        strategy.setAppChannel(Utils.getWalleChannel());
        strategy.setAppVersion(Utils.getVersionName());
        Bugly.init(WAppContext.context, Constants.BUGLY_APPID, Constants.IS_DEVELOPER, strategy);
    }

    private void init() {
        //Realm数据库
        Realm.init(WAppContext.context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

//        Bugly.setUserId(WAppContext.context, User.getCurrent() == null ? "0" : String.valueOf(User.getCurrent().getId()));

        //Refresh Header
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                MaterialHeader header = new MaterialHeader(context);
                header.setColorSchemeColors(R.color.colorPrimary);
                return header;
            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });

        //
        ZoomMediaLoader.getInstance().init(new NineImageLoader());
    }

    @Override
    public void onTrimMemory(int level) {
        Glide.get(WAppContext.context).clearMemory();
        super.onTrimMemory(level);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        MultiDex.install(base);

        WAppContext.context = getApplication();
        WAppContext.application = getApplication();

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