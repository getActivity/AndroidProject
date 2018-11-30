package com.hjq.demo.mvp;

import com.hjq.demo.base.MyLazyFragment;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : MVP 懒加载 Fragment 基类
 */
public abstract class MvpLazyFragment<P extends MvpPresenter> extends MyLazyFragment {

    private P mPresenter;

    @Override
    protected void init() {
        mPresenter = initPresenter();
        mPresenter.attach(this);
        mPresenter.start();
        super.init();
    }

    @Override
    public void onDestroy() {
        mPresenter.detach();
        super.onDestroy();
    }

    public P getPresenter() {
        return mPresenter;
    }

    protected abstract P initPresenter();
}