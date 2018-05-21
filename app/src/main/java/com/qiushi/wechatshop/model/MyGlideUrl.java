package com.qiushi.wechatshop.model;

import com.bumptech.glide.load.model.GlideUrl;

/**
 * Created by Rylynn on 2018-04-11.
 * 去除阿里云oss图片地址的签名，获取图片缓存key
 */
public class MyGlideUrl extends GlideUrl {

    private String mUrl;

    public MyGlideUrl(String url) {
        super(url);
        mUrl = url;
    }

    @Override
    public String getCacheKey() {
        return mUrl.replace(deleteSignature(), "");
    }

    private String deleteSignature() {
        String tokenParam = "";
        int tokenKeyIndex = mUrl.contains("?Signature=") ? mUrl.indexOf("?Signature=") : mUrl.indexOf("&Signature=");
        if (tokenKeyIndex != -1) {
            int nextAndIndex = mUrl.indexOf("&", tokenKeyIndex + 1);
            if (nextAndIndex != -1) {
                tokenParam = mUrl.substring(tokenKeyIndex + 1, nextAndIndex + 1);
            } else {
                tokenParam = mUrl.substring(tokenKeyIndex);
            }
        }
        return tokenParam;
    }
}