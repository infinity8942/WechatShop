package com.qiushi.wechatshop.util.oss;

import com.google.gson.annotations.SerializedName;
import com.qiushi.wechatshop.ApiService;
import com.qiushi.wechatshop.net.BaseResponse;
import com.qiushi.wechatshop.net.RetrofitManager;
import com.qiushi.wechatshop.rx.BaseObserver;
import com.qiushi.wechatshop.rx.SchedulerUtils;
import com.qiushi.wechatshop.util.MD5;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscriber;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import retrofit2.Callback;

public class UploadFile extends RealmObject {
    @PrimaryKey
    private String Md5;
    @Ignore
    private File file;
    @SerializedName("id")
    private long OssId;
    @SerializedName("dir")
    private String OssPath;

    public static void create(final File file, final com.qiushi.wechatshop.util.oss.Callback.SimpleCallback<UploadFile> callback) {
        final String md5 = MD5.getMD5(file);


        RetrofitManager.INSTANCE.getService().gUploadFile(md5)
                .compose(SchedulerUtils.INSTANCE.<BaseResponse<UploadFile>>ioToMain())
                .subscribeWith(new BaseObserver<UploadFile>() {
                    @Override
                    protected void onHandleError(@NotNull com.qiushi.wechatshop.net.exception.Error error) {
//                        if (callback != null)
//                            callback.onFail(Error.get(e));
                    }

                    @Override
                    protected void onHandleSuccess(UploadFile uploadFile) {
                        callback.onSuccess(uploadFile);
                    }

                    @Override
                    protected void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        super.onError(e);
                    }
                });



    }

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