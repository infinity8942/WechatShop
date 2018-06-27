package com.qiushi.wechatshop.model;

import com.qiushi.wechatshop.util.RxBus;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;


public class Notifycation {

    private static Flowable<Notifycation> observable;

    public static Subscription register(Consumer<Notifycation> action1) {
        if (observable == null) {
            observable = RxBus.getInstance().register(Notifycation.class);
        }
        return (Subscription) observable.subscribe(action1, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }

        });
    }

    private int code;
    private long id;

    public Notifycation(int code, long id) {
        this.code = code;
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


}
