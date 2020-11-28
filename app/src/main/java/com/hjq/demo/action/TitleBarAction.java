package com.hjq.demo.action;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/08
 *    desc   : 标题栏意图
 */
public interface TitleBarAction extends OnTitleBarListener {

    @Nullable
    TitleBar getTitleBar();
    
    /**
     * 左项被点击
     *
     * @param v     被点击的左项View
     */
    @Override
    default void onLeftClick(View v) {}

    /**
     * 标题被点击
     *
     * @param v     被点击的标题View
     */
    @Override
    default void onTitleClick(View v){}

    /**
     * 右项被点击
     *
     * @param v     被点击的右项View
     */
    @Override
    default void onRightClick(View v) {}

    /**
     * 设置标题栏的标题
     */
    default void setTitle(@StringRes int id) {
        if (getTitleBar() != null) {
            setTitle(getTitleBar().getResources().getString(id));
        }
    }

    /**
     * 设置标题栏的标题
     */
    default void setTitle(CharSequence title) {
        if (getTitleBar() != null) {
            getTitleBar().setTitle(title);
        }
    }

    /**
     * 设置标题栏的左标题
     */
    default void setLeftTitle(int id) {
        if (getTitleBar() != null) {
            getTitleBar().setLeftTitle(id);
        }
    }

    default void setLeftTitle(CharSequence text) {
        if (getTitleBar() != null) {
            getTitleBar().setLeftTitle(text);
        }
    }

    default CharSequence getLeftTitle() {
        if (getTitleBar() != null) {
            return getTitleBar().getLeftTitle();
        }
        return "";
    }

    /**
     * 设置标题栏的右标题
     */
    default void setRightTitle(int id) {
        if (getTitleBar() != null) {
            getTitleBar().setRightTitle(id);
        }
    }

    default void setRightTitle(CharSequence text) {
        if (getTitleBar() != null) {
            getTitleBar().setRightTitle(text);
        }
    }

    default CharSequence getRightTitle() {
        if (getTitleBar() != null) {
            return getTitleBar().getRightTitle();
        }
        return "";
    }

    /**
     * 设置标题栏的左图标
     */
    default void setLeftIcon(int id) {
        if (getTitleBar() != null) {
            getTitleBar().setLeftIcon(id);
        }
    }

    default void setLeftIcon(Drawable drawable) {
        if (getTitleBar() != null) {
            getTitleBar().setLeftIcon(drawable);
        }
    }

    @Nullable
    default Drawable getLeftIcon() {
        if (getTitleBar() != null) {
            return getTitleBar().getLeftIcon();
        }
        return null;
    }

    /**
     * 设置标题栏的右图标
     */
    default void setRightIcon(int id) {
        if (getTitleBar() != null) {
            getTitleBar().setRightIcon(id);
        }
    }

    default void setRightIcon(Drawable drawable) {
        if (getTitleBar() != null) {
            getTitleBar().setRightIcon(drawable);
        }
    }

    @Nullable
    default Drawable getRightIcon() {
        if (getTitleBar() != null) {
            return getTitleBar().getRightIcon();
        }
        return null;
    }

    /**
     * 递归获取 ViewGroup 中的 TitleBar 对象
     */
    default TitleBar obtainTitleBar(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if ((view instanceof TitleBar)) {
                return (TitleBar) view;
            } else if (view instanceof ViewGroup) {
                TitleBar titleBar = obtainTitleBar((ViewGroup) view);
                if (titleBar != null) {
                    return titleBar;
                }
            }
        }
        return null;
    }
}