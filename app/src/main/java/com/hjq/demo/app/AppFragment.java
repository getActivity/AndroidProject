package com.hjq.demo.app;

import com.hjq.base.BaseFragment;
import com.hjq.demo.action.ToastAction;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 业务 Fragment 基类
 */
public abstract class AppFragment<A extends AppActivity> extends BaseFragment<A>
        implements ToastAction  {

    /**
     * 当前加载对话框是否在显示中
     */
    public boolean isShowDialog() {
        A activity = getAttachActivity();
        if (activity != null) {
            return activity.isShowDialog();
        }

        return false;
    }

    /**
     * 显示加载对话框
     */
    public void showDialog() {
        A activity = getAttachActivity();
        if (activity != null) {
            activity.showDialog();
        }
    }

    /**
     * 隐藏加载对话框
     */
    public void hideDialog() {
        A activity = getAttachActivity();
        if (activity != null) {
            activity.hideDialog();
        }
    }


}