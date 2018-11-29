package com.hjq.demo.common;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.hjq.baselibrary.utils.ActivityStackManager;
import com.hjq.toast.ToastUtils;
import com.umeng.analytics.MobclickAgent;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目中的Application基类
 */
public class CommonApplication extends UIApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化吐司工具类
        ToastUtils.init(this);

        // 友盟统计
        MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);

        // Activity 栈管理
        ActivityStackManager.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 使用 Dex分包
        MultiDex.install(this);
    }
}