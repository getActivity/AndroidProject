package com.hjq.demo.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.FragmentActivity;

import com.hjq.base.BaseDialog;
import com.hjq.demo.ui.dialog.InputDialog;
import com.hjq.demo.ui.dialog.MessageDialog;
import com.hjq.demo.ui.dialog.ToastDialog;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/24
 *    desc   : 基于 WebView 封装
 */
public final class BrowserView extends WebView {

    public BrowserView(Context context) {
        this(context, null);
    }

    public BrowserView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public BrowserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getFixedContext(context), attrs, defStyleAttr);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 解决 Android 5.0 上 WebView 默认不允许加载 Http 与 Https 混合内容
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 不显示滚动条
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
    }

    /**
     * 修复原生 WebView 和 AndroidX 在 Android 5.x 上面崩溃的问题
     */
    public static Context getFixedContext(Context context) {
        // 博客地址：https://blog.csdn.net/qq_34206863/article/details/103660307
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // 不用上下文
            return context.createConfigurationContext(new Configuration());
        }
        return context;
    }

    /**
     * 获取当前的url
     *
     * @return      返回原始的url,因为有些url是被WebView解码过的
     */
    @Override
    public String getUrl() {
        String originalUrl = super.getOriginalUrl();
        // 避免开始时同时加载两个地址而导致的崩溃
        if (originalUrl != null) {
            return originalUrl;
        }
        return super.getUrl();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeTimers();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseTimers();
    }

    public void onDestroy() {
        ((ViewGroup) getParent()).removeView(this);
        //清除历史记录
        clearHistory();
        //停止加载
        stopLoading();
        //加载一个空白页
        loadUrl("about:blank");
        setBrowserChromeClient(null);
        setBrowserViewClient(null);
        //移除WebView所有的View对象
        removeAllViews();
        //销毁此的WebView的内部状态
        destroy();
    }

    /**
     * 已过时，推荐使用 {@link BrowserViewClient}
     */
    @Deprecated
    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
    }

    public void setBrowserViewClient(BrowserViewClient client) {
        super.setWebViewClient(client);
    }

    /**
     * 已过时，推荐使用 {@link BrowserChromeClient}
     */
    @Deprecated
    @Override
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
    }

    public void setBrowserChromeClient(BrowserChromeClient client) {
        super.setWebChromeClient(client);
    }

    public static class BrowserViewClient extends WebViewClient {

        /**
         * 同名 API 兼容
         */
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (request.isForMainFrame()) {
                onReceivedError(view,
                        error.getErrorCode(), error.getDescription().toString(),
                        request.getUrl().toString());
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // 注意一定要去除这行代码，否则设置无效。
            //super.onReceivedSslError(view, handler, error);
            // Android默认的处理方式
            //handler.cancel();
            // 接受所有网站的证书
            handler.proceed();
        }

        /**
         * 同名 API 兼容
         */
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view, request.getUrl().toString());
        }

        /**
         * 跳转到其他链接
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            String scheme = Uri.parse(url).getScheme();
            if (scheme != null) {
                scheme = scheme.toLowerCase();
            }
            if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                view.loadUrl(url);
            }
            // 已经处理该链接请求
            return true;
        }
    }

    public static class BrowserChromeClient extends WebChromeClient {

        private final BrowserView mWebView;
        private FragmentActivity mActivity;

        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;

        public BrowserChromeClient(BrowserView view) {
            mWebView = view;
            if (view.getContext() instanceof FragmentActivity) {
                mActivity = (FragmentActivity) view.getContext();
            }
        }

        /**
         * 播放视频时进入全屏回调
         */
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (mActivity == null) {
                return;
            }
            mCustomViewCallback = callback;
            // 给Activity设置横屏
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            if (mCustomView != null) {
                mWebView.setVisibility(View.GONE);
                mCustomViewCallback.onCustomViewHidden();
                mWebView.setVisibility(View.VISIBLE);
                return;
            }

            mWebView.addView(view);
            mCustomView = view;
        }

        /**
         * 播放视频时退出全屏回调
         */
        @Override
        public void onHideCustomView() {
            // 不是全屏播放状态就不往下执行
            if (mActivity == null || mCustomView == null || mCustomViewCallback == null) {
                return;
            }

            // 给Activity设置竖屏
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            mWebView.removeView(mCustomView);
            mCustomView = null;

            mWebView.setVisibility(View.GONE);
            mCustomViewCallback.onCustomViewHidden();
            mWebView.setVisibility(View.VISIBLE);
        }

        /**
         * 弹出警告框
         */
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            if (mActivity == null) {
                return super.onJsAlert(view, url, message, result);
            } else {
                new ToastDialog.Builder(mActivity)
                        .setType(ToastDialog.Type.WARN)
                        .setMessage(message)
                        .addOnDismissListener(dialog -> result.confirm())
                        .show();
                return true;
            }
        }

        /**
         * 弹出确定取消框
         */
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            if (mActivity == null) {
                return super.onJsConfirm(view, url, message, result);
            } else {
                new MessageDialog.Builder(mActivity)
                        .setMessage(message)
                        .setCancelable(false)
                        .setListener(new MessageDialog.OnListener() {

                            @Override
                            public void onConfirm(BaseDialog dialog) {
                                result.confirm();
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                result.cancel();
                            }
                        })
                        .show();
                return true;
            }
        }

        /**
         * 弹出输入框
         */
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
            if (mActivity == null){
                return super.onJsPrompt(view, url, message, defaultValue, result);
            } else {
                new InputDialog.Builder(mActivity)
                        .setContent(defaultValue)
                        .setHint(message)
                        .setListener(new InputDialog.OnListener() {

                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                result.confirm(content);
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                result.cancel();
                            }
                        })
                        .show();
                return true;
            }
        }
    }
}