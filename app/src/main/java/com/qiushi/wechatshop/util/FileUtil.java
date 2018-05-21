package com.qiushi.wechatshop.util;

import android.os.Environment;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * 文件操作工具类
 * Created by Rylynn on 2017/12/19.
 */
public class FileUtil {
    public static File getRootFile() {
        File root = new File(Environment.getExternalStorageDirectory(), "Top6000");
        if (!root.exists()) {
            root.mkdirs();
        }
        return root;
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            File myFilePath = new File(folderPath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (!file.isDirectory()) {
            return false;
        }
        String[] tempList = file.list();
        File temp;
        for (String str : tempList) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + str);
            } else {
                temp = new File(path + File.separator + str);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + str);//先删除文件夹里面的文件
                delFolder(path + "/" + str);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 上传图片时的临时保存路径
     */
    public static File getUploadTempFolder() {
        File uploadFolder = new File(FileUtil.getRootFile(), "upload");
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        //在系统相册中隐藏文件夹
        File nomedia = new File(uploadFolder + "/.nomedia");
        if (!nomedia.exists()) {
            try {
                nomedia.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return uploadFolder;
    }

    public static String FormatFileSize(File file) {
        long fileS = getFileSizes(file);
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    private static long getFileSizes(File file) {
        long size = 0;
        File files[] = file.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) {
                    size = size + getFileSizes(f);
                } else {
                    try {
                        size = size + getFileSize(f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        return size;
    }

    private static long getFileSize(File file) {
        long size = 0;
        FileInputStream fis = null;
        if (file.exists()) {
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (Exception e) {
            } finally {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return size;
    }

    /**
     * 根据图片地址转换为base64编码字符串
     */
    public static String getImageBase64(String imgFile) {
        InputStream inputStream;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(data, Base64.DEFAULT);
    }
}