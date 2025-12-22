package com.hjq.demo.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import com.chuckerteam.chucker.api.ChuckerInterceptor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonToken;
import com.hjq.bar.TitleBar;
import com.hjq.core.manager.ActivityManager;
import com.hjq.core.tools.AndroidVersion;
import com.hjq.demo.R;
import com.hjq.demo.http.model.HttpCacheStrategy;
import com.hjq.demo.http.model.RequestHandler;
import com.hjq.demo.http.model.RequestServer;
import com.hjq.demo.other.AppConfig;
import com.hjq.demo.other.CrashHandler;
import com.hjq.demo.other.DebugLoggerTree;
import com.hjq.demo.other.MaterialHeader;
import com.hjq.demo.other.SmartBallPulseFooter;
import com.hjq.demo.other.TitleBarStyle;
import com.hjq.demo.other.ToastInterceptor;
import com.hjq.demo.other.ToastStyle;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.gson.factory.ParseExceptionCallback;
import com.hjq.http.EasyConfig;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.hjq.http.request.HttpRequest;
import com.hjq.toast.Toaster;
import com.hjq.umeng.sdk.UmengClient;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tencent.bugly.library.Bugly;
import com.tencent.bugly.library.BuglyBuilder;
import com.tencent.mmkv.MMKV;
import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 *     author : Android 轮子哥
 *     github : https://github.com/getActivity/AndroidProject
 *     time    : 2023/06/24
 *     desc    : 初始化管理器
 */
public final class InitManager {

    /** 隐私政策配置文件 */
    private static final String AGREE_PRIVACY_NAME = "agree_privacy_config";
    /** 隐私政策同意结果 */
    private static final String KEY_AGREE_PRIVACY_RESULT = "key_agree_privacy_result";

    /**
     * 是否同意了隐私协议
     */
    public static boolean isAgreePrivacy(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AGREE_PRIVACY_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_AGREE_PRIVACY_RESULT, false);
    }

    /**
     * 设置隐私协议结果
     */
    public static void setAgreePrivacy(@NonNull Context context, boolean result) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AGREE_PRIVACY_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(KEY_AGREE_PRIVACY_RESULT, result).apply();
    }

    /**
     * 预初始化第三方 SDK
     */
    public static void preInitSdk(@NonNull Application application) {
        // 初始化日志打印
        if (AppConfig.isLogEnable()) {
            Timber.plant(new DebugLoggerTree());
        }

        // 设置标题栏全局样式
        TitleBar.setGlobalStyle(new TitleBarStyle());

        // 设置全局的 Header 构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) ->
            new MaterialHeader(context).setColorSchemeColors(ContextCompat.getColor(context, R.color.common_accent_color)));
        // 设置全局的 Footer 构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new SmartBallPulseFooter(context));
        // 设置全局初始化器
        SmartRefreshLayout.setDefaultRefreshInitializer((context, layout) -> {
            // 刷新头部是否跟随内容偏移
            layout.setEnableHeaderTranslationContent(true)
                // 刷新尾部是否跟随内容偏移
                .setEnableFooterTranslationContent(true)
                // 加载更多是否跟随内容偏移
                .setEnableFooterFollowWhenNoMoreData(true)
                // 内容不满一页时是否可以上拉加载更多
                .setEnableLoadMoreWhenContentNotFull(false)
                // 仿苹果越界效果开关
                .setEnableOverScrollDrag(false);

            // 关闭框架预埋的彩蛋
            // https://github.com/scwang90/SmartRefreshLayout/issues/1105
            layout.getLayout().setTag("close egg");
        });

        // 初始化吐司
        Toaster.init(application, new ToastStyle());
        // 设置调试模式
        Toaster.setDebugMode(AppConfig.isDebug());
        // 设置 Toast 拦截器
        Toaster.setInterceptor(new ToastInterceptor());

        // 本地异常捕捉
        CrashHandler.register(application);

        // Bugly 异常捕捉
        BuglyBuilder builder = new BuglyBuilder(AppConfig.getBuglyId(), AppConfig.getBuglyKey());
        builder.debugMode = AppConfig.isDebug();
        Bugly.init(application, builder);

        // Activity 栈管理初始化
        ActivityManager.getInstance().init(application);

        // MMKV 初始化
        MMKV.initialize(application);

        // 网络请求框架初始化
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(new ChuckerInterceptor(application))
            .build();

        EasyConfig.with(okHttpClient)
            // 是否打印日志
            .setLogEnabled(AppConfig.isLogEnable())
            // 设置服务器配置
            .setServer(new RequestServer())
            // 设置请求处理策略
            .setHandler(new RequestHandler(application))
            // 设置请求缓存实现策略（非必须）
            .setCacheStrategy(new HttpCacheStrategy())
            // 设置请求重试次数
            .setRetryCount(1)
            .setInterceptor(new IRequestInterceptor() {
                @Override
                public void interceptArguments(@NonNull HttpRequest<?> httpRequest,
                    @NonNull HttpParams params,
                    @NonNull HttpHeaders headers) {
                    // 添加全局请求头
                    headers.put("token", "66666666666");
                    headers.put("deviceOaid", UmengClient.getDeviceOaid());
                    headers.put("versionName", AppConfig.getVersionName());
                    headers.put("versionCode", String.valueOf(AppConfig.getVersionCode()));
                    // 添加全局请求参数
                    // params.put("6666666", "6666666");
                }
            })
            .into();

        // 设置 Json 解析容错监听
        GsonFactory.setParseExceptionCallback(new ParseExceptionCallback() {

            @Override
            public void onParseObjectException(TypeToken<?> typeToken, String fieldName, JsonToken jsonToken) {
                handlerGsonParseException("解析对象析异常：" + typeToken + "#" + fieldName + "，后台返回的类型为：" + jsonToken);
            }

            @Override
            public void onParseListItemException(TypeToken<?> typeToken, String fieldName, JsonToken listItemJsonToken) {
                handlerGsonParseException("解析 List 异常：" + typeToken + "#" + fieldName + "，后台返回的条目类型为：" + listItemJsonToken);
            }

            @Override
            public void onParseMapItemException(TypeToken<?> typeToken, String fieldName, String mapItemKey, JsonToken mapItemJsonToken) {
                handlerGsonParseException("解析 Map 异常：" + typeToken + "#" + fieldName + "，mapItemKey = " + mapItemKey + "，后台返回的条目类型为：" + mapItemJsonToken);
            }

            private void handlerGsonParseException(String message) {
                IllegalArgumentException e = new IllegalArgumentException(message);
                if (AppConfig.isDebug()) {
                    throw e;
                } else {
                    // 上报到 Bugly 错误列表中
                    Bugly.handleCatchException(Thread.currentThread(), e, e.getMessage(), null, true);
                }
            }
        });

        // 注册网络状态变化监听
        ConnectivityManager connectivityManager = ContextCompat.getSystemService(application, ConnectivityManager.class);
        if (connectivityManager != null && AndroidVersion.isAndroid7()) {
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {

                @Override
                public void onLost(@NonNull Network network) {
                    Activity topActivity = ActivityManager.getInstance().getTopActivity();
                    if (!(topActivity instanceof LifecycleOwner)) {
                        return;
                    }

                    LifecycleOwner lifecycleOwner = ((LifecycleOwner) topActivity);
                    if (lifecycleOwner.getLifecycle().getCurrentState() != Lifecycle.State.RESUMED) {
                        return;
                    }

                    Toaster.show(R.string.common_network_error);
                }
            });
        }

        // 预初始化友盟 SDK
        UmengClient.preInit(application, AppConfig.isLogEnable());
    }

    /**
     * 初始化第三方 SDK
     */
    public static void initSdk(@NonNull Application application) {
        // 友盟统计、登录、分享 SDK
        UmengClient.init(application, AppConfig.isLogEnable());
    }
}