package com.hjq.baselibrary.base;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : Activity中优化Handler基类
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
        return mActivity.get() != null && !mActivity.get().isFinishing();
    }

    /**
     * 在Activity销毁前移除所有的任务
     */
    public void onDestroy() {
        //删除所有的回调函数和消息
        removeCallbacksAndMessages(null);
    }
}