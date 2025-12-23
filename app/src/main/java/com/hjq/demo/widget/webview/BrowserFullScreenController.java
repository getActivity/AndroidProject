package com.hjq.demo.widget.webview;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient.CustomViewCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2023/11/05
 *    desc   : WebView 全屏模式控制器
 */
public final class BrowserFullScreenController {

    /** 记录当前 Activity 方向 */
    private int mRecordActivityOrientation;

    /** 记录当前 View SystemUiVisibility 值 */
    private int mRecordViewSystemUiVisibility;

    @Nullable
    private View mCustomView;

    @Nullable
    private CustomViewCallback mCustomViewCallback;

    /**
     * 进入全屏状态
     */
    @SuppressWarnings("deprecation")
    public void enterFullScreen(@NonNull Activity activity, @Nullable View customView, @Nullable CustomViewCallback callback) {
        if (isFullScreen()) {
            exitFullScreen(activity);
            return;
        }

        if (customView == null || callback == null) {
            return;
        }

        mCustomView = customView;
        mCustomViewCallback = callback;

        int currentActivityOrientation = activity.getRequestedOrientation();
        // 如果当前 Activity 不是横屏，就将 Activity 设置成横屏
        if (currentActivityOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            try {
                // 兼容问题：在 Android 8.0 的手机上可以固定 Activity 的方向，但是这个 Activity 不能是透明的，否则就会抛出异常
                // 复现场景：只需要给 Activity 主题设置 <item name="android:windowIsTranslucent">true</item> 属性即可
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } catch (IllegalStateException e) {
                // java.lang.IllegalStateException: Only fullscreen activities can request orientation
                e.printStackTrace();
            }
        }
        mRecordActivityOrientation = currentActivityOrientation;

        ViewGroup contentView = getContentView(activity);
        if (contentView != null) {
            contentView.addView(customView);
            mRecordViewSystemUiVisibility = contentView.getSystemUiVisibility();
            // 隐藏系统状态栏、导航栏
            contentView.setSystemUiVisibility(mRecordViewSystemUiVisibility |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    /**
     * 退出全屏状态
     */
    @SuppressWarnings("deprecation")
    public void exitFullScreen(@NonNull Activity activity) {
        if (!isFullScreen()) {
            return;
        }

        int currentActivityOrientation = activity.getRequestedOrientation();
        // 方向和之前记录的不一样，就还原回之前的方向
        if (currentActivityOrientation != mRecordActivityOrientation) {
            try {
                // 兼容问题：在 Android 8.0 的手机上可以固定 Activity 的方向，但是这个 Activity 不能是透明的，否则就会抛出异常
                // 复现场景：只需要给 Activity 主题设置 <item name="android:windowIsTranslucent">true</item> 属性即可
                activity.setRequestedOrientation(mRecordActivityOrientation);
            } catch (IllegalStateException e) {
                // java.lang.IllegalStateException: Only fullscreen activities can request orientation
                e.printStackTrace();
            }
        }

        ViewGroup contentView = getContentView(activity);
        if (contentView != null) {
            contentView.removeView(mCustomView);
            // 系统样式恢复成进入全屏之前的
            contentView.setSystemUiVisibility(mRecordViewSystemUiVisibility);
            if (mCustomViewCallback != null) {
                // 通知 WebView 自定义 View 已从容器上移除
                mCustomViewCallback.onCustomViewHidden();
            }
        }

        mCustomView = null;
        mCustomViewCallback = null;
    }

    /**
     * 判断当前是否为全屏状态
     */
    public boolean isFullScreen() {
        return mCustomView != null && mCustomViewCallback != null;
    }

    /**
     * 获取容器 View
     */
    @Nullable
    private ViewGroup getContentView(@NonNull Activity activity) {
        if (activity == null) {
            return null;
        }
        return activity.findViewById(Window.ID_ANDROID_CONTENT);
    }
}