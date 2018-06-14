package com.qiushi.wechatshop.rx

import com.orhanobut.logger.Logger
import com.qiushi.wechatshop.net.BaseResponse
import com.qiushi.wechatshop.net.exception.Error
import com.qiushi.wechatshop.net.exception.ExceptionHandler

import io.reactivex.observers.DisposableObserver

/**
 * Created by Rylynn on 2018-05-22.
 */
abstract class BaseObserver<T> : DisposableObserver<BaseResponse<T>>() {
    override fun onNext(value: BaseResponse<T>) {
        if (value.isSuccess()) {
            val t = value.data
            onHandleSuccess(t)
        } else {
            if(value.msg!=null)
            onHandleError(Error(value?.code, value?.msg))
//            Logger.e("onError = " + value.code + " " + value.msg)
        }
    }

    override fun onStart() {}

    override fun onComplete() {}

    override fun onError(e: Throwable) {
        onHandleError(ExceptionHandler.handleException(e))
    }

    protected abstract fun onHandleSuccess(t: T)

    protected abstract fun onHandleError(error: Error)
}