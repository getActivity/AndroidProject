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

    @SuppressWarnings("unchecked")
    public void attach(V view) {
        mView = view;
        // 使用动态代理，解决 getView 方法可能为空的问题
        mProxyView = (V) Proxy.newProxyInstance(view.getClass().getClassLoader(), view.getClass().getInterfaces(), this);
        // V 层解绑了 P 层，那么 getView 就为空，调用 V 层就会发生空指针异常
        // 如果在 P 层的每个子类中都进行 getView() != null 防空判断会导致开发成本非常高，并且容易出现遗漏
    }

    /**
     *  动态代理接口，每次调用了代理对象的方法最终也会回到到这里
     *
     * {@link InvocationHandler}
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 如果当前还是绑定状态就执行 View 的方法，否则就不执行
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

    /**
     * P 层初始化方法
     */
    public abstract void start();
}