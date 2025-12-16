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

    /**
     * 获取标题栏对象
     */
    @Nullable
    TitleBar acquireTitleBar();

    /**
     * 设置标题栏的标题
     */
    default void setTitle(@StringRes int id) {
        TitleBar titleBar = acquireTitleBar();
        if (titleBar == null) {
            return;
        }
        titleBar.setTitle(id);
    }

    /**
     * 设置标题栏的标题
     */
    default void setTitle(CharSequence title) {
        TitleBar titleBar = acquireTitleBar();
        if (titleBar == null) {
            return;
        }
        titleBar.setTitle(title);
    }

    /**
     * 设置标题栏的左标题
     */
    default void setLeftTitle(int id) {
        TitleBar titleBar = acquireTitleBar();
        if (titleBar == null) {
            return;
        }
        titleBar.setLeftTitle(id);
    }

    default void setLeftTitle(CharSequence text) {
        TitleBar titleBar = acquireTitleBar();
        if (titleBar == null) {
            return;
        }
        titleBar.setLeftTitle(text);
    }

    default CharSequence getLeftTitle() {
        TitleBar titleBar = acquireTitleBar();
        if (titleBar == null) {
            return "";
        }
        return titleBar.getLeftTitle();
    }

    /**
     * 设置标题栏的右标题
     */
    default void setRightTitle(int id) {
        TitleBar titleBar = acquireTitleBar();
        if (titleBar == null) {
            return;
        }
        titleBar.setRightTitle(id);
    }

    default void setRightTitle(CharSequence text) {
        TitleBar titleBar = acquireTitleBar();
        if (titleBar == null) {
            return;
        }
        titleBar.setRightTitle(text);
    }

    default CharSequence getRightTitle() {
        TitleBar titleBar = acquireTitleBar();
        if (titleBar == null) {
            return "";
        }
        return titleBar.getRightTitle();
    }

    /**
     * 设置标题栏的左图标
     */
    default void setLeftIcon(int id) {
        TitleBar titleBar = acquireTitleBar();
        if (titleBar == null) {
            return;
        }
        titleBar.setLeftIcon(id);
    }

    default void setLeftIcon(Drawable drawable) {
        TitleBar titleBar = acquireTitleBar();
        if (titleBar == null) {
            return;
        }
        titleBar.setLeftIcon(drawable);
    }

    @Nullable
    default Drawable getLeftIcon() {
        TitleBar titleBar = acquireTitleBar();
        if (titleBar == null) {
            return null;
        }
        return titleBar.getLeftIcon();
    }

    /**
     * 设置标题栏的右图标
     */
    default void setRightIcon(int id) {
        TitleBar titleBar = acquireTitleBar();
        if (titleBar == null) {
            return;
        }
        titleBar.setRightIcon(id);
    }

    default void setRightIcon(Drawable drawable) {
        TitleBar titleBar = acquireTitleBar();
        if (titleBar == null) {
            return;
        }
        titleBar.setRightIcon(drawable);
    }

    @Nullable
    default Drawable getRightIcon() {
        TitleBar titleBar = acquireTitleBar();
        if (titleBar == null) {
            return null;
        }
        return titleBar.getRightIcon();
    }

    /**
     * 递归获取 ViewGroup 中的 TitleBar 对象
     */
    default TitleBar findTitleBar(@Nullable View contentView) {
        if (contentView == null) {
            return null;
        }
        if (contentView instanceof TitleBar) {
            return (TitleBar) contentView;
        }
        if (contentView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) contentView;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View view = viewGroup.getChildAt(i);
                if ((view instanceof TitleBar)) {
                    return (TitleBar) view;
                }

                if (view instanceof ViewGroup) {
                    TitleBar titleBar = findTitleBar(view);
                    if (titleBar != null) {
                        return titleBar;
                    }
                }
            }
        }
        return null;
    }
}