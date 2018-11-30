package com.hjq.demo.mvp;

import com.hjq.demo.base.MyActivity;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : MVP Activity 基类
 */
public abstract class MvpActivity<P extends MvpPresenter> extends MyActivity {

    private P mPresenter;

    @Override
    public void init() {
        mPresenter = initPresenter();
        mPresenter.attach(this);
        mPresenter.start();
        super.init();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detach();
        super.onDestroy();
    }

    public P getPresenter() {
        return mPresenter;
    }

    protected abstract P initPresenter();
}