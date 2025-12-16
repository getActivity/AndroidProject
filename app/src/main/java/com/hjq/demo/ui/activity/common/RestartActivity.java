package com.hjq.demo.ui.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.ui.activity.HomeActivity;
import com.hjq.demo.ui.activity.SplashActivity;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/11/29
 *    desc   : 重启应用
 */
public final class RestartActivity extends AppActivity {

    public static void start(@NonNull Context context) {
        Intent intent = new Intent(context, RestartActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        // 这里解释一下，为什么不用 Toaster 来显示，而是用系统的来显示
        // 这是因为 Application 在初始化第三方框架前会判断当前是否是主进程
        // 如果是主进程才会初始化第三方框架，但是当前 Activity 运行在非主进程中
        Toast.makeText(this, R.string.common_crash_hint, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void initData() {
        restart(this);
        finish();
    }

    public static void restart(@NonNull Context context) {
        Intent intent;
        if (true) {
            // 如果是未登录的情况下跳转到闪屏页
            intent = new Intent(context, SplashActivity.class);
        } else {
            // 如果是已登录的情况下跳转到首页
            intent = new Intent(context, HomeActivity.class);
        }

        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}