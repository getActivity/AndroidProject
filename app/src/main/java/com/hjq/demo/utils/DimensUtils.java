package com.hjq.demo.utils;

import android.content.Context;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 尺寸转换工具类
 */
public final class DimensUtils {

    /**
     * dp转px
     *
     * @param context       上下文
     * @param dpValue       dp值
     * @return              px值
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * sp转px
     * @param context       上下文
     * @param spValue       sp值
     * @return              px值
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
