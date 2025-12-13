package com.hjq.demo.action;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.DrawableRes;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import com.hjq.demo.R;
import com.hjq.demo.widget.StatusLayout;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/08
 *    desc   : 状态布局意图
 */
public interface StatusAction {

    /**
     * 获取状态布局
     */
    StatusLayout acquireStatusLayout();

    /**
     * 显示加载中
     */
    default void showLoading() {
        showLoading(R.raw.loading);
    }

    default void showLoading(@RawRes int id) {
        StatusLayout layout = acquireStatusLayout();
        if (layout == null) {
            return;
        }
        layout.show();
        layout.setAnimResource(id);
        layout.setHint("");
        layout.setOnRetryListener(null);
    }

    /**
     * 显示加载完成
     */
    default void showComplete() {
        StatusLayout layout = acquireStatusLayout();
        if (layout == null || !layout.isShow()) {
            return;
        }
        layout.hide();
    }

    /**
     * 显示空提示
     */
    default void showEmpty() {
        showLayout(R.drawable.status_empty_ic, R.string.status_layout_no_data, null);
    }

    /**
     * 显示错误提示
     */
    default void showError(StatusLayout.OnRetryListener listener) {
        StatusLayout layout = acquireStatusLayout();
        if (layout == null) {
            return;
        }
        Context context = layout.getContext();
        ConnectivityManager manager = ContextCompat.getSystemService(context, ConnectivityManager.class);
        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            // 判断网络是否连接
            if (info == null || !info.isConnected()) {
                showLayout(R.drawable.status_network_ic, R.string.status_layout_error_network, listener);
                return;
            }
        }
        showLayout(R.drawable.status_error_ic, R.string.status_layout_error_request, listener);
    }

    /**
     * 显示自定义提示
     */
    default void showLayout(@DrawableRes int drawableId, @StringRes int stringId, StatusLayout.OnRetryListener listener) {
        StatusLayout layout = acquireStatusLayout();
        if (layout == null) {
            return;
        }
        Context context = layout.getContext();
        showLayout(ContextCompat.getDrawable(context, drawableId), context.getString(stringId), listener);
    }

    default void showLayout(Drawable drawable, CharSequence hint, StatusLayout.OnRetryListener listener) {
        StatusLayout layout = acquireStatusLayout();
        if (layout == null) {
            return;
        }
        layout.show();
        layout.setIcon(drawable);
        layout.setHint(hint);
        layout.setOnRetryListener(listener);
    }
}