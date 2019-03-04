package com.hjq.demo.common;

import android.content.Context;
import android.support.multidex.MultiDex;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目中的Application基类
 */
public class MyApplication extends UIApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        // 为了优化启动速度，请将一些没必须一定要在 Application 初始化的第三方框架移步至 LauncherActivity 中的 initData 方法中
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 使用 Dex分包
        MultiDex.install(this);
    }
}