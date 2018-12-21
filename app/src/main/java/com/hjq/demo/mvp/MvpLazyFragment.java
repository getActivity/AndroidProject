package com.hjq.demo.mvp;

import com.hjq.demo.common.MyLazyFragment;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : MVP 懒加载 Fragment 基类
 */
public abstract class MvpLazyFragment<P extends MvpPresenter> extends MyLazyFragment implements IMvpView {

    private P mPresenter;

    @Override
    protected void initFragment() {
        mPresenter = createPresenter();
        mPresenter.attach(this);
        mPresenter.start();
        super.initFragment();
    }

    @Override
    public void onDestroy() {
        mPresenter.detach();
        super.onDestroy();
    }

    public P getPresenter() {
        return mPresenter;
    }

    protected abstract P createPresenter();
}