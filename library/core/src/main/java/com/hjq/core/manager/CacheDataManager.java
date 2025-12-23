package com.hjq.core.manager;

import android.content.Context;
import android.os.Environment;
import androidx.annotation.NonNull;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/03/01
 *    desc   : 应用缓存管理
 */
public final class CacheDataManager {

    /**
     * 获取缓存大小
     */
    public static String getTotalCacheSize(@NonNull Context context) {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                cacheSize += getFolderSize(externalCacheDir);
            }
        }
        return getFormatSize(cacheSize);
    }

    /**
     * 清除缓存
     */
    public static void clearAllCache(@NonNull Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    /**
     * 删除文件夹
     */
    private static boolean deleteDir(File dir) {
        if (dir == null) {
            return false;
        }
        if (!dir.isDirectory()) {
            // noinspection ResultOfMethodCallIgnored
            return dir.delete();
        }

        String[] children = dir.list();
        if (children == null) {
            return false;
        }
        for (String child : children) {
            deleteDir(new File(dir, child));
        }
        return false;
    }

    // 获取文件大小
    // Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    // Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    private static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] list = file.listFiles();
            if (list == null) {
                return 0;
            }
            for (File temp : list) {
                // 如果下面还有文件
                if (temp.isDirectory()) {
                    size = size + getFolderSize(temp);
                } else {
                    size = size + temp.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            // return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            return new BigDecimal(kiloByte).setScale(2, RoundingMode.HALF_UP).toPlainString() + "K";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            return new BigDecimal(megaByte).setScale(2, RoundingMode.HALF_UP).toPlainString() + "M";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            return new BigDecimal(gigaByte).setScale(2, RoundingMode.HALF_UP).toPlainString() + "GB";
        }

        return new BigDecimal(teraBytes).setScale(2, RoundingMode.HALF_UP).toPlainString() + "TB";
    }
}