package com.hjq.demo.mvp;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : MVP 业务基类
 */
public abstract class MvpPresenter<V> {

    private V mView;

    public void attach(V view){
        mView = view;
    }

    public void detach() {
        mView = null;
    }

    public boolean isAttached() {
        return mView != null;
    }

    public V getView() {
        return mView;
    }

    public abstract void start();
}