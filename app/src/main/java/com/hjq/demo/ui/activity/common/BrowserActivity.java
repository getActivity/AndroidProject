package com.hjq.demo.ui.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.drake.softinput.SoftInputKt;
import com.hjq.bar.TitleBar;
import com.hjq.demo.R;
import com.hjq.demo.action.StatusAction;
import com.hjq.demo.aop.CheckNet;
import com.hjq.demo.aop.Log;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.widget.StatusLayout;
import com.hjq.demo.widget.webview.BrowserChromeClient;
import com.hjq.demo.widget.webview.BrowserFullScreenController;
import com.hjq.demo.widget.webview.BrowserView;
import com.hjq.demo.widget.webview.BrowserViewClient;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 浏览器界面
 */
public final class BrowserActivity extends AppActivity
        implements StatusAction, OnRefreshListener {

    private static final String INTENT_KEY_IN_URL = "url";

    @CheckNet
    @Log
    public static void start(@NonNull Context context, @NonNull String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(INTENT_KEY_IN_URL, url);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @NonNull
    private final BrowserFullScreenController mFullScreenController = new BrowserFullScreenController();

    private StatusLayout mStatusLayout;
    private ProgressBar mProgressBar;
    private SmartRefreshLayout mRefreshLayout;
    private BrowserView mBrowserView;

    @Override
    protected int getLayoutId() {
        return R.layout.browser_activity;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.sl_browser_status);
        mProgressBar = findViewById(R.id.pb_browser_progress);
        mRefreshLayout = findViewById(R.id.sl_browser_refresh);
        mBrowserView = findViewById(R.id.wv_browser_view);

        // 设置 WebView 生命管控
        mBrowserView.setLifecycleOwner(this);
        // 设置网页刷新监听
        mRefreshLayout.setOnRefreshListener(this);

        // 解决 WebView 底部有输入框会被遮挡的问题
        SoftInputKt.setWindowSoftInput(this, mBrowserView);
    }

    @Override
    protected void initData() {
        showLoading();

        mBrowserView.setBrowserViewClient(new AppBrowserViewClient());
        mBrowserView.setBrowserChromeClient(new AppBrowserChromeClient(mBrowserView));
        mBrowserView.loadUrl(getString(INTENT_KEY_IN_URL));
    }

    @Nullable
    @Override
    public View getImmersionBottomView() {
        return mStatusLayout;
    }

    @Override
    public StatusLayout acquireStatusLayout() {
        return mStatusLayout;
    }

    @Override
    public void onLeftClick(@NonNull TitleBar titleBar) {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mFullScreenController.isFullScreen()) {
                mFullScreenController.exitFullScreen(this);
                return true;
            }

            if (mBrowserView.canGoBack()) {
                // 后退网页并且拦截该事件
                mBrowserView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 重新加载当前页
     */
    @CheckNet
    private void reload() {
        mBrowserView.reload();
    }

    /**
     * {@link OnRefreshListener}
     */

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        reload();
    }

    private class AppBrowserViewClient extends BrowserViewClient {

        @Override
        protected void onUserRefuseSslError(@Nullable SslErrorHandler handler) {
            super.onUserRefuseSslError(handler);
            if (mBrowserView.canGoBack()) {
                return;
            }
            // 如果当前是 WebView 的第一个页面，那么就直接销毁当前页面
            finish();
        }

        @Override
        public void onWebPageLoadStarted(@NonNull WebView view, @NonNull String url, @Nullable Bitmap favicon) {
            super.onWebPageLoadStarted(view, url, favicon);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onWebPageLoadFinished(@NonNull WebView view, @NonNull String url, boolean success) {
            super.onWebPageLoadFinished(view, url, success);
            mProgressBar.setVisibility(View.GONE);
            mRefreshLayout.finishRefresh();
            if (success) {
                showComplete();
            } else {
                showError(listener -> reload());
            }
        }
    }

    private class AppBrowserChromeClient extends BrowserChromeClient {

        private AppBrowserChromeClient(BrowserView view) {
            super(view);
        }

        /**
         * 收到网页标题
         */
        @Override
        public void onReceivedTitle(@NonNull WebView view, @NonNull String title) {
            setTitle(title);
        }

        /**
         * 收到网页图标
         */
        @Override
        public void onReceivedIcon(@NonNull WebView view, @NonNull Bitmap icon) {
            setRightIcon(new BitmapDrawable(getResources(), icon));
        }

        /**
         * 收到加载进度变化
         */
        @Override
        public void onProgressChanged(@NonNull WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgressBar.setProgress(newProgress);
        }

        /**
         * 播放视频时进入全屏回调
         */
        @Override
        public void onShowCustomView(@Nullable View view, @Nullable CustomViewCallback callback) {
            mBrowserView.setVisibility(View.INVISIBLE);
            mFullScreenController.enterFullScreen(BrowserActivity.this, view, callback);
            mBrowserView.setVisibility(View.VISIBLE);
        }

        /**
         * 播放视频时退出全屏回调
         */
        @Override
        public void onHideCustomView() {
            mFullScreenController.exitFullScreen(BrowserActivity.this);
        }
    }
}