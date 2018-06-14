package com.qiushi.wechatshop.util.oss;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.gson.reflect.TypeToken;
import com.qiushi.wechatshop.Constants;
import com.qiushi.wechatshop.WAppContext;
import com.qiushi.wechatshop.net.BaseResponse;

import java.io.File;
import java.util.HashMap;
import java.util.List;


public class UploadService extends IntentService implements OSSProgressCallback<PutObjectRequest>, OSSCompletedCallback<PutObjectRequest, PutObjectResult> {

    private static final int MAX_REQUEST = 5;
    private static final String BUCKET_NAME = Constants.DEBUG ? "top6000origtest" : "top6000";//TODO

    private OSS oss;

    public UploadService() {
        super("UploadService");
    }

    private OSS getOss() {
        if (oss == null) {
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(15 * 1000); // connction time out default 15s
            conf.setSocketTimeout(15 * 1000); // socket timeout，default 15s
            conf.setMaxConcurrentRequest(MAX_REQUEST); // synchronous request number，default 5
            conf.setMaxErrorRetry(2); // retry，default 2
            OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(Constants.ALI_OSS_APPKEY, Constants.ALI_OSS_SECRET, "");
            oss = new OSSClient(getApplicationContext(), Constants.OssEndPoint, credentialProvider, conf);
        }
        return oss;
    }

    public static void start() {
        if (!isServiceRunning()) {
            Intent intent = new Intent(WAppContext.context, UploadService.class);
            WAppContext.context.startService(intent);
        }
    }

    private static boolean isServiceRunning() {
        boolean isRunning = false;
        ActivityManager am =
                (ActivityManager) WAppContext.application.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return false;
        List<ActivityManager.RunningServiceInfo> serviceList
                = am.getRunningServices(Integer.MAX_VALUE);
        if (serviceList == null || serviceList.size() == 0) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(UploadService.class.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        for (int i = 0; i < MAX_REQUEST; i++) {
            if (!next()) {
                break;
            }
        }
    }

    private boolean next() {
        File file = UploadManager.getInstance().poll();
        if (file == null) {
            return false;
        } else {
            uploadFile(file);
            return true;
        }
    }

    @Override
    public void onProgress(PutObjectRequest putObjectRequest, long currentSize, long totalSize) {
        File file = new File(putObjectRequest.getUploadFilePath());
        UploadManager.getInstance().onProgress(file, currentSize, totalSize);
    }

    @Override
    public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
        next();
        File file = new File(putObjectRequest.getUploadFilePath());
        String callback = putObjectResult.getServerCallbackReturnBody();
        BaseResponse<Long> top = NetConfig.Companion.getInstance().getGson().fromJson(callback, new TypeToken<BaseResponse<Long>>() {
        }.getType());
        Log.d("tag","file"+file.getAbsolutePath());
        UploadManager.getInstance().onSuccess(file, top.getData());
    }

    @Override
    public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
        next();
        File file = new File(putObjectRequest.getUploadFilePath());
        String info = "";
        if (e != null) {
            info = e.getMessage();
            e.printStackTrace();
        }
        if (e1 != null) {
            info = info + e1.getMessage();
            e1.printStackTrace();
        }
        Error error = new Error(2000, info);
        UploadManager.getInstance().onFailure(file, error);
    }

    private void uploadFile(final File file) {
        UploadFile.create(file, new Callback.SimpleCallback<UploadFile>() {
            @Override
            public void onSuccess(UploadFile uploadFile) {
                if (uploadFile.getOssId() != 0) {
                    UploadManager.getInstance().onSuccess(uploadFile.getFile(), uploadFile.getOssId());
                    next();
                } else {
                    upFileToOss(uploadFile);
                }
            }

            @Override
            public void onFail(Error error) {
                UploadManager.getInstance().onFailure(file, error);
                next();
            }
        });
    }

    private void upFileToOss(UploadFile uploadFile) {
        PutObjectRequest put = new PutObjectRequest(BUCKET_NAME,
                uploadFile.getOssPath() + uploadFile.getFile().getName(),
                uploadFile.getFile().getPath());
        put.setCallbackParam(new HashMap<String, String>() {
            {
                put("callbackUrl", Constants.OssCallback);
                put("callbackBody", "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}&md5=${etag}&fromApp=1&type=footprint");
            }
        });
        put.setProgressCallback(this);
        getOss().asyncPutObject(put, this);
    }
}