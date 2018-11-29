package com.hjq.demo.mvp;

import com.hjq.demo.common.CommonActivity;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : MVP Activity 基类
 */
public abstract class MvpActivity<P extends MvpPresenter> extends CommonActivity {

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