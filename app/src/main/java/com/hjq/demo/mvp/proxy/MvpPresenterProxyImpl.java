package com.hjq.demo.mvp.proxy;

import com.hjq.demo.mvp.IMvpView;
import com.hjq.demo.mvp.MvpInject;
import com.hjq.demo.mvp.MvpPresenter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/05/11
 *    desc   : 模型层代理实现
 */
public class MvpPresenterProxyImpl implements IMvpPresenterProxy {

    private IMvpView mView;
    private List<MvpPresenter> mPresenters;

    public MvpPresenterProxyImpl(IMvpView view){
        mView = view;
    }

    @SuppressWarnings("all")
    @Override
    public void bindPresenter() {
        mPresenters = new ArrayList<>();

        Field[] fields = mView.getClass().getDeclaredFields();
        for (Field field : fields) {
            MvpInject inject = field.getAnnotation(MvpInject.class);
            if(inject != null){
                try {
                    Class<? extends MvpPresenter> clazz = (Class<? extends MvpPresenter>) field.getType();
                    MvpPresenter presenter = clazz.newInstance();
                    field.setAccessible(true);
                    field.set(mView, presenter);
                    presenter.attachView(mView);
                    mPresenters.add(presenter);
                } catch (IllegalAccessException | InstantiationException | ClassCastException e) {
                    e.printStackTrace();
                    /**
                     * IllegalAccessException
                     * field.set：没有权限访问，请检查注解对象的修饰符
                     */
                    /**
                     * InstantiationException
                     * clazz.newInstance：检查一下注解的对象有没有空的构造函数
                     */
                    /**
                     * ClassCastException
                     * clazz.newInstance：检查一下自己注解的对象类型是否正确
                     * field.set：检查一下自己的 V 层（Activity 或 Fragment）有没有实现 P 层对应的接口
                     */
                    throw new IllegalStateException("are you ok?");
                }
            }
        }
    }

    @Override
    public void unbindPresenter() {
        // 一定要解绑
        for (MvpPresenter presenter : mPresenters) {
            presenter.detachView();
        }
        mPresenters.clear();
        mPresenters = null;
        mView = null;
    }
}
