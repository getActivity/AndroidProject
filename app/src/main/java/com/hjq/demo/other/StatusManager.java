package com.hjq.demo.other;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresPermission;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.dialog.WaitDialog;
import com.hjq.widget.HintLayout;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/18
 *    desc   : 界面状态管理类
 */
public final class StatusManager {

    // 加载对话框
    private BaseDialog mDialog;

    // 提示布局
    private HintLayout mHintLayout;

    /**
     * 显示加载中
     */
    public void showLoading(FragmentActivity activity) {
        if (mDialog == null) {
            mDialog = new WaitDialog.Builder(activity)
                    .setMessage("加载中...") // 消息文本可以不用填写
                    .create();
        }

        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    /**
     * 显示加载完成
     */
    public void showComplete() {

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        if (mHintLayout != null && mHintLayout.isShow()) {
            mHintLayout.hide();
        }
    }

    /**
     * 显示空提示
     */
    public void showEmpty(View view) {
        showLayout(view, R.mipmap.icon_hint_empty, R.string.hint_layout_no_data);
    }

    /**
     * 显示错误提示
     */
    public void showError(View view) {
        // 判断当前网络是否可用
        if (isNetworkAvailable(view.getContext())) {
            showLayout(view, R.mipmap.icon_hint_request, R.string.hint_layout_error_request);
        } else {
            showLayout(view, R.mipmap.icon_hint_nerwork, R.string.hint_layout_error_network);
        }
    }

    /**
     * 显示自定义提示
     */
    public void showLayout(View view, @DrawableRes int iconId, @StringRes int textId) {
        showLayout(view, view.getResources().getDrawable(iconId), view.getResources().getString(textId));
    }

    public void showLayout(View view, Drawable drawable, CharSequence hint) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        if (mHintLayout == null) {

            if (view instanceof HintLayout) {
                mHintLayout = (HintLayout) view;
            }else if (view instanceof ViewGroup) {
                mHintLayout = findHintLayout((ViewGroup) view);
            }

            if (mHintLayout == null) {
                throw new IllegalStateException("You didn't add this HintLayout to your Activity layout");
            }
        }
        mHintLayout.show();
        mHintLayout.setIcon(drawable);
        mHintLayout.setHint(hint);
    }

    /**
     * 判断网络功能是否可用
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    private static boolean isNetworkAvailable(Context context){
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    /**
     * 智能获取布局中的 HintLayout 对象
     */
    private static HintLayout findHintLayout(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if ((view instanceof HintLayout)) {
                return (HintLayout) view;
            } else if (view instanceof ViewGroup) {
                HintLayout layout = findHintLayout((ViewGroup) view);
                if (layout != null) return layout;
            }
        }
        return null;
    }
}