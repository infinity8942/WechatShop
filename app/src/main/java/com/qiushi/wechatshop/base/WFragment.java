package com.qiushi.wechatshop.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gyf.barlibrary.ImmersionBar;
import com.qiushi.wechatshop.view.LoadingDialog;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class WFragment extends Fragment {

    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ImmersionBar mImmersionBar;
    private LoadingDialog loadingDialog = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            getParams(getArguments());
    }

    protected abstract
    @LayoutRes
    int layoutId();

    @Nullable
    @Override
    public final View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId(), container, false);
        try {
            mImmersionBar = ImmersionBar.with(this).statusBarDarkFont(true, 0.2f)
                    .navigationBarColor(android.R.color.white);
            mImmersionBar.init();
        } catch (IllegalArgumentException e) {
        }
        return view;
    }

    public WFragment() {
    }

    protected void getParams(Bundle args) {
    }

    protected final void showLoading() {
        showLoading("正在加载...");
    }

    protected final void showLoading(String msg) {
        if (getContext() == null) return;
        if (null == loadingDialog) {
            loadingDialog = new LoadingDialog(getContext(), msg);
        } else {
            loadingDialog.setText(msg);
        }
        if (!loadingDialog.isShowing())
            loadingDialog.show();
    }

    protected final void dismissLoading() {
        if (loadingDialog != null && loadingDialog.isShowing())
            loadingDialog.dismiss();
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

    public void addSubscription(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    @Override
    public void onDestroy() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
        dismissLoading();
        loadingDialog = null;
        super.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();
    }
}