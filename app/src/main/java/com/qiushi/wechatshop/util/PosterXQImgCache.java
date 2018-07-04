package com.qiushi.wechatshop.util;

import java.util.ArrayList;
import java.util.List;

public class PosterXQImgCache {
    private List<String> imgCache = new ArrayList<>();//用于存放保存后的图片路径
    private static final PosterXQImgCache instance = new PosterXQImgCache();


    public static PosterXQImgCache getInstance() {
        return instance;
    }

    public List<String> getImgCache() {
        return imgCache;
    }

    public void setImgCache(String path) {//传入保存后的图片绝对路径
        imgCache.add(path);
    }

    public void removeImgCache() {//清空缓存
        imgCache.clear();
    }
}
