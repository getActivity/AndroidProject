package com.hjq.baselibrary.utils;

import android.os.SystemClock;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 防多击工具类
 */
public final class OnClickUtils {

    private static final long[] ONCLICK_TIME = new long[2]; //数组的长度为2代表只能双击

    /**
     * 是否在短时间内进行了双击操作
     */
    public static boolean isOnDoubleClick() {
        System.arraycopy(ONCLICK_TIME, 1, ONCLICK_TIME, 0, ONCLICK_TIME.length - 1);
        ONCLICK_TIME[ONCLICK_TIME.length - 1] = SystemClock.uptimeMillis();
        if (ONCLICK_TIME[0] >= (SystemClock.uptimeMillis() - 1500)) {
            return true;
        }else {
            return false;
        }
    }
}
