package com.hjq.core.action;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/03/08
 *    desc   : 软键盘相关意图
 */
public interface KeyboardAction {

    /**
     * 显示软键盘，需要先 requestFocus 获取焦点，如果是在 Activity Create，那么需要延迟一段时间
     */
    default void showKeyboard(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager == null) {
            return;
        }
        manager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏软键盘
     */
    default void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager == null) {
            return;
        }
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}