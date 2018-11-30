package com.hjq.demo.utils;

import android.os.SystemClock;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 防多击判断工具类
 */
public final class OnClickUtils {

    private static final long[] ONCLICK_TIME = new long[2]; // 数组的长度为2代表只记录双击操作
    private static final int INTERVAL_TIME = 1500; // 限定间隔时长

    /**
     * 是否在短时间内进行了双击操作
     */
    public static boolean isOnDoubleClick() {
        System.arraycopy(ONCLICK_TIME, 1, ONCLICK_TIME, 0, ONCLICK_TIME.length - 1);
        ONCLICK_TIME[ONCLICK_TIME.length - 1] = SystemClock.uptimeMillis();
        if (ONCLICK_TIME[0] >= (SystemClock.uptimeMillis() - INTERVAL_TIME)) {
            return true;
        }else {
            return false;
        }
    }
}
