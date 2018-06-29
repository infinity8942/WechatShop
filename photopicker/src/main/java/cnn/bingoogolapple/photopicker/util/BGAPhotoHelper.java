package cnn.bingoogolapple.photopicker.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;

public class BGAPhotoHelper {
    private final static String STATE_CAMERA_FILE_PATH = "STATE_CAMERA_FILE_PATH";
    private final static String STATE_CROP_FILE_PATH = "STATE_CROP_FILE_PATH";
    private static final SimpleDateFormat PHOTO_NAME_POSTFIX_SDF = new SimpleDateFormat("yyyy-MM-dd_HH-mm_ss", Locale.getDefault());
    private File mCameraFileDir;
    private String mCameraFilePath;
    private String mCropFilePath;

    /**
     * @param cameraFileDir 拍照后图片保存的目录
     */
    public BGAPhotoHelper(File cameraFileDir) {
        mCameraFileDir = cameraFileDir;
        if (!mCameraFileDir.exists()) {
            mCameraFileDir.mkdirs();
        }
    }

    /**
     * 创建用于保存拍照生成的图片文件
     */
    private File createCameraFile() throws IOException {
        File captureFile = File.createTempFile(
                "Capture_" + PHOTO_NAME_POSTFIX_SDF.format(new Date()),
                ".jpg",
                mCameraFileDir);
        mCameraFilePath = captureFile.getAbsolutePath();
        return captureFile;
    }

    /**
     * 获取拍照意图
     */
    public Intent getTakePhotoIntent() throws IOException {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, BGAPhotoHelper.createFileUri(createCameraFile()));
        return takePhotoIntent;
    }

    /**
     * 刷新图库
     */
    public void refreshGallery() {
        if (!TextUtils.isEmpty(mCameraFilePath)) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(createFileUri(new File(mCameraFilePath)));
            BGABaseAdapterUtil.getApp().sendBroadcast(mediaScanIntent);
            mCameraFilePath = null;
        }
    }

    /**
     * 删除拍摄的照片
     */
    public void deleteCameraFile() {
        deleteFile(mCameraFilePath);
        mCameraFilePath = null;
    }

    private void deleteFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File photoFile = new File(filePath);
            photoFile.deleteOnExit();
        }
    }

    public String getCameraFilePath() {
        return mCameraFilePath;
    }

    /**
     * 根据文件创建 Uri
     */
    private static Uri createFileUri(File file) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            String authority = BGABaseAdapterUtil.getApp().getApplicationInfo().packageName + ".bga_photo_picker.file_provider";
            return BGAPhotoFileProvider.getUriForFile(BGABaseAdapterUtil.getApp(), authority, file);
        } else {
            return Uri.fromFile(file);
        }
    }

    public static void onRestoreInstanceState(BGAPhotoHelper photoHelper, Bundle savedInstanceState) {
        if (photoHelper != null && savedInstanceState != null) {
            photoHelper.mCameraFilePath = savedInstanceState.getString(STATE_CAMERA_FILE_PATH);
            photoHelper.mCropFilePath = savedInstanceState.getString(STATE_CROP_FILE_PATH);
        }
    }

    public static void onSaveInstanceState(BGAPhotoHelper photoHelper, Bundle savedInstanceState) {
        if (photoHelper != null && savedInstanceState != null) {
            savedInstanceState.putString(STATE_CAMERA_FILE_PATH, photoHelper.mCameraFilePath);
            savedInstanceState.putString(STATE_CROP_FILE_PATH, photoHelper.mCropFilePath);
        }
    }
}