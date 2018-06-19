package com.qiushi.wechatshop.util.share;

/**
 * Created by Rylynn on 2018-06-19.
 */
public interface Callback<Data> {
    void onStart();

    void onProgress(int progress);

    void onSuccess(Data data);

    void onFail(String error);

    void onAfter();

    abstract class SimpleCallback<Data> implements Callback<Data> {
        @Override
        public void onStart() {
        }

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onAfter() {
        }
    }
}
