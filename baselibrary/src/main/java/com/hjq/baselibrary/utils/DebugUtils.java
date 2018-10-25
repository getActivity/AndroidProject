package com.hjq.baselibrary.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * Debug工具类
 */
public final class DebugUtils {

    /**
     * 当前是否为Debug模式
     */
    public static boolean isDebug(Context context) {
        return context.getApplicationInfo() != null
                && (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}
