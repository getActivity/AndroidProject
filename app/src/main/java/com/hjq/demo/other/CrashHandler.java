package com.hjq.demo.other;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hjq.demo.ui.activity.CrashActivity;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/02/03
 *    desc   : Crash 处理类
 */
public final class CrashHandler implements Thread.UncaughtExceptionHandler {

    /**
     * 注册 Crash 监听
     */
    public static void register(Application application) {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(application));
    }

    private Application mApplication;
    private Thread.UncaughtExceptionHandler mOldHandler;

    private CrashHandler(Application application) {
        mApplication = application;
        mOldHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (getClass().getName().equals(mOldHandler.getClass().getName())) {
            // 请不要重复注册 Crash 监听
            throw new IllegalStateException("are you ok?");
        }
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        CrashActivity.start(mApplication, throwable);
        // 不去触发系统的崩溃处理（com.android.internal.os.RuntimeInit$KillApplicationHandler）
        if (mOldHandler != null && !mOldHandler.getClass().getName().startsWith("com.android.internal.os")) {
            mOldHandler.uncaughtException(thread, throwable);
        }
        // 杀死进程（这个事应该是系统干的，但是它会多弹出一个崩溃对话框，所以需要我们自己手动杀死进程）
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}