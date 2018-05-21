package com.qiushi.wechatshop.util.oss;

import com.google.gson.annotations.SerializedName;

import java.io.File;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class UploadFile extends RealmObject {
    @PrimaryKey
    private String Md5;
    @Ignore
    private File file;
    @SerializedName("id")
    private long OssId;
    @SerializedName("dir")
    private String OssPath;

//    public static void create(final File file, final Callback<UploadFile> callback) {
//        final String md5 = MD5.getMD5(file);
//        ApiService.Creator.get().gUploadFile(md5)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap(new FlatMapResponse<TopResponse<UploadFile>>())
//                .flatMap(new FlatMapTopRes<UploadFile>())
//                .doOnRequest(new Action1<Long>() {//发起请求
//                    @Override
//                    public void call(Long aLong) {
//                        if (callback != null)
//                            callback.onStart();
//                    }
//                })
//                .doOnTerminate(new Action0() {
//                    @Override
//                    public void call() {
//                        if (callback != null)
//                            callback.onAfter();
//                    }
//                })
//                .subscribe(new Subscriber<UploadFile>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        if (callback != null)
//                            callback.onFail(Error.get(e));
//                    }
//
//                    @Override
//                    public void onNext(UploadFile uploadFile) {
//                        uploadFile.file = file;
//                        uploadFile.Md5 = md5;
//                        if (callback != null)
//                            callback.onSuccess(uploadFile);
//                    }
//                });
//    }

    public String getMd5() {
        return Md5;
    }

    public void setMd5(String md5) {
        Md5 = md5;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getOssId() {
        return OssId;
    }

    public void setOssId(long ossId) {
        OssId = ossId;
    }

    public String getOssPath() {
        return OssPath;
    }

    public void setOssPath(String ossPath) {
        OssPath = ossPath;
    }
}