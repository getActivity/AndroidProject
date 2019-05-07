package com.hjq.demo.helper;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 文本输入辅助类，通过管理多个 TextView 输入是否为空来启用或者禁用按钮的点击事件
 *    blog   : https://www.jianshu.com/p/fd3795e8a6b3
 */
public final class InputTextHelper implements TextWatcher {

    private View mView; // 操作按钮的View
    private boolean isAlpha; // 是否禁用后设置半透明度

    private List<TextView> mViewSet; // TextView集合

    public InputTextHelper(View view) {
        this(view, false);
    }

    /**
     * 构造函数
     *
     * @param view              跟随 TextView 输入为空来判断启动或者禁用这个 View
     * @param alpha             是否需要设置透明度
     */
    public InputTextHelper(View view, boolean alpha) {
        if (view == null) throw new IllegalArgumentException("The view is empty");
        mView = view;
        isAlpha = alpha;
    }

    /**
     * 添加 TextView
     *
     * @param views     传入单个或者多个 TextView
     */
    public void addViews(List<TextView> views) {
        if (views == null) return;

        if (mViewSet == null) {
            mViewSet = views;
        } else {
            mViewSet.addAll(views);
        }

        for (TextView view : views) {
            view.addTextChangedListener(this);
        }

        // 触发一次监听
        afterTextChanged(null);
    }

    /**
     * 添加 TextView
     *
     * @param views     传入单个或者多个 TextView
     */
    public void addViews(TextView... views) {
        if (views == null) return;

        if (mViewSet == null) {
            mViewSet = new ArrayList<>(views.length - 1);
        }

        for (TextView view : views) {
            view.addTextChangedListener(this);
            mViewSet.add(view);
        }
        // 触发一次监听
        afterTextChanged(null);
    }

    /**
     * 移除 TextView 监听，避免内存泄露
     */
    public void removeViews() {
        if (mViewSet == null) return;

        for (TextView view : mViewSet) {
            view.removeTextChangedListener(this);
        }
        mViewSet.clear();
        mViewSet = null;
    }

    /**
     * {@link TextWatcher}
     */

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if (mViewSet == null) return;

        for (TextView view : mViewSet) {
            if ("".equals(view.getText().toString())) {
                setEnabled(false);
                return;
            }
        }

        setEnabled(true);
    }

    /**
     * 设置 View 的事件
     *
     * @param enabled               启用或者禁用 View 的事件
     */
    public void setEnabled(boolean enabled) {
        if (enabled == mView.isEnabled()) return;

        if (enabled) {
            //启用View的事件
            mView.setEnabled(true);
            if (isAlpha) {
                //设置不透明
                mView.setAlpha(1f);
            }
        }else {
            //禁用View的事件
            mView.setEnabled(false);
            if (isAlpha) {
                //设置半透明
                mView.setAlpha(0.5f);
            }
        }
    }

    public static final class Builder implements Application.ActivityLifecycleCallbacks {

        private Activity mActivity; // 当前的Activity
        private View mView; // 操作按钮的View
        private boolean isAlpha; // 是否禁用后设置半透明度
        private List<TextView> mViewSet = new ArrayList<>(); // TextView集合

        InputTextHelper mTextHelper;

        public Builder() {

        }

        public Builder(Activity activity) {
            mActivity = activity;
        }

        public Builder setMain(View view) {
            mView = view;
            return this;
        }

        public Builder setAlpha(boolean alpha) {
            isAlpha = alpha;
            return this;
        }

        public Builder addView(TextView view) {
            mViewSet.add(view);
            return this;
        }

        public InputTextHelper build(){
            if (mActivity != null) {
                mActivity.getApplication().registerActivityLifecycleCallbacks(this);
            }
            mTextHelper = new InputTextHelper(mView, isAlpha);
            mTextHelper.addViews(mViewSet);
            return mTextHelper;
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (mActivity != null && mActivity == activity) {
                mTextHelper.removeViews();
            }
        }
    }
}