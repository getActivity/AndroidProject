package com.hjq.demo.mvp;

import com.hjq.demo.common.MyActivity;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : MVP Activity 基类
 */
public abstract class MvpActivity<P extends MvpPresenter> extends MyActivity implements IMvpView {

    private P mPresenter;

    @Override
    public void initActivity() {
        mPresenter = createPresenter();
        mPresenter.attach(this);
        mPresenter.start();
        super.initActivity();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detach();
        super.onDestroy();
    }

    public P getPresenter() {
        return mPresenter;
    }

    protected abstract P createPresenter();
}