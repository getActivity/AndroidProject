package com.hjq.demo.widget.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import com.hjq.core.action.ActivityAction;
import com.hjq.demo.other.AppConfig;
import com.hjq.nested.scroll.layout.NestedScrollWebView;
import java.util.Map;
import timber.log.Timber;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/24
 *    desc   : 基于原生 WebView 封装
 */
public final class BrowserView extends NestedScrollWebView
        implements LifecycleEventObserver, ActivityAction {

    static {
        // WebView 调试模式开关
        WebView.setWebContentsDebuggingEnabled(AppConfig.isDebug());
    }

    public BrowserView(@NonNull Context context) {
        this(context, null);
    }

    public BrowserView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    public BrowserView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public BrowserView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        WebSettings settings = getSettings();
        // 允许文件访问
        settings.setAllowFileAccess(true);
        // 允许网页定位
        settings.setGeolocationEnabled(true);
        // 允许保存密码
        //settings.setSavePassword(true);
        // 开启 JavaScript
        settings.setJavaScriptEnabled(true);
        // 允许网页弹对话框
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 加快网页加载完成的速度，等页面完成再加载图片
        settings.setLoadsImagesAutomatically(true);
        // 本地 DOM 存储（解决加载某些网页出现白板现象）
        settings.setDomStorageEnabled(true);
        // 解决 Android 5.0 上 WebView 默认不允许加载 Http 与 Https 混合内容
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // 不显示滚动条
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
    }

    @Override
    public void loadUrl(@NonNull String url) {
        super.loadUrl(url);
        log(String.format("loadUrl: url = %s", url));
    }

    @Override
    public void loadUrl(@NonNull String url, @NonNull Map<String, String> additionalHttpHeaders) {
        super.loadUrl(url, additionalHttpHeaders);
        log(String.format("loadUrl: url = %s, additionalHttpHeaders = %s", url, additionalHttpHeaders));
    }

    @Override
    public void loadDataWithBaseURL(@Nullable String baseUrl, @NonNull String data, @Nullable String mimeType,
                                    @Nullable String encoding, @Nullable String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
        log(String.format("loadUrl: baseUrl = %s, mimeType = %s, encoding = %s, historyUrl = %s",
                            baseUrl, mimeType, encoding, historyUrl));
    }

    @Override
    public void loadData(@NonNull String data, @Nullable String mimeType, @Nullable String encoding) {
        super.loadData(data, mimeType, encoding);
        log(String.format("loadUrl: mimeType = %s, encoding = %s", mimeType, encoding));
    }

    @Override
    public void postUrl(@NonNull String url, @NonNull byte[] postData) {
        super.postUrl(url, postData);
        log(String.format("postUrl: url = %s", url));
    }

    @Override
    public void reload() {
        super.reload();
        log(String.format("reload: url = %s", getUrl()));
    }

    /**
     * 获取当前的 url
     *
     * @return      返回原始的 url，因为有些 url 是被 WebView 解码过的
     */
    @Override
    public String getUrl() {
        String originalUrl = super.getOriginalUrl();
        // 避免开始时同时加载两个地址而导致的崩溃
        if (originalUrl == null) {
            return super.getUrl();
        }
        return originalUrl;
    }

    /**
     * 设置 WebView 生命管控（自动回调生命周期方法）
     */
    public void setLifecycleOwner(LifecycleOwner owner) {
        owner.getLifecycle().addObserver(this);
    }

    /**
     * {@link LifecycleEventObserver}
     */

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        switch (event) {
            case ON_RESUME:
                onResume();
                break;
            case ON_STOP:
                onPause();
                break;
            case ON_DESTROY:
                onDestroy();
                break;
            default:
                break;
        }
    }

    /**
     * 销毁 WebView
     */
    public void onDestroy() {
        log("onDestroy");
        // 停止加载网页
        stopLoading();
        // 清除历史记录
        clearHistory();
        // 取消监听引用
        setBrowserChromeClient(null);
        setBrowserViewClient(null);
        // 移除WebView所有的View对象
        removeAllViews();
        // 销毁此的WebView的内部状态
        destroy();
    }

    /**
     * 已过时，请使用 {@link #setBrowserViewClient(BrowserViewClient)}
     */
    @Deprecated
    @Override
    public void setWebViewClient(@NonNull WebViewClient client) {
        super.setWebViewClient(client);
    }

    public void setBrowserViewClient(@Nullable BrowserViewClient client) {
        if (client == null) {
            super.setWebViewClient(new WebViewClient());
            return;
        }
        super.setWebViewClient(client);
    }

    /**
     * 已过时，请使用 {@link #setBrowserChromeClient(BrowserChromeClient)}
     */
    @Deprecated
    @Override
    public void setWebChromeClient(@Nullable WebChromeClient client) {
        super.setWebChromeClient(client);
    }

    public void setBrowserChromeClient(@Nullable BrowserChromeClient client) {
        super.setWebChromeClient(client);
    }

    protected void log(@Nullable String message) {
        if (message == null) {
            return;
        }
        Timber.i(message);
    }
}