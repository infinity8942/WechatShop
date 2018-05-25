package com.qiushi.wechatshop.test

import com.orhanobut.logger.Logger
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.net.exception.ExceptionHandler
import io.reactivex.observers.DisposableObserver

/**
 * Created by Rylynn on 2018-05-22.
 */
abstract class TestObserver<T> : DisposableObserver<TestResponse<T>>() {
    override fun onStart() {}
    override fun onComplete() {}

    override fun onNext(value: TestResponse<T>) {
        if (value.isSuccess()) {
            val t = value.newslist
            onHandleSuccess(t)
        } else {
            onHandleError(Error(value.code, value.msg))
            Logger.e("onFailed = " + value.code + " " + value.msg)
        }
    }

    override fun onError(e: Throwable) {
        onHandleError(ExceptionHandler.handleException(e))
    }

    protected abstract fun onHandleSuccess(t: T)

    protected abstract fun onHandleError(error: Error)
}