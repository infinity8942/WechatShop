package com.qiushi.wechatshop.util.oss;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class UploadManager implements OnUploadListener {

    private static UploadManager instance;
    private Queue<File> queue;
    private List<OnUploadListener> listeners;

    public static UploadManager getInstance() {
        if (instance == null) {
            synchronized (UploadManager.class) {
                if (instance == null) {
                    instance = new UploadManager();
                }
            }
        }
        return instance;
    }

    private UploadManager() {
        queue = new LinkedList<>();
        listeners = new ArrayList<>();
    }

    public void register(OnUploadListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unregister(OnUploadListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    /**
     * 添加任务
     */
    public void add(File file) {
        if (file == null)
            return;
        queue.offer(file);
        UploadService.start();
    }

    public File poll() {
        return queue.poll();
    }

    /**
     * 添加任务
     */
    public void add(List<File> files) {
        if (files == null || files.isEmpty())
            return;
        for (File file : files) {
            queue.offer(file);
        }
        UploadService.start();
    }

    /**
     * 移除任务
     */
    public UploadManager remove(File file) {
        try {
            queue.remove(file);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 一些监听 存在子父级别的fragment关系中。父级一个，子级中一个。onDestroy() 不会unregister listener的。导致两个接听都会走，所以手动排除一下
     */
    public void removeOthers(OnUploadListener onUploadListener) {
        if (listeners.size() == 1 && listeners.contains(onUploadListener)) return;
        for (int i = 0; i < listeners.size(); i++) {
            if (listeners.get(i) != onUploadListener) {
                unregister(listeners.get(i));
            }
        }
    }

    public UploadManager clear() {
        queue.clear();
        return this;
    }

    @Override
    public void onProgress(File file, long currentSize, long totalSize) {
        for (OnUploadListener listener : listeners) {
            listener.onProgress(file, currentSize, totalSize);
        }
    }

    @Override
    public void onSuccess(File file, long id) {
        for (OnUploadListener listener : listeners) {
            listener.onSuccess(file, id);
        }
    }

    @Override
    public void onFailure(File file,Error error) {
        for (OnUploadListener listener : listeners) {
            listener.onFailure(file, error);
        }
    }
}