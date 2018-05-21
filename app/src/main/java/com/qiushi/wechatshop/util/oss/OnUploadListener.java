package com.qiushi.wechatshop.util.oss;

import java.io.File;

public interface OnUploadListener {
    void onProgress(File file, long currentSize, long totalSize);

    void onSuccess(File file, long id);

    void onFailure(File file, Error error);
}