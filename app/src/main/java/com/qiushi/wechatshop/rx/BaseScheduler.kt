package com.qiushi.wechatshop.rx

import android.widget.Toast
import com.qiushi.wechatshop.R
import com.qiushi.wechatshop.WAppContext
import com.qiushi.wechatshop.util.NetworkUtil
import io.reactivex.*
import org.reactivestreams.Publisher

abstract class BaseScheduler<T> protected constructor(private val subscribeOnScheduler: Scheduler,
                                                      private val observeOnScheduler: Scheduler) : ObservableTransformer<T, T>,
        SingleTransformer<T, T>,
        MaybeTransformer<T, T>,
        CompletableTransformer,
        FlowableTransformer<T, T> {

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.subscribeOn(subscribeOnScheduler)
                .doOnSubscribe({
                    isNetworkAvailable()
                })
                .observeOn(observeOnScheduler)
    }

    override fun apply(upstream: Completable): CompletableSource {
        return upstream.subscribeOn(subscribeOnScheduler)
                .doOnSubscribe({
                    isNetworkAvailable()
                })
                .observeOn(observeOnScheduler)
    }

    override fun apply(upstream: Flowable<T>): Publisher<T> {
        return upstream.subscribeOn(subscribeOnScheduler)
                .doOnSubscribe({
                    isNetworkAvailable()
                })
                .observeOn(observeOnScheduler)
    }

    override fun apply(upstream: Maybe<T>): MaybeSource<T> {
        return upstream.subscribeOn(subscribeOnScheduler)
                .doOnSubscribe({
                    isNetworkAvailable()
                })
                .observeOn(observeOnScheduler)
    }

    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.subscribeOn(subscribeOnScheduler)
                .doOnSubscribe({
                    isNetworkAvailable()
                })
                .observeOn(observeOnScheduler)
    }

    private fun isNetworkAvailable() {
        if (!NetworkUtil.isNetworkAvailable(WAppContext.context!!)) {
            Toast.makeText(WAppContext.context, R.string.no_network, Toast.LENGTH_SHORT).show();
        }
    }
}