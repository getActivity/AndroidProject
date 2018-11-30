package com.hjq.base;

import android.app.Activity;
import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 在 Activity 中优化 Handler 基类
 */
public abstract class BaseActivityHandler<T extends Activity> extends Handler {

    private final WeakReference<T> mActivity;

    public BaseActivityHandler(T activity) {
        mActivity = new WeakReference<>(activity);
    }

    /**
     * 判断当前Handler是否可用
     */
    public boolean isEnabled() {
        return getActivity() != null && !getActivity().isFinishing();
    }

    /**
     * 获取Activity对象
     */
    public T getActivity() {
        return mActivity.get();
    }

    /**
     * 在Activity销毁前移除所有的任务
     */
    public void onDestroy() {
        //删除所有的回调函数和消息
        removeCallbacksAndMessages(null);
    }
}