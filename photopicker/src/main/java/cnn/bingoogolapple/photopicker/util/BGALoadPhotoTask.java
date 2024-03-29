package cnn.bingoogolapple.photopicker.util;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.bingoogolapple.photopicker.R;
import cnn.bingoogolapple.photopicker.model.BGAPhotoFolderModel;

public class BGALoadPhotoTask extends BGAAsyncTask<Void, ArrayList<BGAPhotoFolderModel>> {
    private Context mContext;
    private boolean mTakePhotoEnabled;


    public BGALoadPhotoTask(Callback<ArrayList<BGAPhotoFolderModel>> callback, Context context, boolean takePhotoEnabled) {
        super(callback);
        mContext = context.getApplicationContext();
        mTakePhotoEnabled = takePhotoEnabled;
    }

    private static boolean isNotImageFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }

        File file = new File(path);
        return !file.exists() || file.length() == 0;
    }

    @Override
    protected ArrayList<BGAPhotoFolderModel> doInBackground(Void... voids) {
        ArrayList<BGAPhotoFolderModel> imageFolderModels = new ArrayList<>();

        BGAPhotoFolderModel allImageFolderModel = new BGAPhotoFolderModel(mTakePhotoEnabled);
        allImageFolderModel.name = mContext.getString(R.string.bga_pp_all_image);
        imageFolderModels.add(allImageFolderModel);

        HashMap<String, BGAPhotoFolderModel> imageFolderModelMap = new HashMap<>();

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media.DATA},
                    MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png", "image/jpg"},
                    MediaStore.Images.Media.DATE_ADDED + " DESC"
            );

            BGAPhotoFolderModel otherImageFolderModel;
            if (cursor != null && cursor.getCount() > 0) {
                boolean firstInto = true;
                while (cursor.moveToNext()) {
                    String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    if (isNotImageFile(imagePath)) {
                        continue;
                    }

                    if (firstInto) {
                        allImageFolderModel.coverPath = imagePath;
                        firstInto = false;
                    }
                    // 所有图片目录每次都添加
                    allImageFolderModel.addLastPhoto(imagePath);

                    String folderPath = null;
                    // 其他图片目录
                    File folder = new File(imagePath).getParentFile();
                    if (folder != null) {
                        folderPath = folder.getAbsolutePath();
                    }

                    if (TextUtils.isEmpty(folderPath)) {
                        int end = imagePath.lastIndexOf(File.separator);
                        if (end != -1) {
                            folderPath = imagePath.substring(0, end);
                        }
                    }

                    if (!TextUtils.isEmpty(folderPath)) {
                        if (imageFolderModelMap.containsKey(folderPath)) {
                            otherImageFolderModel = imageFolderModelMap.get(folderPath);
                        } else {
                            String folderName = folderPath.substring(folderPath.lastIndexOf(File.separator) + 1);
                            if (TextUtils.isEmpty(folderName)) {
                                folderName = "/";
                            }
                            otherImageFolderModel = new BGAPhotoFolderModel(folderName, imagePath);
                            imageFolderModelMap.put(folderPath, otherImageFolderModel);
                        }
                        otherImageFolderModel.addLastPhoto(imagePath);
                    }
                }

                // 添加其他图片目录
                imageFolderModels.addAll(imageFolderModelMap.values());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return imageFolderModels;
    }

    public BGALoadPhotoTask perform() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return this;
    }
}