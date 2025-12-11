package com.hjq.demo.app;

import androidx.annotation.NonNull;
import com.hjq.base.BaseFragment;
import com.hjq.demo.action.ToastAction;
import com.hjq.demo.http.model.HttpData;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.listener.OnHttpListener;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : Fragment 业务基类
 */
public abstract class AppFragment<A extends AppActivity> extends BaseFragment<A>
        implements ToastAction, OnHttpListener<Object> {

    /**
     * 当前加载对话框是否在显示中
     */
    public boolean isShowDialog() {
        A activity = getAttachActivity();
        if (activity == null) {
            return false;
        }
        return activity.isShowDialog();
    }

    /**
     * 显示加载对话框
     */
    public void showLoadingDialog() {
        A activity = getAttachActivity();
        if (activity == null) {
            return;
        }
        activity.showLoadingDialog();
    }

    /**
     * 隐藏加载对话框
     */
    public void hideLoadingDialog() {
        A activity = getAttachActivity();
        if (activity == null) {
            return;
        }
        activity.hideLoadingDialog();
    }

    /**
     * {@link OnHttpListener}
     */

    @Override
    public void onHttpStart(@NonNull IRequestApi api) {
        showLoadingDialog();
    }

    @Override
    public void onHttpSuccess(@NonNull Object result) {
        if (!(result instanceof HttpData)) {
            return;
        }
        toast(((HttpData<?>) result).getMessage());
    }

    @Override
    public void onHttpFail(@NonNull Throwable throwable) {
        toast(throwable.getMessage());
    }

    @Override
    public void onHttpEnd(@NonNull IRequestApi api) {
        hideLoadingDialog();
    }
}