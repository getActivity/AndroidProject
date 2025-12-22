package com.hjq.core.action;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/15
 *    desc   : Handler 意图处理
 */
public interface HandlerAction {

    Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 获取 Handler 对象
     */
    static Handler getHandler() {
        return HANDLER;
    }

    /**
     * 延迟执行
     */
    default boolean post(Runnable runnable) {
        return postDelayed(runnable, 0);
    }

    /**
     * 延迟一段时间执行
     */
    default boolean postDelayed(Runnable runnable, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return postAtTime(runnable, SystemClock.uptimeMillis() + delayMillis);
    }

    /**
     * 在指定的时间执行
     */
    default boolean postAtTime(Runnable runnable, long uptimeMillis) {
        // 发送和当前对象相关的消息回调
        return getHandler().postAtTime(runnable, this, uptimeMillis);
    }

    /**
     * 移除单个消息回调
     */
    default void removeCallbacks(Runnable runnable) {
        getHandler().removeCallbacks(runnable);
    }

    /**
     * 移除全部消息回调
     */
    default void removeCallbacks() {
        // 移除和当前对象相关的消息回调
        getHandler().removeCallbacksAndMessages(this);
    }
}