package com.hjq.demo.aop;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.flyjingfish.android_aop_annotation.ProceedJoinPoint;
import com.flyjingfish.android_aop_annotation.base.BasePointCut;
import com.hjq.core.manager.ActivityManager;
import com.hjq.demo.R;
import com.hjq.toast.Toaster;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/01/11
 *    desc   : 网络检测切面
 */
public class CheckNetCut implements BasePointCut<CheckNet> {

    @SuppressWarnings("deprecation")
    @Override
    public Object invoke(@NonNull ProceedJoinPoint joinPoint, @NonNull CheckNet anno) throws Throwable {
        Application application = ActivityManager.getInstance().getApplication();
        ConnectivityManager manager = ContextCompat.getSystemService(application, ConnectivityManager.class);
        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info == null || !info.isConnected()) {
                Toaster.show(R.string.common_network_hint);
                return null;
            }
        }
        return joinPoint.proceed();
    }
}