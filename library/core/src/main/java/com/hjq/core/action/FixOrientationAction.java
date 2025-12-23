package com.hjq.core.action;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import com.hjq.core.tools.AndroidVersion;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2022/01/20
 *    desc   : 修复透明 Activity 在 Android 8.0 固定屏幕出现崩溃的问题
 */
public interface FixOrientationAction {

    /**
     * 是否允许 Activity 设置显示方向
     */
    default boolean isAllowOrientation(@NonNull Activity activity) {
        if (AndroidVersion.getSdkVersion() != AndroidVersion.ANDROID_8) {
            return true;
        }
        return !isTranslucentOrFloating(activity);
    }

    /**
     * 判断 Activity 是否为半透明或者浮动
     */
    @SuppressLint("PrivateApi")
    @SuppressWarnings({"JavaReflectionMemberAccess", "ConstantConditions"})
    default boolean isTranslucentOrFloating(@NonNull Activity activity) {
        TypedArray typedArray = null;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            typedArray = activity.obtainStyledAttributes(styleableRes);
            Method method = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            method.setAccessible(true);
            return (boolean) method.invoke(null, typedArray);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
        return false;
    }

    /**
     * 修正在清单文件中给透明 Activity 固定方向后出现崩溃的问题，具体修复方式如下：
     * 通过反射将 screenOrientation 设置成 SCREEN_ORIENTATION_UNSPECIFIED，从而绕开系统的检查
     *
     * java.lang.IllegalStateException: Only fullscreen opaque activities can request orientation
     */
    @SuppressLint("DiscouragedPrivateApi")
    @SuppressWarnings("JavaReflectionMemberAccess")
    default void fixScreenOrientation(@NonNull Activity activity) {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo activityInfo = (ActivityInfo) field.get(activity);
            if (activityInfo != null) {
                activityInfo.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}