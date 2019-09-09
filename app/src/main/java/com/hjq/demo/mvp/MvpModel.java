package com.hjq.demo.mvp;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : MVP 模型基类
 */
public abstract class MvpModel<L> {

    private L mListener;

    public void setListener(L listener) {
        mListener = listener;
    }

    public L getListener() {
        return mListener;
    }
}