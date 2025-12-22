package com.hjq.demo.app;

import android.app.Application;
import com.hjq.core.manager.ActivityManager;
import com.hjq.demo.aop.Log;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.manager.InitManager;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 应用入口
 */
public final class AppApplication extends Application {

    @Log("启动耗时")
    @Override
    public void onCreate() {
        super.onCreate();

        // 如果当前的进程不是主进程的话，则不进行第三方框架的初始化
        if (!ActivityManager.isMainProcess(this)) {
            return;
        }

        InitManager.preInitSdk(this);
        if (InitManager.isAgreePrivacy(this)) {
            InitManager.initSdk(this);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // 清理所有图片内存缓存
        GlideApp.get(this).onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // 根据手机内存剩余情况清理图片内存缓存
        GlideApp.get(this).onTrimMemory(level);
    }
}