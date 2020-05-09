package com.hjq.demo.other;

import android.app.Activity;
import android.app.Application;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/07/04
 *    desc   : 软键盘监听类
 */
public final class KeyboardWatcher implements
        ViewTreeObserver.OnGlobalLayoutListener,
        Application.ActivityLifecycleCallbacks {

    private Activity mActivity;
    private View mContentView;
    private SoftKeyboardStateListener mListeners;
    private boolean isSoftKeyboardOpened;
    private int mStatusBarHeight;

    public static KeyboardWatcher with(Activity activity) {
        return new KeyboardWatcher(activity);
    }

    private KeyboardWatcher(Activity activity) {
        mActivity = activity;
        mContentView = activity.findViewById(Window.ID_ANDROID_CONTENT);

        mActivity.getApplication().registerActivityLifecycleCallbacks(this);
        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        // 获取 status_bar_height 资源的 ID
        int resourceId = mActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源 ID 获取响应的尺寸值
            mStatusBarHeight = mActivity.getResources().getDimensionPixelSize(resourceId);
        }
    }

    /**
     * {@link ViewTreeObserver.OnGlobalLayoutListener}
     */

    @Override
    public void onGlobalLayout() {
        final Rect r = new Rect();
        //r will be populated with the coordinates of your view that area still visible.
        mContentView.getWindowVisibleDisplayFrame(r);

        final int heightDiff = mContentView.getRootView().getHeight() - (r.bottom - r.top);
        if (!isSoftKeyboardOpened && heightDiff > mContentView.getRootView().getHeight() / 4) {
            isSoftKeyboardOpened = true;
            if ((mActivity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                if (mListeners != null) {
                    mListeners.onSoftKeyboardOpened(heightDiff - mStatusBarHeight);
                }
            } else {
                if (mListeners != null) {
                    mListeners.onSoftKeyboardOpened(heightDiff);
                }
            }

        } else if (isSoftKeyboardOpened && heightDiff < mContentView.getRootView().getHeight() / 4) {
            isSoftKeyboardOpened = false;
            if (mListeners != null) {
                mListeners.onSoftKeyboardClosed();
            }
        }
    }

    /**
     * 设置软键盘弹出监听
     */
    public void setListener(SoftKeyboardStateListener listener) {
        mListeners = listener;
    }

    /**
     * {@link Application.ActivityLifecycleCallbacks}
     */

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {}

    @Override
    public void onActivityResumed(@NonNull Activity activity) {}

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (mActivity == activity) {
            mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
            mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            mActivity = null;
            mContentView = null;
            mListeners = null;
        }
    }

    /**
     * 软键盘状态监听器
     */
    public interface SoftKeyboardStateListener {

        /**
         * 软键盘弹出了
         * @param keyboardHeight            软键盘高度
         */
        void onSoftKeyboardOpened(int keyboardHeight);

        /**
         * 软键盘收起了
         */
        void onSoftKeyboardClosed();
    }
}