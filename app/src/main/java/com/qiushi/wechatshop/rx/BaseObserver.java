package com.qiushi.wechatshop.rx;

import com.qiushi.wechatshop.net.BaseResponse;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by Rylynn on 2018-05-22.
 */
public abstract class BaseObserver<T> extends DisposableObserver<BaseResponse<T>> {
    @Override
    public void onNext(BaseResponse<T> value) {
        if (value.isSuccess()) {
            T t = value.getData();
            onHandleSuccess(t);
        } else {
            onHandleError(value.getInfo());
        }
    }

    @Override
    protected void onStart() {
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable e) {
//        ToastUtils.showError(ExceptionHandle.handleException(e), ExceptionHandle.errorCode);
    }

    protected abstract void onHandleSuccess(T t);

    protected void onHandleError(String msg) {
    }
}