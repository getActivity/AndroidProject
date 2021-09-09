package com.hjq.demo.other;

import com.tencent.mmkv.MMKV;

/**
 * 腾讯文件存值 工具 2021年3月4日17:40:37
 */
public class MmkvUtil {
    private static MMKV kv;

    private static MMKV init() {
        if (kv == null) {
            kv = MMKV.defaultMMKV();
        }
        return kv;

    }

    public static boolean save(String key, boolean value) {
        init();
        return kv.encode(key, value);
    }

    public static boolean save(String key, int value) {
        init();
        return kv.encode(key, value);
    }

    public static boolean save(String key, double value) {
        init();
        return kv.encode(key, value);
    }

    public static boolean save(String key, float value) {
        init();
        return kv.encode(key, value);
    }

    public static boolean save(String key, String value) {
        init();
        return kv.encode(key, value);
    }

    public static boolean save(String key, byte[] value) {
        init();
        return kv.encode(key, value);
    }

    public static Boolean getBool(String key) {
        init();
        return kv.decodeBool(key);
    }

    public static byte[] getBytes(String key) {
        init();
        return kv.decodeBytes(key);
    }

    public static double getDouble(String key, double defaultValue) {
        init();
        return kv.decodeDouble(key, defaultValue);

    }

    public static int getInt(String key, int defaultValue) {
        init();
        return kv.decodeInt(key, defaultValue);
    }

    public static long getLong(String key, long defaultValue) {
        init();
        return kv.decodeLong(key, defaultValue);
    }

    public static String getString(String key, String defaultValue) {
        init();
        return kv.decodeString(key, defaultValue);
    }
}
