package com.hjq.demo.helper;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 文本输入辅助类，通过管理多个 EditText 输入是否为空来启用或者禁用按钮的点击事件
 *    blog   : https://www.jianshu.com/p/fd3795e8a6b3
 */
public final class InputTextHelper implements TextWatcher {

    private View mView; // 操作按钮的View
    private boolean isAlpha; // 是否禁用后设置半透明度

    private List<EditText> mViewSet; // EditText集合

    public InputTextHelper(View view) {
        this(view, false);
    }

    /**
     * 构造函数
     *
     * @param view              跟随 EditText 输入为空来判断启动或者禁用这个 View
     * @param alpha             是否需要设置透明度
     */
    public InputTextHelper(View view, boolean alpha) {
        if (view == null) throw new IllegalArgumentException("The view is empty");
        mView = view;
        isAlpha = alpha;
    }

    /**
     * 添加 EditText
     *
     * @param views     传入单个或者多个 EditText
     */
    public void addViews(EditText... views) {
        if (views == null) return;

        if (mViewSet == null) {
            mViewSet = new ArrayList<>(views.length - 1);
        }

        for (EditText view : views) {
            view.addTextChangedListener(this);
            mViewSet.add(view);
        }
        afterTextChanged(null);
    }

    /**
     * 移除 EditText 监听，避免内存泄露
     */
    public void removeViews() {
        if (mViewSet == null) return;

        for (EditText view : mViewSet) {
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

        for (EditText view : mViewSet) {
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
}