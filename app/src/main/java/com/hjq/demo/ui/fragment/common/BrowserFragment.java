package com.hjq.demo.ui.fragment.common;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.drake.softinput.SoftInputKt;
import com.hjq.base.BaseActivity;
import com.hjq.demo.R;
import com.hjq.demo.action.StatusAction;
import com.hjq.demo.aop.CheckNet;
import com.hjq.demo.aop.Log;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.app.AppFragment;
import com.hjq.demo.ui.activity.common.BrowserActivity;
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
 *    time   : 2020/10/24
 *    desc   : 浏览器 Fragment
 */
public final class BrowserFragment extends AppFragment<AppActivity>
        implements StatusAction, OnRefreshListener {

    private static final String INTENT_KEY_IN_URL = "url";

    @Log
    public static BrowserFragment newInstance(String url) {
        BrowserFragment fragment = new BrowserFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_KEY_IN_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    private final BrowserFullScreenController mFullScreenController = new BrowserFullScreenController();

    private StatusLayout mStatusLayout;
    private SmartRefreshLayout mRefreshLayout;
    private BrowserView mBrowserView;

    @Override
    protected int getLayoutId() {
        return R.layout.browser_fragment;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.sl_browser_status);
        mRefreshLayout = findViewById(R.id.sl_browser_refresh);
        mBrowserView = findViewById(R.id.wv_browser_view);

        // 设置 WebView 生命周期回调
        mBrowserView.setLifecycleOwner(this);
        // 设置网页刷新监听
        mRefreshLayout.setOnRefreshListener(this);

        // 解决 WebView 底部有输入框会被遮挡的问题
        SoftInputKt.setWindowSoftInput(this, mBrowserView);
    }

    @Override
    protected void initData() {
        mBrowserView.setBrowserViewClient(new AppBrowserViewClient());
        mBrowserView.setBrowserChromeClient(new AppBrowserChromeClient(mBrowserView));
        mBrowserView.loadUrl(getString(INTENT_KEY_IN_URL));
        showLoading();
    }

    @Override
    public StatusLayout acquireStatusLayout() {
        return mStatusLayout;
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        Activity activity = getAttachActivity();
        if (keyCode == KeyEvent.KEYCODE_BACK && activity != null) {
            if (mFullScreenController.isFullScreen()) {
                mFullScreenController.exitFullScreen(activity);
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
        public void onWebPageLoadFinished(@NonNull WebView view, @NonNull String url, boolean success) {
            super.onWebPageLoadFinished(view, url, success);
            mRefreshLayout.finishRefresh();
            if (success) {
                showComplete();
            } else {
                showError(listener -> reload());
            }
        }

        /**
         * 跳转到其他链接
         */
        @Override
        public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull String url) {
            String scheme = Uri.parse(url).getScheme();
            if (scheme == null) {
                return true;
            }
            switch (scheme.toLowerCase()) {
                // 如果这是跳链接操作
                case "http":
                case "https":
                    BaseActivity activity = getAttachActivity();
                    if (activity != null) {
                        BrowserActivity.start(activity, url);
                    }
                    break;
                default:
                    break;
            }
            // 已经处理该链接请求
            return true;
        }
    }

    private class AppBrowserChromeClient extends BrowserChromeClient {

        public AppBrowserChromeClient(@NonNull BrowserView view) {
            super(view);
        }

        /**
         * 播放视频时进入全屏回调
         */
        @Override
        public void onShowCustomView(@Nullable View view, @Nullable CustomViewCallback callback) {
            Activity activity = getAttachActivity();
            if (activity == null) {
                return;
            }
            mFullScreenController.enterFullScreen(getAttachActivity(), view, callback);
        }

        /**
         * 播放视频时退出全屏回调
         */
        @Override
        public void onHideCustomView() {
            Activity activity = getAttachActivity();
            if (activity == null) {
                return;
            }
            mFullScreenController.exitFullScreen(activity);
        }
    }
}