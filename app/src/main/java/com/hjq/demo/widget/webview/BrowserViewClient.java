package com.hjq.demo.widget.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.base.BaseDialog;
import com.hjq.core.tools.AndroidVersion;
import com.hjq.demo.R;
import com.hjq.demo.ui.dialog.common.MessageDialog;
import timber.log.Timber;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/24
 *    desc   : 基于原生 WebViewClient 封装
 */
public class BrowserViewClient extends WebViewClient {

    private boolean mLoadingFail;
    private int mErrorCode;
    private String mDescription;
    private String mFailingUrl;

    @Override
    public final void onPageStarted(@NonNull WebView view, @NonNull String url, @Nullable Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        log(String.format("onPageStarted: url = %s", url));
        mLoadingFail = false;
        onWebPageLoadStarted(view, url, favicon);
    }

    /**
     * 同名 API 兼容
     */
    @Override
    public void onReceivedError(@NonNull WebView view, @NonNull WebResourceRequest request, @NonNull WebResourceError error) {
        if (!request.isForMainFrame()) {
            return;
        }
        onReceivedError(view,
            error.getErrorCode(), error.getDescription().toString(),
            request.getUrl().toString());
    }

    /**
     * 网页加载错误时回调，需要注意的是：这个方法会在 onPageFinished 之前调用
     */
    @SuppressWarnings("deprecation")
    @Override
    public final void onReceivedError(@NonNull WebView view, int errorCode, @NonNull String description, @NonNull String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        log(String.format("onReceivedError: errorCode = %s, description = %s, failingUrl = %s",
                            errorCode, description, failingUrl));
        mLoadingFail = true;
        mErrorCode = errorCode;
        mDescription = description;
        mFailingUrl = failingUrl;
    }

    @Override
    public final void onPageFinished(@NonNull WebView view, @NonNull String url) {
        super.onPageFinished(view, url);
        log(String.format("onPageFinished: url = %s", url));
        int progress = view.getProgress();
        // 这里是为了处理网页重定向时会回调多次 onPageFinished 方法的问题
        // 会执行重定向的网站：https://xiaomi.com/ ---> https://www.mi.com/
        // 问题地址：https://stackoverflow.com/questions/3149216/how-to-listen-for-a-webview-finishing-loading-a-url
        if (progress != 100) {
            return;
        }
        if (mLoadingFail) {
            // 加载出错之后会先调用 onReceivedError 再调用 onPageFinished
            onWebPageLoadFail(view, mErrorCode, mDescription, mFailingUrl);
        } else {
            onWebPageLoadSuccess(view, url);
        }
        onWebPageLoadFinished(view, url, !mLoadingFail);
    }

    public void onWebPageLoadStarted(@NonNull WebView view, @NonNull String url, @Nullable Bitmap favicon) {
        log(String.format("onWebPageLoadStarted: url = %s", url));
    }

    public void onWebPageLoadSuccess(@NonNull WebView view, @NonNull String url) {
        log(String.format("onWebPageLoadSuccess: url = %s", url));
    }

    public void onWebPageLoadFail(@NonNull WebView view, int errorCode, @NonNull String description, @NonNull String failingUrl) {
        log(String.format("onWebPageLoadFail: errorCode = %s, description = %s, failingUrl = %s",
            errorCode, description, failingUrl));
    }

    public void onWebPageLoadFinished(@NonNull WebView view, @NonNull String url, boolean success) {
        log(String.format("onWebPageLoadFinished: url = %s", url));
    }

    /**
     * 网站证书校验错误
     */
    @Override
    public void onReceivedSslError(@NonNull WebView view, @Nullable SslErrorHandler handler, @NonNull SslError error) {
        log(String.format("onReceivedSslError: error = %s", error));
        Context context = view.getContext();
        if (context == null) {
            return;
        }

        String errorMessage;
        switch (error.getPrimaryError()) {
            case SslError.SSL_NOTYETVALID:
                errorMessage = context.getString(R.string.common_web_ssl_error_type_not_valid);
                break;
            case SslError.SSL_EXPIRED:
                errorMessage = context.getString(R.string.common_web_ssl_error_type_expired);
                break;
            case SslError.SSL_IDMISMATCH:
                errorMessage = context.getString(R.string.common_web_ssl_error_type_hostname_mismatch);
                break;
            case SslError.SSL_UNTRUSTED:
                errorMessage = context.getString(R.string.common_web_ssl_error_type_untrusted);
                break;
            case SslError.SSL_DATE_INVALID:
                errorMessage = context.getString(R.string.common_web_ssl_error_type_date_invalid);
                break;
            case SslError.SSL_INVALID:
                errorMessage = context.getString(R.string.common_web_ssl_error_type_invalid);
                break;
            default:
                errorMessage = context.getString(R.string.common_web_ssl_error_type_other);
                break;
        }

        // 如何处理应用中的 WebView SSL 错误处理程序提醒：https://support.google.com/faqs/answer/7071387?hl=zh-Hans
        new MessageDialog.Builder(context)
            .setMessage(errorMessage + "\n" + context.getString(R.string.common_web_ssl_error_inquire))
            .setConfirm(R.string.common_web_ssl_error_allow)
            .setCancel(R.string.common_web_ssl_error_reject)
            .setCancelable(false)
            .setListener(new MessageDialog.OnListener() {

                @SuppressLint("WebViewClientOnReceivedSslError")
                @Override
                public void onConfirm(@NonNull BaseDialog dialog) {
                    onUserProceedSslError(handler);
                }

                @Override
                public void onCancel(@NonNull BaseDialog dialog) {
                    onUserRefuseSslError(handler);
                }
            })
            .show();
    }

    /**
     * 用户接受了 SSL 证书错误
     */
    protected void onUserProceedSslError(@Nullable SslErrorHandler handler) {
        log("onUserProceedSslError");
        if (handler == null) {
            return;
        }
        handler.proceed();
    }

    /**
     * 用户拒绝了 SSL 证书错误
     */
    protected void onUserRefuseSslError(@Nullable SslErrorHandler handler) {
        log("onUserRefuseSslError");
        if (handler == null) {
            return;
        }
        handler.cancel();
    }

    /**
     * 同名 API 兼容
     */
    @TargetApi(AndroidVersion.ANDROID_7)
    @Override
    public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest request) {
        return shouldOverrideUrlLoading(view, request.getUrl().toString());
    }

    /**
     * 跳转到其他链接
     */
    @Override
    public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull String url) {
        log(String.format("shouldOverrideUrlLoading: url = %s", url));
        String scheme = Uri.parse(url).getScheme();
        if (scheme == null) {
            return false;
        }
        switch (scheme) {
            // 如果这是跳链接操作
            case "http":
            case "https":
                view.loadUrl(url);
                break;
            // 如果这是打电话操作
            case "tel":
                showDialAskDialog(view, url);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 跳转到拨号界面
     */
    protected void showDialAskDialog(@NonNull WebView view, @NonNull String url) {
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

    protected void log(@Nullable String message) {
        if (message == null) {
            return;
        }
        Timber.i(message);
    }
}