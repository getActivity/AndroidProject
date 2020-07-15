package com.hjq.demo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.hjq.demo.R;
import com.hjq.demo.action.StatusAction;
import com.hjq.demo.aop.CheckNet;
import com.hjq.demo.aop.DebugLog;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.other.IntentKey;
import com.hjq.demo.widget.BrowserView;
import com.hjq.demo.widget.HintLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 浏览器界面
 */
public final class BrowserActivity extends MyActivity
        implements StatusAction, OnRefreshListener {

    @CheckNet
    @DebugLog
    public static void start(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(IntentKey.URL, url);
        context.startActivity(intent);
    }

    private HintLayout mHintLayout;
    private ProgressBar mProgressBar;
    private SmartRefreshLayout mRefreshLayout;
    private BrowserView mBrowserView;

    @Override
    protected int getLayoutId() {
        return R.layout.browser_activity;
    }

    @Override
    protected void initView() {
        mHintLayout = findViewById(R.id.hl_browser_hint);
        mProgressBar = findViewById(R.id.pb_browser_progress);
        mRefreshLayout = findViewById(R.id.sl_browser_refresh);
        mBrowserView = findViewById(R.id.wv_browser_view);

        // 设置网页刷新监听
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        showLoading();

        mBrowserView.setBrowserViewClient(new MyBrowserViewClient());
        mBrowserView.setBrowserChromeClient(new MyBrowserChromeClient(mBrowserView));

        String url = getString(IntentKey.URL);
        mBrowserView.loadUrl(url);
    }

    @Override
    public HintLayout getHintLayout() {
        return mHintLayout;
    }

    @Override
    public void onLeftClick(View v) {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mBrowserView.canGoBack()) {
            // 后退网页并且拦截该事件
            mBrowserView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        mBrowserView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mBrowserView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mBrowserView.onDestroy();
        super.onDestroy();
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
        mBrowserView.reload();
    }

    private class MyBrowserViewClient extends BrowserView.BrowserViewClient {

        /**
         * 网页加载错误时回调，这个方法会在 onPageFinished 之前调用
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // 这里为什么要用延迟呢？因为加载出错之后会先调用 onReceivedError 再调用 onPageFinished
            post(() -> showError(v -> reload()));
        }

        /**
         * 开始加载网页
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        /**
         * 完成加载网页
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.setVisibility(View.GONE);
            mRefreshLayout.finishRefresh();
            showComplete();
        }
    }

    private class MyBrowserChromeClient extends BrowserView.BrowserChromeClient {

        private MyBrowserChromeClient(BrowserView view) {
            super(view);
        }

        /**
         * 收到网页标题
         */
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (title != null) {
                setTitle(title);
            }
        }

        /**
         * 收到加载进度变化
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
        }
    }
}