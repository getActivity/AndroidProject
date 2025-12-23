package com.hjq.core.tools;

import android.annotation.SuppressLint;
import android.os.Build;
import androidx.annotation.ChecksSdkIntAtLeast;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2025/12/12
 *    desc   : Android 版本判断类
 */
@SuppressLint("AnnotateVersionCheck")
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
public final class AndroidVersion {

    /** {@link Build.VERSION_CODES#BAKLAVA} */
    public static final int ANDROID_16 = Build.VERSION_CODES.BAKLAVA;
    /** {@link Build.VERSION_CODES#VANILLA_ICE_CREAM} */
    public static final int ANDROID_15 = Build.VERSION_CODES.VANILLA_ICE_CREAM;
    /** {@link Build.VERSION_CODES#UPSIDE_DOWN_CAKE} */
    public static final int ANDROID_14 = Build.VERSION_CODES.UPSIDE_DOWN_CAKE;
    /** {@link Build.VERSION_CODES#TIRAMISU} */
    public static final int ANDROID_13 = Build.VERSION_CODES.TIRAMISU;
    /** {@link Build.VERSION_CODES#S_V2} */
    public static final int ANDROID_12_1 = Build.VERSION_CODES.S_V2;
    /** {@link Build.VERSION_CODES#S} */
    public static final int ANDROID_12 = Build.VERSION_CODES.S;
    /** {@link Build.VERSION_CODES#R} */
    public static final int ANDROID_11 = Build.VERSION_CODES.R;
    /** {@link Build.VERSION_CODES#Q} */
    public static final int ANDROID_10 = Build.VERSION_CODES.Q;
    /** {@link Build.VERSION_CODES#P} */
    public static final int ANDROID_9 = Build.VERSION_CODES.P;
    /** {@link Build.VERSION_CODES#O_MR1} */
    public static final int ANDROID_8_1 = Build.VERSION_CODES.O_MR1;
    /** {@link Build.VERSION_CODES#O} */
    public static final int ANDROID_8 = Build.VERSION_CODES.O;
    /** {@link Build.VERSION_CODES#N_MR1} */
    public static final int ANDROID_7_1 = Build.VERSION_CODES.N_MR1;
    /** {@link Build.VERSION_CODES#N} */
    public static final int ANDROID_7 = Build.VERSION_CODES.N;

    private AndroidVersion() {}

    /**
     * 获取当前 SDK 版本
     */
    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 是否是 Android 16 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_16)
    public static boolean isAndroid16() {
        return Build.VERSION.SDK_INT >= ANDROID_16;
    }

    /**
     * 是否是 Android 15 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_15)
    public static boolean isAndroid15() {
        return Build.VERSION.SDK_INT >= ANDROID_15;
    }

    /**
     * 是否是 Android 14 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_14)
    public static boolean isAndroid14() {
        return Build.VERSION.SDK_INT >= ANDROID_14;
    }

    /**
     * 是否是 Android 13 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_13)
    public static boolean isAndroid13() {
        return Build.VERSION.SDK_INT >= ANDROID_13;
    }

    /**
     * 是否是 Android 12.1（又称 Android 12L）及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_12_1)
    public static boolean isAndroid12_1() {
        return Build.VERSION.SDK_INT >= ANDROID_12_1;
    }

    /**
     * 是否是 Android 12 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_12)
    public static boolean isAndroid12() {
        return Build.VERSION.SDK_INT >= ANDROID_12;
    }

    /**
     * 是否是 Android 11 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_11)
    public static boolean isAndroid11() {
        return Build.VERSION.SDK_INT >= ANDROID_11;
    }

    /**
     * 是否是 Android 10 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_10)
    public static boolean isAndroid10() {
        return Build.VERSION.SDK_INT >= ANDROID_10;
    }

    /**
     * 是否是 Android 9.0 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_9)
    public static boolean isAndroid9() {
        return Build.VERSION.SDK_INT >= ANDROID_9;
    }

    /**
     * 是否是 Android 8.1 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_8_1)
    public static boolean isAndroid8_1() {
        return Build.VERSION.SDK_INT >= ANDROID_8_1;
    }

    /**
     * 是否是 Android 8.0 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_8)
    public static boolean isAndroid8() {
        return Build.VERSION.SDK_INT >= ANDROID_8;
    }

    /**
     * 是否是 Android 7.1 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_7_1)
    public static boolean isAndroid7_1() {
        return Build.VERSION.SDK_INT >= ANDROID_7_1;
    }

    /**
     * 是否是 Android 7.0 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_7)
    public static boolean isAndroid7() {
        return Build.VERSION.SDK_INT >= ANDROID_7;
    }
}