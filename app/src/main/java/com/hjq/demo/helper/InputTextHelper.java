package com.hjq.demo.helper;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

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

    /** 操作按钮的View */
    private View mView;
    /** 是否禁用后设置半透明度 */
    private boolean isAlpha;

    /** TextView集合 */
    private List<TextView> mViewSet;

    /** 输入监听器 */
    private OnInputTextListener mListener;

    /**
     * 构造函数
     *
     * @param view              跟随 TextView 输入为空来判断启动或者禁用这个 View
     * @param alpha             是否需要设置透明度
     */
    private InputTextHelper(View view, boolean alpha) {
        if (view == null) {
            throw new IllegalArgumentException("are you ok?");
        }
        mView = view;
        isAlpha = alpha;
    }

    /**
     * 创建 Builder
     */
    public static Builder with(Activity activity) {
        return new Builder(activity);
    }

    /**
     * 添加 TextView
     *
     * @param views     传入单个或者多个 TextView
     */
    public void addViews(List<TextView> views) {
        if (views == null) {
            return;
        }

        if (mViewSet == null) {
            mViewSet = views;
        } else {
            mViewSet.addAll(views);
        }

        for (TextView view : views) {
            view.addTextChangedListener(this);
        }

        // 触发一次监听
        notifyChanged();
    }

    /**
     * 添加 TextView
     *
     * @param views     传入单个或者多个 TextView
     */
    public void addViews(TextView... views) {
        if (views == null) {
            return;
        }

        if (mViewSet == null) {
            mViewSet = new ArrayList<>(views.length);
        }

        for (TextView view : views) {
            // 避免重复添加
            if (!mViewSet.contains(view)) {
                view.addTextChangedListener(this);
                mViewSet.add(view);
            }
        }
        // 触发一次监听
        notifyChanged();
    }

    /**
     * 移除 TextView 监听，避免内存泄露
     */
    public void removeViews(TextView... views) {
        if (mViewSet != null && mViewSet.size() > 0) {
            for (TextView view : views) {
                view.removeTextChangedListener(this);
                mViewSet.remove(view);
            }
            // 触发一次监听
            notifyChanged();
        }
    }

    /**
     * 移除所有 TextView 监听，避免内存泄露
     */
    public void removeAllViews() {
        if (mViewSet == null) {
            return;
        }

        for (TextView view : mViewSet) {
            view.removeTextChangedListener(this);
        }
        mViewSet.clear();
        mViewSet = null;
    }

    /**
     * 设置输入监听
     */
    public void setListener(OnInputTextListener listener) {
        mListener = listener;
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
        notifyChanged();
    }

    /**
     * 通知更新
     */
    public void notifyChanged() {
        if (mViewSet == null) {
            return;
        }

        // 重新遍历所有的输入
        for (TextView view : mViewSet) {
            if ("".equals(view.getText().toString())) {
                setEnabled(false);
                return;
            }
        }

        if (mListener != null) {
            setEnabled(mListener.onInputChange(this));
        } else {
            setEnabled(true);
        }
    }

    /**
     * 设置 View 的事件
     *
     * @param enabled               启用或者禁用 View 的事件
     */
    public void setEnabled(boolean enabled) {
        if (enabled == mView.isEnabled()) {
            return;
        }

        if (enabled) {
            //启用View的事件
            mView.setEnabled(true);
            if (isAlpha) {
                //设置不透明
                mView.setAlpha(1f);
            }
        } else {
            //禁用View的事件
            mView.setEnabled(false);
            if (isAlpha) {
                //设置半透明
                mView.setAlpha(0.5f);
            }
        }
    }

    public static final class Builder {

        /** 当前的 Activity */
        private final Activity mActivity;
        /** 操作按钮的 View */
        private View mView;
        /** 是否禁用后设置半透明度 */
        private boolean isAlpha;
        /**  TextView集合 */
        private final List<TextView> mViewSet = new ArrayList<>();
        /** 文本 */
        private OnInputTextListener mListener;

        private Builder(Activity activity) {
            mActivity = activity;
        }

        public Builder addView(TextView view) {
            mViewSet.add(view);
            return this;
        }

        public Builder setMain(View view) {
            mView = view;
            return this;
        }

        public Builder setAlpha(boolean alpha) {
            isAlpha = alpha;
            return this;
        }

        public Builder setListener(OnInputTextListener listener) {
            mListener = listener;
            return this;
        }

        public InputTextHelper build(){
            InputTextHelper helper = new InputTextHelper(mView, isAlpha);
            helper.addViews(mViewSet);
            helper.setListener(mListener);
            mActivity.getApplication().registerActivityLifecycleCallbacks(new TextInputLifecycle(mActivity, helper));
            return helper;
        }
    }

    private static class TextInputLifecycle implements Application.ActivityLifecycleCallbacks {

        private Activity mActivity;
        private InputTextHelper mTextHelper;

        private TextInputLifecycle(Activity activity, InputTextHelper helper) {
            this.mActivity = activity;
            this.mTextHelper = helper;
        }

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
            if (mActivity != null && mActivity == activity) {
                mTextHelper.removeAllViews();
                mActivity.getApplication().registerActivityLifecycleCallbacks(this);
                mTextHelper = null;
                mActivity = null;
            }
        }
    }

    /**
     * 文本变化监听器
     */
    public interface OnInputTextListener {

        /**
         * 输入发生了变化
         * @return          返回按钮的 Enabled 状态
         */
        boolean onInputChange(InputTextHelper helper);
    }
}