package com.qiushi.wechatshop.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.LongSparseArray;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.gyf.barlibrary.ImmersionBar;
import com.qiushi.wechatshop.model.Notification;
import com.qiushi.wechatshop.util.ToastUtils;
import com.qiushi.wechatshop.view.LoadingDialog;

import java.lang.ref.WeakReference;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public abstract class WActivity extends SwipeBackActivity implements View.OnClickListener, Action1<Notification> {

    private static volatile LongSparseArray<WeakReference<WActivity>> activities = new LongSparseArray<>();

    private volatile boolean exit = false;

    public WeakHandler mHandler;

    private final Runnable resetExit = new Runnable() {
        @Override
        public void run() {
            exit = false;
        }
    };

    private long id;
    public CompositeSubscription mCompositeSubscription = new CompositeSubscription();//订阅集合
    public Subscription notification;

    public LoadingDialog loadingDialog = null;
    public ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImmersionBar = ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                .navigationBarColor(android.R.color.white);
        mImmersionBar.init();

        mHandler = new WeakHandler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                WActivity.this.handleMessage(msg);
                return false;
            }
        });
        notification = Notification.register(this);
        activities.put(getId(), new WeakReference<>(this));
        getParams(getIntent());
        setContentView(layoutId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            activities.remove(id);
            notification.unsubscribe();
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onDestroy() {
        if (mImmersionBar != null)
            mImmersionBar.destroy();  //不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
        if (null != mCompositeSubscription && mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
        dismissLoading();
        loadingDialog = null;
        super.onDestroy();
    }

    @Override
    public void call(Notification notification) {
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getParams(intent);
        setIntent(intent);
    }

    @Override
    public void onBackPressed() {
        if (activities.size() == 1 && !exit) {
            exit = true;
            ToastUtils.showMessage("再按一次退出程序");
            mHandler.postDelayed(resetExit, 2000);
        } else {
            super.onBackPressed();
        }
    }

    protected void handleMessage(Message msg) {
    }

    protected final void showLoading() {
        showLoading("正在加载...");
    }

    protected final void showLoading(String msg) {
        if (null == loadingDialog) {
            loadingDialog = new LoadingDialog(this, msg);
        } else {
            loadingDialog.setText(msg);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    protected final void dismissLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public abstract int layoutId();

    public void getParams(Intent intent) {
    }

    public void onClick(View view) {
    }

    /**
     * 获取状态栏高度
     */
    protected int getStateHeight() {
        int stateHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            stateHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return stateHeight;
    }

    protected final long getId() {
        if (id == 0) {
            id = createId();
        }
        return id;
    }

    private synchronized long createId() {
        long id = System.currentTimeMillis();
        while (activities.indexOfKey(id) >= 0) {
            id++;
        }
        return id;
    }

    public static WActivity findTopActivity() {
        if (activities.size() == 0) {
            return null;
        }

        WeakReference<WActivity> weakReference = activities.valueAt(activities.size() - 1);
        if (weakReference != null && weakReference.get() != null && !weakReference.get().isFinishing()) {
            return weakReference.get();
        }
        return null;
    }
}