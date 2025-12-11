package com.hjq.demo.other;

import com.hjq.toast.ToastLogInterceptor;
import timber.log.Timber;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/11/04
 *    desc   : 自定义 Toast 拦截器（用于追踪 Toast 调用的位置）
 */
public final class ToastInterceptor extends ToastLogInterceptor {

    @Override
    protected boolean isLogEnable() {
        return AppConfig.isLogEnable();
    }

    @Override
    protected void printLog(String msg) {
        Timber.tag("Toaster");
        Timber.i(msg);
    }
}