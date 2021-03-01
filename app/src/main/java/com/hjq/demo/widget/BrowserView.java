package com.hjq.demo.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.hjq.base.BaseActivity;
import com.hjq.base.BaseDialog;
import com.hjq.base.action.ActivityAction;
import com.hjq.demo.R;
import com.hjq.demo.other.AppConfig;
import com.hjq.demo.other.PermissionCallback;
import com.hjq.demo.ui.dialog.HintDialog;
import com.hjq.demo.ui.dialog.InputDialog;
import com.hjq.demo.ui.dialog.MessageDialog;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.widget.layout.NestedScrollWebView;

import java.util.List;

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

    public BrowserView(Context context) {
        this(context, null);
    }

    public BrowserView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    public BrowserView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(getFixedContext(context), attrs, defStyleAttr, 0);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public BrowserView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
     *
     * doc：https://stackoverflow.com/questions/41025200/android-view-inflateexception-error-inflating-class-android-webkit-webview
     */
    private static Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // 为什么不用 ContextImpl，因为使用 ContextImpl 获取不到 Activity 对象，而 ContextWrapper 可以
            // 这种写法返回的 Context 是 ContextImpl，而不是 Activity 或者 ContextWrapper
            // return context.createConfigurationContext(new Configuration());
            // 如果使用 ContextWrapper 还是导致崩溃，因为 Resources 对象冲突了
            // return new ContextWrapper(context);
            // 如果使用 ContextThemeWrapper 就没有问题，因为它重写了 getResources 方法，返回的是一个新的 Resources 对象
            return new ContextThemeWrapper(context, context.getTheme());
        }
        return context;
    }

    /**
     * 获取当前的 url
     *
     * @return      返回原始的 url，因为有些url是被WebView解码过的
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
                resumeTimers();
                break;
            case ON_PAUSE:
                onPause();
                pauseTimers();
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
     * 已过时，推荐使用 {@link BrowserViewClient}
     */
    @Deprecated
    @Override
    public void setWebViewClient(@NonNull WebViewClient client) {
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
         * 网站证书校验错误
         */
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Context context = view.getContext();
            if (context == null) {
                return;
            }

            new MessageDialog.Builder(context)
                    .setMessage(R.string.common_web_ssl_error_title)
                    .setConfirm(R.string.common_web_ssl_error_allow)
                    .setCancel(R.string.common_web_ssl_error_reject)
                    .setCancelable(false)
                    .setListener(new MessageDialog.OnListener() {

                        @Override
                        public void onConfirm(BaseDialog dialog) {
                            handler.proceed();
                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                            handler.cancel();
                        }
                    })
                    .show();
        }

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

        /**
         * 加载错误
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
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
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Timber.i("WebView shouldOverrideUrlLoading：%s", url);
            String scheme = Uri.parse(url).getScheme();
            if (scheme == null) {
                return true;
            }
            switch (scheme) {
                // 如果这是跳链接操作
                case "http":
                case "https":
                    view.loadUrl(url);
                    break;
                // 如果这是打电话操作
                case "tel":
                    dialing(view, url);
                    break;
                default:
                    break;
            }
            // 已经处理该链接请求
            return true;
        }

        /**
         * 跳转到拨号界面
         */
        protected void dialing(WebView view, String url) {
            Context context = view.getContext();
            if (context == null) {
                return;
            }

            new MessageDialog.Builder(context)
                    .setMessage(String.format(view.getResources().getString(R.string.common_web_call_phone_title), url.replace("tel:", "")))
                    .setConfirm(R.string.common_web_call_phone_allow)
                    .setCancel(R.string.common_web_call_phone_reject)
                    .setCancelable(false)
                    .setListener(dialog -> {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    })
                    .show();
        }
    }

    public static class BrowserChromeClient extends WebChromeClient {

        private final BrowserView mWebView;

        public BrowserChromeClient(BrowserView view) {
            mWebView = view;
            if (mWebView == null) {
                throw new IllegalArgumentException("are you ok?");
            }
        }

        /**
         * 网页弹出警告框
         */
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Activity activity = mWebView.getActivity();
            if (activity == null) {
                return false;
            }

            new HintDialog.Builder(activity)
                    .setIcon(HintDialog.ICON_WARNING)
                    .setMessage(message)
                    .setCancelable(false)
                    .addOnDismissListener(dialog -> result.confirm())
                    .show();
            return true;
        }

        /**
         * 网页弹出确定取消框
         */
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            Activity activity = mWebView.getActivity();
            if (activity == null) {
                return false;
            }

            new MessageDialog.Builder(activity)
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

        /**
         * 网页弹出输入框
         */
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            Activity activity = mWebView.getActivity();
            if (activity == null) {
                return false;
            }

            new InputDialog.Builder(activity)
                    .setContent(defaultValue)
                    .setHint(message)
                    .setCancelable(false)
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

        /**
         * 网页请求定位功能
         * 测试地址：https://map.baidu.com/
         */
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            Activity activity = mWebView.getActivity();
            if (activity == null) {
                return;
            }

            new MessageDialog.Builder(activity)
                    .setMessage(R.string.common_web_location_permission_title)
                    .setConfirm(R.string.common_web_location_permission_allow)
                    .setCancel(R.string.common_web_location_permission_reject)
                    .setCancelable(false)
                    .setListener(new MessageDialog.OnListener() {

                        @Override
                        public void onConfirm(BaseDialog dialog) {
                            XXPermissions.with(activity)
                                    .permission(Permission.ACCESS_FINE_LOCATION)
                                    .permission(Permission.ACCESS_COARSE_LOCATION)
                                    .request(new PermissionCallback() {

                                        @Override
                                        public void onGranted(List<String> permissions, boolean all) {
                                            if (all) {
                                                callback.invoke(origin, true, true);
                                            }
                                        }
                                    });
                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                            callback.invoke(origin, false, true);
                        }
                    })
                    .show();
        }

        /**
         * 网页弹出选择文件请求
         * 测试地址：https://app.xunjiepdf.com/jpg2pdf/、http://www.script-tutorials.com/demos/199/index.html
         *
         * @param callback              文件选择回调
         * @param params                文件选择参数
         */
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> callback, FileChooserParams params) {
            Activity activity = mWebView.getActivity();

            if (!(activity instanceof BaseActivity)) {
                return false;
            }

            XXPermissions.with(activity)
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(new PermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                openSystemFileChooser((BaseActivity) activity, callback, params);
                            }
                        }

                        @Override
                        public void onDenied(List<String> permissions, boolean never) {
                            super.onDenied(permissions, never);
                            callback.onReceiveValue(null);
                        }
                    });
            return true;
        }

        /**
         * 打开系统文件选择器
         */
        private void openSystemFileChooser(BaseActivity activity, ValueCallback<Uri[]> callback, FileChooserParams params) {
            Intent intent = params.createIntent();
            String[] mimeTypes = params.getAcceptTypes();
            if (mimeTypes != null && mimeTypes.length > 0 && mimeTypes[0] != null && !"".equals(mimeTypes[0])) {
                // 要过滤的文件类型
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
            // 是否是多选模式
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, params.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE);
            activity.startActivityForResult(Intent.createChooser(intent, params.getTitle()), (resultCode, data) -> {
                Uri[] uris = null;
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        // 如果用户只选择了一个文件
                        uris = new Uri[]{uri};
                    } else {
                        // 如果用户选择了多个文件
                        ClipData clipData = data.getClipData();
                        if (clipData != null) {
                            uris = new Uri[clipData.getItemCount()];
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                uris[i] = clipData.getItemAt(i).getUri();
                            }
                        }
                    }
                }
                // 不管用户最后有没有选择文件，最后必须要调用 onReceiveValue，如果没有调用就会导致网页再次点击上传无响应
                callback.onReceiveValue(uris);
            });
        }
    }
}