package com.hjq.demo.mvp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : MVP 业务基类
 */
public abstract class MvpPresenter<V extends IMvpView> implements InvocationHandler {

    // 当前 View 对象
    private V mView;

    // 代理对象
    private V mProxyView;

    public void attach(V view){
        mView = view;
        // 使用动态代理，解决 getView 为空的问题
        mProxyView = (V) Proxy.newProxyInstance(view.getClass().getClassLoader(), view.getClass().getInterfaces(), this);
    }

    /**
     * {@link InvocationHandler}
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 如果当前还是绑定状态就执行 View 的方法
        return isAttached() ? method.invoke(mView, args) : null;
    }

    public void detach() {
        mView = null;
        // 这里注意不能把代理对象置空
        // mProxyView = null;
    }

    public boolean isAttached() {
        return mProxyView != null && mView != null;
    }

    public V getView() {
        return mProxyView;
    }

    public abstract void start();
}