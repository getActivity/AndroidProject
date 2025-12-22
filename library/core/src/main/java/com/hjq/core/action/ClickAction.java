package com.hjq.core.action;

import android.view.View;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/15
 *    desc   : 点击事件意图
 */
public interface ClickAction extends View.OnClickListener {

    <V extends View> V findViewById(@IdRes int id);

    default void setOnClickListener(@IdRes int... ids) {
        setOnClickListener(this, ids);
    }

    default void setOnClickListener(@Nullable View.OnClickListener listener, @IdRes int... ids) {
        for (int id : ids) {
            View view = findViewById(id);
            if (view == null) {
                continue;
            }
            view.setOnClickListener(listener);
        }
    }

    default void setOnClickListener(View... views) {
       setOnClickListener(this, views);
    }

    default void setOnClickListener(@Nullable View.OnClickListener listener, View... views) {
        for (View view : views) {
            if (view == null) {
                continue;
            }
            view.setOnClickListener(listener);
        }
    }

    @Override
    default void onClick(@NonNull View view) {
        // 默认不实现，让子类实现
    }
}