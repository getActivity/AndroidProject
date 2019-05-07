package com.hjq.demo.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/02
 *    desc   : 获取清单文件中的值
 */
public final class ManifestHelper {

    /**
     * 获取 meta-data 的值
     */
    private static Bundle getMetaData(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException ignored) {
            return new Bundle();
        }
    }

    /**
     * 检查 key
     * @param context           上下文
     * @return                  meta-data
     */
    public static Bundle checkMetaData(Context context, String key) {
        Bundle metaData = getMetaData(context);
        if (!metaData.containsKey(key)) {
            // 清单文件没有设置这个 key
            throw new IllegalArgumentException("are you ok?");
        }
        return metaData;
    }

    /**
     * 是否有设置这个值
     *
     * @param context           上下文
     * @param key               key
     * @return                  value
     */
    public static boolean contains(Context context, String key) {
        return checkMetaData(context, key).containsKey(key);
    }

    /**
     * 获取 Object
     *
     * @param context           上下文
     * @param key               key
     * @return                  value
     */
    public static Object get(Context context, String key) {
        return checkMetaData(context, key).get(key);
    }

    /**
     * 获取 String
     *
     * @param context           上下文
     * @param key               key
     * @return                  value
     */
    public static String getString(Context context, String key) {
        return checkMetaData(context, key).getString(key);
    }

    /**
     * 获取 boolean
     *
     * @param context           上下文
     * @param key               key
     * @return                  value
     */
    public static boolean getBoolean(Context context, String key) {
        return checkMetaData(context, key).getBoolean(key);
    }

    /**
     * 获取 int
     *
     * @param context           上下文
     * @param key               key
     * @return                  value
     */
    public static int getInt(Context context, String key) {
        return checkMetaData(context, key).getInt(key);
    }

    /**
     * 获取 long
     *
     * @param context           上下文
     * @param key               key
     * @return                  value
     */
    public static long getLong(Context context, String key) {
        return checkMetaData(context, key).getLong(key);
    }

    /**
     * 获取 float
     *
     * @param context           上下文
     * @param key               key
     * @return                  value
     */
    public static float getFloat(Context context, String key) {
        return checkMetaData(context, key).getFloat(key);
    }

    /**
     * 获取 double
     *
     * @param context           上下文
     * @param key               key
     * @return                  value
     */
    public static double getDouble(Context context, String key) {
        return checkMetaData(context, key).getDouble(key);
    }
}