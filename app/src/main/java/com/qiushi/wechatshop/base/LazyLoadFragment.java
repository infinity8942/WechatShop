package com.qiushi.wechatshop.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public abstract class LazyLoadFragment extends WFragment {

    protected boolean isViewPrepare;//View是否加载完毕
    protected boolean hasLoadData;//数据是否加载过了

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            lazyLoadData();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewPrepare = true;// 视图加载完成了, 更新标记位
        initView();
        lazyLoadData();
    }

    private void lazyLoadData() {
        if (getUserVisibleHint() && isViewPrepare && !hasLoadData) {
            lazyLoad();
            hasLoadData = true;// 避免重试加载数据, 更新标记位
        }
    }

    protected abstract void initView();

    protected abstract void lazyLoad();
}