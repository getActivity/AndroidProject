package com.hjq.demo.common;

import android.app.Application;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.billy.android.swipe.SmartSwipeBack;
import com.hjq.bar.TitleBar;
import com.hjq.bar.style.TitleBarLightStyle;
import com.hjq.demo.R;
import com.hjq.demo.action.SwipeAction;
import com.hjq.demo.helper.ActivityStackManager;
import com.hjq.demo.http.model.RequestHandler;
import com.hjq.demo.http.server.ReleaseServer;
import com.hjq.demo.http.server.TestServer;
import com.hjq.demo.other.AppConfig;
import com.hjq.demo.other.CrashHandler;
import com.hjq.http.EasyConfig;
import com.hjq.http.config.IRequestServer;
import com.hjq.toast.ToastInterceptor;
import com.hjq.toast.ToastUtils;
import com.hjq.umeng.UmengClient;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.crashreport.CrashReport;

import okhttp3.OkHttpClient;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目中的 Application 基类
 */
public final class MyApplication extends Application implements LifecycleOwner {

    private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);

    @Override
    public void onCreate() {
        super.onCreate();
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        initSdk(this);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }

    /**
     * 初始化一些第三方框架
     */
    public static void initSdk(Application application) {
        // 吐司工具类
        ToastUtils.init(application);

        // 设置 Toast 拦截器
        ToastUtils.setToastInterceptor(new ToastInterceptor() {
            @Override
            public boolean intercept(Toast toast, CharSequence text) {
                boolean intercept = super.intercept(toast, text);
                if (intercept) {
                    Log.e("Toast", "空 Toast");
                } else {
                    Log.i("Toast", text.toString());
                }
                return intercept;
            }
        });

        // 初始化标题栏全局样式
        TitleBar.initStyle(new TitleBarLightStyle(application) {

            @Override
            public Drawable getBackground() {
                return new ColorDrawable(ContextCompat.getColor(application, R.color.colorPrimary));
            }

            @Override
            public Drawable getBackIcon() {
                return getDrawable(R.drawable.arrows_left_ic);
            }
        });

        // 本地异常捕捉
        CrashHandler.register(application);

        // 友盟统计、登录、分享 SDK
        UmengClient.init(application);

        // Bugly 异常捕捉
        CrashReport.initCrashReport(application, AppConfig.getBuglyId(), AppConfig.isDebug());

        // 设置全局的 Header 构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> new ClassicsHeader(context).setEnableLastTime(false));
        // 设置全局的 Footer 构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new ClassicsFooter(context).setDrawableSize(20));

        // Activity 栈管理初始化
        ActivityStackManager.getInstance().init(application);

        // 网络请求框架初始化
        IRequestServer server;
        if (AppConfig.isDebug()) {
            server = new TestServer();
        } else {
            server = new ReleaseServer();
        }

        EasyConfig.with(new OkHttpClient())
                // 是否打印日志
                //.setLogEnabled(AppConfig.isDebug())
                // 设置服务器配置
                .setServer(server)
                // 设置请求处理策略
                .setHandler(new RequestHandler(application))
                // 设置请求重试次数
                .setRetryCount(1)
                // 添加全局请求参数
                //.addParam("token", "6666666")
                // 添加全局请求头
                //.addHeader("time", "20191030")
                // 启用配置
                .into();

        // Activity 侧滑返回
        SmartSwipeBack.activitySlidingBack(application, activity -> {
            if (activity instanceof SwipeAction) {
                return ((SwipeAction) activity).isSwipeEnable();
            }
            return true;
        });
    }
}